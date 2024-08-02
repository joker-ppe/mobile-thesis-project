package com.eddiez.plantirrigsys.utilities

import android.util.Log
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope

class RabbitMqClient private constructor() {
    private val host = "54.169.246.109"
    private val username = "android"
    private val password = "android123@"

    private var connection: Connection? = null
    private var channel: Channel? = null
    private val consumerTags = mutableMapOf<String, String>()

    companion object {
        @Volatile
        private var instance: RabbitMqClient? = null

        fun getInstance(): RabbitMqClient {
            return instance ?: synchronized(this) {
                instance ?: RabbitMqClient().also { instance = it }
            }
        }
    }

    fun connect() {
        try {
            if (connection?.isOpen == true) {
                Log.d("RabbitMqClient", "Already connected to RabbitMQ")
                return
            }

            val factory = ConnectionFactory()
            factory.host = host
            factory.username = username
            factory.password = password

            connection = factory.newConnection()

            if (connection?.isOpen == true) {
                Log.d("RabbitMqClient", "Connected to RabbitMQ")
                channel = connection?.createChannel()
                if (channel?.isOpen == true) {
                    Log.d("RabbitMqClient", "Channel created")
                } else {
                    Log.d("RabbitMqClient", "Failed to create channel")
                }
            } else {
                Log.d("RabbitMqClient", "Failed to connect to RabbitMQ")
            }
        } catch (e: Exception) {
            Log.e("RabbitMqClient", "Failed to connect to RabbitMQ", e)
        }
    }

    fun sendMessage(exchangeName: String, message: String) {
        if (channel?.isOpen == false) {
            Log.d("RabbitMqClient", "Channel is closed")
            return
        }
        try {
            channel?.exchangeDeclare(exchangeName, "fanout", false, true, null)
            channel?.basicPublish(exchangeName, "", null, message.toByteArray())
            Log.d("RabbitMqClient", "Message published to exchange '$exchangeName'")
        } catch (e: Exception) {
            Log.e("RabbitMqClient", "Failed to publish message to exchange $exchangeName", e)
        }
    }

    fun consumeMessage(exchangeName: String, queueName: String, listener: OnMessageReceived) {
        if (channel?.isOpen == false) {
            Log.d("RabbitMqClient", "Channel is closed")
            return
        }
        try {
            channel?.exchangeDeclare(exchangeName, "fanout", false, true, null)
            channel?.queueDeclare(queueName, false, false, true, null)
            channel?.queueBind(queueName, exchangeName, "")

            val consumer = object : DefaultConsumer(channel) {
                override fun handleDelivery(
                    consumerTag: String,
                    envelope: Envelope,
                    properties: AMQP.BasicProperties,
                    body: ByteArray
                ) {
                    val message = String(body, charset("UTF-8"))
                    listener.onMessageReceived(message)
                }
            }

            val tag = channel?.basicConsume(queueName, true, consumer)
            tag?.let { consumerTags[queueName] = it }
            Log.d("RabbitMqClient", "Started consuming messages from exchange '$exchangeName' queue '$queueName'")
        } catch (e: Exception) {
            Log.e("RabbitMqClient", "Failed to consume messages from exchange '$exchangeName' queue $queueName", e)
        }
    }

    fun stopConsumingAll() {
        try {
            consumerTags.forEach { (_, tag) ->
                channel?.basicCancel(tag)
                Log.d("RabbitMqClient", "Stopped consuming messages for tag $tag")
            }
            consumerTags.clear()
        } catch (e: Exception) {
            Log.e("RabbitMqClient", "Failed to stop consuming messages", e)
        }
    }

    fun stopConsuming(queueName: String) {
        try {
            consumerTags[queueName]?.let {
                channel?.basicCancel(it)
                consumerTags.remove(queueName)
                Log.d("RabbitMqClient", "Stopped consuming messages for queue $queueName")
            }
        } catch (e: Exception) {
            Log.e("RabbitMqClient", "Failed to stop consuming messages for queue $queueName", e)
        }
    }

    fun close() {
        try {
            channel?.close()
            connection?.close()
            Log.d("RabbitMqClient", "Closed connection and channel")
        } catch (e: Exception) {
            Log.e("RabbitMqClient", "Failed to close connection and channel", e)
        }
    }
}