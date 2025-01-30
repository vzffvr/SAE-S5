package com.example.orchestrion

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.MqttTopic
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.nio.charset.Charset

class MqttClientManager(
    private val serverUri: String,
    context: Context
) {
    private var mqttClient: MqttClient =
        MqttClient(serverUri, MqttClient.generateClientId(), MemoryPersistence())
    var connected = false

    init {
        reconnectToMqttBroker(context)
    }

    private fun connect() {
        try {
            mqttClient = MqttClient(serverUri, MqttClient.generateClientId(), MemoryPersistence())
            val options = MqttConnectOptions()
            options.isAutomaticReconnect = true
            mqttClient.connect(options)


            mqttClient.setCallback(object : MqttCallback {

                override fun connectionLost(cause: Throwable?) {
                    try {
                        mqttClient.connect(options)
                    } catch (e: Exception) {
                        connected = mqttClient.isConnected
                    }
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val receivedMessage = message?.toString()
                    if (!receivedMessage.isNullOrEmpty()) {
                        val utf8String = receivedMessage.toByteArray(Charset.forName("UTF-8"))
                        val convertedString = String(utf8String, Charset.forName("UTF-8"))
                    }
                }


                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })


        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }


    fun publish(topic: String, message: String) {
        try {
            val mqttTopic: MqttTopic = mqttClient.getTopic(topic)
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttTopic.publish(mqttMessage)
        } catch (e: MqttException) {
            e.printStackTrace()
            connected = mqttClient.isConnected
        }

    }


    fun reconnectToMqttBroker(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            if (mqttClient.isConnected) {
//                Toast.makeText(context, "Already connected to MQTT broker", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Set up MQTT connection options (customize as needed)
            val options = MqttConnectOptions()
            options.isAutomaticReconnect = true
            // ... configure username, password, keepAliveInterval, etc. ...

            try {
                withContext(Dispatchers.IO) { // Perform connection on a background thread
                    mqttClient.connect()
                }
//                Toast.makeText(context, "Connected to MQTT broker", Toast.LENGTH_SHORT).show()
                // Perform actions after successful reconnection
            } catch (e: MqttException) {
//                Toast.makeText(context, "Not Connected to MQTT broker", Toast.LENGTH_SHORT).show()
                // Handle reconnection failure
            }
            connected = mqttClient.isConnected
        }
    }

    fun isConnected(): Boolean {
        return mqttClient.isConnected
    }

}