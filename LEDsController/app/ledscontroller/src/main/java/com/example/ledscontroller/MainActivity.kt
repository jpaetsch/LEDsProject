package com.example.ledscontroller

import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ledscontroller.models.viewmodels.ReceiverDevice
import com.example.ledscontroller.repositories.nsd.LEDsDiscovery
import com.example.ledscontroller.ui.theme.LEDsControllerAppTheme
import com.example.ledscontroller.utils.Tags


class MainActivity : ComponentActivity() {

    // TODO fix up this section - is device options necessary?
    // TODO get the composable stuff working and then add state for connection after...

    private var deviceOptions = ArrayList<ReceiverDevice>()

    private val ledsServiceDiscovery: LEDsDiscovery? = object : LEDsDiscovery(applicationContext) {
        override fun onNsdServiceResolved(serviceInfo: NsdServiceInfo) {
            val serviceDeviceAdded = ReceiverDevice(serviceInfo.serviceName, serviceInfo.host.hostAddress?:"")
            if (!deviceOptions.contains(serviceDeviceAdded)) {
                deviceOptions.add(serviceDeviceAdded)
            }
        }
        override fun onNsdServiceLost(serviceInfo: NsdServiceInfo) {
            val serviceDeviceLost = ReceiverDevice(serviceInfo.serviceName, serviceInfo.host.hostAddress?:"")
            if (deviceOptions.contains(serviceDeviceLost)) {
                deviceOptions.remove(serviceDeviceLost)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ledsServiceDiscovery?.initializeListeners()
        ledsServiceDiscovery?.discoverServices()

        setContent {
            LEDsControllerAppTheme {
                MyApp(modifier = Modifier.fillMaxSize())
//                ReceiverDeviceList(deviceOptions)
            }
        }
    }

    override fun onPause() {
        ledsServiceDiscovery?.stopDiscovery()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        ledsServiceDiscovery?.discoverServices()
    }

    override fun onDestroy() {
        ledsServiceDiscovery?.stopDiscovery()
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String) {

    Surface(
        color = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text = "Hello $name!",
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
private fun MyApp(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Greeting("Jacob")
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    LEDsControllerAppTheme {
        MyApp()
    }
}



@Composable
fun ReceiverDeviceList(receivers: List<ReceiverDevice>) {
     LazyColumn(
         modifier = Modifier.fillMaxWidth()
     ) {
        items(
            items = receivers,
//            key = { receiver ->
//                receiver.id
//            }
        ) { receiver ->
            ReceiverDeviceCard(receiver)
        }
    }
}

@Composable
fun ReceiverDeviceCard(receiver: ReceiverDevice) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column() {

            }
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
}