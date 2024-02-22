package com.eddiez.plantirrigsys.dataModel

import java.io.Serializable

data class ScheduleDataModel(
    var id: Int? = null,
    var title: String? = null,
    val description: String? = null,
    val plantName: String? = null,
    var imageData: String? = null,
    val numberOfViews: Int? = null,
    val numberOfCopies: Int? = null,
    val numberOfDates: Int? = null,
    var isPublic: Boolean? = null,
    val createAt: String? = null,
//    val updateAt: String? = null,
    val updateContentAt: String? = null,
    val slots: List<SlotDataModel>? = null,
    val userId: Int? = null,
    val user: UserDataModel? = null,
    var longitude: Double? = null,
    var latitude: Double? = null,

    val moistureThreshold: Float? = null,
    val temperatureThreshold: Float? = null,
    val ecThreshold: Float? = null,
    val pHThreshold: Float? = null,
    val nThreshold: Float? = null,
    val pThreshold: Float? = null,
    val kThreshold: Float? = null,
) : Serializable {
    override fun toString(): String {
        return "ScheduleDataModel(id=$id, title=$title, description=$description, plantName=$plantName, numberOfViews=$numberOfViews, numberOfCopies=$numberOfCopies, numberOfDates=$numberOfDates, isPublic=$isPublic, createAt=$createAt, updateContentAt=$updateContentAt, slots=$slots, moistureThreshold=$moistureThreshold, temperatureThreshold=$temperatureThreshold, ecThreshold=$ecThreshold, pHThreshold=$pHThreshold, nThreshold=$nThreshold, pThreshold=$pThreshold, kThreshold=$kThreshold)"
    }
}
