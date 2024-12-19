import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import kotlinx.serialization.json.*
import kotlinx.serialization.*

class MQTTClient(context: Context, serverURI: String, clientId: String) {

    private val mqttClient = MqttAndroidClient(context, serverURI, clientId)
    private val TAG = "MQTTClient"
    private var callback: ((String) -> Unit)? = null

    fun setCallback(cb: (String) -> Unit) {
        callback = cb
    }

    fun connect(userName: String, password: String) {
        val options = MqttConnectOptions()
        options.userName = userName
        options.password = password.toCharArray()


        try {
            mqttClient.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.d(TAG, "Connection lost ${cause.toString()}")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = String(message!!.payload)
                    Log.d(TAG, "Message arrived: $msg from topic: $topic")
                    if (message != null) {
                        try {
                            val jsonObject = Json.parseToJsonElement(msg).jsonObject
                            if (validateMessage(jsonObject)) {
                                callback?.invoke(msg)
                            } else {
                                Log.e(TAG, "Mensaje JSON inválido")
                            }

                        } catch (e: SerializationException) {
                            Log.e(TAG, "Error al parsear JSON: ${e.message}")
                        }
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(TAG, "Delivery complete")
                }
            })
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Connection success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure: ${exception.toString()}")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String) {
        try {
            mqttClient.subscribe(topic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Subscribed to topic: $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to subscribe to topic: $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publishMessage(topic: String, message: String) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttClient.publish(topic, mqttMessage, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Mensaje publicado")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Fallo al publicar ${exception.toString()}")
                }

            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        mqttClient.disconnect()
    }

    private fun validateMessage(jsonObject: JsonObject): Boolean {

        return jsonObject.containsKey("campo1") && jsonObject.containsKey("campo2")

    }
}