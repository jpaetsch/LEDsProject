package com.example.ledscontroller
//
//import android.content.Context
//import android.net.nsd.NsdManager
//import android.net.nsd.NsdServiceInfo
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.ledscontroller.models.viewmodels.ReceiverDevice
//import com.example.ledscontroller.utils.LEDsReceiver
//import com.example.ledscontroller.utils.Tags
//import java.net.InetAddress
//
//
//class ConnectToReceiverActivity : AppCompatActivity() {
//
//    private val SERVICE_TYPE = "_http._tcp"
//    lateinit var nsdManager: NsdManager
//    private var deviceOptions = ArrayList<ReceiverDevice>()
//
//    private val discoveryListener = object : NsdManager.DiscoveryListener {
//
//        override fun onDiscoveryStarted(regType: String) {
//            Log.i(Tags.connectActivity, "Service discovery started")
//        }
//
//        override fun onDiscoveryStopped(serviceType: String) {
//            Log.i(Tags.connectActivity, "Discovery stopped: $serviceType")
//        }
//
//        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
//            Log.e(Tags.connectActivity, "Discovery start failed with error code: $errorCode")
//            nsdManager.stopServiceDiscovery(this)
//        }
//
//        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
//            Log.e(Tags.connectActivity, "Discovery stop failed with error code: $errorCode")
//            nsdManager.stopServiceDiscovery(this)
//        }
//
//        override fun onServiceFound(service: NsdServiceInfo) {
//            Log.i(Tags.connectActivity, "Service found: $service")
//            when {
//                service.serviceName.contains("LEDsReceiver") &&
//                service.serviceType == SERVICE_TYPE ->
//                    nsdManager.resolveService(service, resolveListener)
//            }
//        }
//
//        override fun onServiceLost(service: NsdServiceInfo) {
//            Log.e(Tags.connectActivity, "Service lost: $service")
//        }
//    }
//
//    private val resolveListener = object : NsdManager.ResolveListener {
//        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
//            Log.e(Tags.connectActivity, "Resolve failed with error code: $errorCode")
//        }
//
//        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
//            Log.i(Tags.connectActivity, "Resolve succeeded: $serviceInfo")
//            val newDevice = ReceiverDevice(serviceInfo.serviceName, serviceInfo.host.hostAddress ?: "")
//            if (!deviceOptions.contains(newDevice)) {
//                deviceOptions.add(newDevice)
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager
//        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
//        setContentView(R.layout.activity_connect_to_receiver)
//    }
//
//    override fun onPause() {
//        nsdManager.stopServiceDiscovery(discoveryListener)
//        super.onPause()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
//    }
//
//    override fun onDestroy() {
//        nsdManager.stopServiceDiscovery(discoveryListener)
//        super.onDestroy()
//    }
//}
//
//@Composable
//fun ReceiverDeviceList(receivers: List<ReceiverDevice>) {
//    LazyColumn {
//        items(receivers) { receiver ->
//            ReceiverDeviceCard(receiver)
//        }
//    }
//}
//
//@Composable
//fun ReceiverDeviceCard(receiver: ReceiverDevice) {
//    Row(verticalAlignment = Alignment.CenterVertically) {
//        Image(
//            painterResource(id = R.drawable.ic_baseline_device_unknown_24),
//            contentDescription = "Unknown device image"
//        )
//        Column {
//            Text(receiver.name)
//            Text(receiver.ip)
//        }
//    }
//}