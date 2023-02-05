package com.example.ledscontroller.repositories.nsd

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.example.ledscontroller.utils.Tags
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList


/**
 * Network service discovery wrapper for finding LED Receiver servers
 * on the current WiFi network. Utilizes thread-safe queues and behaviour to avoid
 * listener issues and other problems with the current official Android NSD API examples
 */
abstract class LEDsDiscovery(val context: Context) {

    val nsdManager: NsdManager? = context.getSystemService(Context.NSD_SERVICE) as NsdManager?
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private var resolveListener: NsdManager.ResolveListener? = null
    private var resolveListenerBusy = AtomicBoolean(false)
    private var pendingNsdServices = ConcurrentLinkedQueue<NsdServiceInfo>()
    var resolvedNsdServices: MutableList<NsdServiceInfo> = Collections.synchronizedList(ArrayList<NsdServiceInfo>())

    companion object {
        const val SERVICE_TYPE = "_http._tcp"
        const val SERVICE_NAME = "LEDsReceiver-"
    }

    fun initializeListeners() {
        initializeResolveListener()
    }

    private fun initializeDiscoveryListener() {

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {
                Log.d(Tags.nsdDiscoveryListener, "NSD started: $regType")
            }
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(Tags.nsdDiscoveryListener, "NSD ($serviceType) start failed with error code: $errorCode")
                stopDiscovery()
            }
            override fun onDiscoveryStopped(serviceType: String) {
                Log.d(Tags.nsdDiscoveryListener, "NSD ($serviceType) stopped")
            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(Tags.nsdDiscoveryListener, "NSD ($serviceType) stop failed with error code: $errorCode")
                nsdManager?.stopServiceDiscovery(this)
            }
            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d(Tags.nsdDiscoveryListener, "Potential service found: $service")
                if (service.serviceName.startsWith(SERVICE_NAME) &&
                    service.serviceType == SERVICE_TYPE) {
                    if (resolveListenerBusy.compareAndSet(false, true)) {
                        nsdManager?.resolveService(service, resolveListener)
                    } else {
                        pendingNsdServices.add(service)
                    }
                } else {
                    Log.d(Tags.nsdDiscoveryListener, "Undesired service (${service.serviceName}, ${service.serviceType}) filtered out")
                }
            }
            override fun onServiceLost(service: NsdServiceInfo) {
                Log.e(Tags.nsdDiscoveryListener, "Service lost: $service")
                var iterator = pendingNsdServices.iterator()
                while (iterator.hasNext()) {
                    if (iterator.next().serviceName == service.serviceName) {
                        iterator.remove()
                    }
                }
                synchronized(resolvedNsdServices) {
                    iterator = resolvedNsdServices.iterator()
                    while (iterator.hasNext()) {
                        if (iterator.next().serviceName == service.serviceName) {
                            iterator.remove()
                        }
                    }
                }
                onNsdServiceLost(service)
            }
        }
    }

    private fun initializeResolveListener() {
        resolveListener = object : NsdManager.ResolveListener {
            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Log.d(Tags.nsdResolveListener, "NSD service ($serviceInfo) resolve succeeded")
                resolvedNsdServices.add(serviceInfo)
                onNsdServiceResolved(serviceInfo)
                resolveNextInQueue()
            }

            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.d(Tags.nsdResolveListener, "NSD service ($serviceInfo) resolve failed with error code: $errorCode")
                resolveNextInQueue()
            }
        }
    }

    fun discoverServices() {
        stopDiscovery()
        initializeDiscoveryListener()
        nsdManager?.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stopDiscovery() {
        if (discoveryListener != null) {
            try {
                nsdManager?.stopServiceDiscovery(discoveryListener)
            } catch (ex: Exception) {
                Log.e(Tags.nsdDiscoveryListener, "Service stop discovery exception thrown", ex)
            }
            discoveryListener = null
        }
    }

    private fun resolveNextInQueue() {
        val nextNsdService = pendingNsdServices.poll()
        if (nextNsdService != null) {
            nsdManager?.resolveService(nextNsdService, resolveListener)
        } else {
            resolveListenerBusy.set(false)
        }
    }

    abstract fun onNsdServiceResolved(serviceInfo: NsdServiceInfo)

    abstract fun onNsdServiceLost(serviceInfo: NsdServiceInfo)
}