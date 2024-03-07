package com.eddiez.plantirrigsys.utilities

import android.util.Log
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope

class RabbitMqClient {
    private val host = "54.169.246.109"
    private val username = "android"
    private val password = "android123@"

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

    fun sendMessage(exchangeName: String, message: String) {
        if (channel?.isOpen == false) {
            Log.d("RabbitMqClient", "Channel is closed")
            return
        }
        try {
            // Declare an exchange
            channel?.exchangeDeclare(exchangeName, "fanout", false, true, null)
            // Publish a message to the exchange
            channel?.basicPublish(exchangeName, "", null, message.toByteArray())
            Log.d("RabbitMqClient", "Message published to exchange '$exchangeName'")
        } catch (e: Exception) {
            Log.e("RabbitMqClient", "Failed to publish message to exchange $exchangeName", e)
        }
    }

    fun consumeMessage(exchangeName: String,queueName: String, listener: OnMessageReceived) {
        if (channel?.isOpen == false) {
            Log.d("RabbitMqClient", "Channel is closed")
            return
        }
        try {
            // Declare an exchange
            channel?.exchangeDeclare(exchangeName, "fanout", false, true, null)
            // Declare a queue
            channel?.queueDeclare(queueName, false, false, true, null)

            // Bind the queue to the exchange
            channel?.queueBind(queueName, exchangeName, "")

            // Create a consumer
            val consumer = object : DefaultConsumer(channel) {
                override fun handleDelivery(
                    consumerTag: String,
                    envelope: Envelope,
                    properties: AMQP.BasicProperties,
                    body: ByteArray
                ) {
                    val message = String(body, charset("UTF-8"))
                    listener.onMessageReceived(message)
//                    Log.d("RabbitMqClient", "Received message: '$message'")
                }
            }

            // Start consuming messages
            channel?.basicConsume(queueName, true, consumer)
            Log.d("RabbitMqClient", "Started consuming messages from exchange '$exchangeName' queue '$queueName'")

            // Keep the connection open until the application finishes
            while (true) { }
        } catch (e: Exception) {
            Log.e("RabbitMqClient", "Failed to consume messages from exchange '$exchangeName' queue $queueName", e)
        }
    }

    fun close() {
        channel?.close()
        connection?.close()
        Log.d("RabbitMqClient", "Closed connection and channel")
    }
}