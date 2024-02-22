package com.eddiez.plantirrigsys.utilities

import android.util.Log
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class RabbitMqClient {
    private val host = "54.169.246.109"
    private val username = "joker"
    private val password = "Joker@123$"

    private var connection: Connection? = null
    private var channel: Channel? = null


    fun connect() {
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
            // Handle connection error
            Log.d("RabbitMqClient", "Failed to connect to RabbitMQ")
        }
    }

    fun sendMessage(queueName: String, message: String) {
        if (channel?.isOpen == false) {
            Log.d("RabbitMqClient", "Channel is closed")
            return
        }
        try {
            // Declare a queue
            channel?.queueDeclare(queueName, false, false, false, null)

            // Publish a message to the queue
            channel?.basicPublish("", queueName, null, message.toByteArray())
            Log.d("RabbitMqClient", "Message published to queue '$queueName'")
        } catch (e: Exception) {
            Log.e("RabbitMqClient", "Failed to publish message to queue $queueName", e)
        }
    }

    fun close() {
        channel?.close()
        connection?.close()
        Log.d("RabbitMqClient", "Closed connection and channel")
    }
}