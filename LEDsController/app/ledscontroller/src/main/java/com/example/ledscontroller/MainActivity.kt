package com.example.ledscontroller

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.example.ledscontroller.models.viewmodels.ReceiverDevice
import com.example.ledscontroller.ui.theme.LEDsControllerAppTheme
import com.example.ledscontroller.utils.Tags


class MainActivity : ComponentActivity() {

    private val SERVICE_TYPE = "_http._tcp"
    private val nsdManager: NsdManager by lazy {
        (getSystemService(Context.NSD_SERVICE) as NsdManager)
    }
    private var deviceOptions = ArrayList<ReceiverDevice>()

    private val nsdDiscoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(regType: String) {
            Log.d(Tags.nsdDiscoveryListener, "Service discovery started")
        }
        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(Tags.nsdDiscoveryListener, "Discovery start failed with error code: $errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
        override fun onDiscoveryStopped(serviceType: String) {
            Log.d(Tags.nsdDiscoveryListener, "Discovery stopped: $serviceType")
        }
        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(Tags.nsdDiscoveryListener, "Discovery stop failed with error code: $errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
        override fun onServiceFound(service: NsdServiceInfo) {
            Log.d(Tags.nsdDiscoveryListener, "Service found: $service")
            when {
                service.serviceName.contains("LEDsReceiver") &&
                service.serviceType == SERVICE_TYPE ->
                    nsdManager.resolveService(service, nsdResolveListener)
            }
        }
        override fun onServiceLost(service: NsdServiceInfo) {
            Log.e(Tags.nsdDiscoveryListener, "Service lost: $service")
        }
    }

    private val nsdResolveListener = object : NsdManager.ResolveListener {
        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            Log.d(Tags.nsdResolveListener, "Service resolve succeeded: $serviceInfo")
            val newDevice = ReceiverDevice(serviceInfo.serviceName, serviceInfo.host.hostAddress ?: "")
            if (!deviceOptions.contains(newDevice)) {
                deviceOptions.add(newDevice)
            }
        }
        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            Log.e(Tags.nsdResolveListener, "Resolve failed with error code: $errorCode")
        }
    }

//    private val nsdRegistrationListener = object : NsdManager.RegistrationListener {
//        override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
//            Log.d(Tags.nsdRegistrationListener, "Service registration succeeded: $serviceInfo")
//        }
//        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
//            Log.d(Tags.nsdRegistrationListener, "Service registration failed: $serviceInfo")
//        }
//        override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
//            Log.d(Tags.nsdRegistrationListener, "Service unregistered: $serviceInfo")
//        }
//        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
//            Log.d(Tags.nsdRegistrationListener, "Service unregister failed: $serviceInfo")
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, nsdDiscoveryListener)

        setContent {
            LEDsControllerAppTheme {
                ReceiverDeviceList(deviceOptions)
            }
        }
    }

    override fun onPause() {
//        nsdManager.stopServiceDiscovery(nsdDiscoveryListener)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
//        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, nsdDiscoveryListener)
    }

    override fun onDestroy() {
        nsdManager.stopServiceDiscovery(nsdDiscoveryListener)
        super.onDestroy()
    }

    @Composable
    fun ScanForDevicesButton() {
        ReceiverDeviceList(deviceOptions)
//        Button(onClick = {nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, nsdDiscoveryListener)}) {
//
//        }
    }
}



@Composable
fun ReceiverDeviceList(receivers: List<ReceiverDevice>) {
    LazyColumn {
        items(receivers) { receiver ->
            ReceiverDeviceCard(receiver)
        }
    }
}

@Composable
fun ReceiverDeviceCard(receiver: ReceiverDevice) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painterResource(id = R.drawable.ic_baseline_device_unknown_24),
            contentDescription = "Unknown device image"
        )
        Column {
            Text(receiver.name)
            Text(receiver.ip)
        }
    }
}