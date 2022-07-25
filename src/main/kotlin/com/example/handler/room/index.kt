package com.example.handler.room

import com.example.controller.room.RoomController
import com.example.handler.room.info.roomInfo
import com.example.lib.api.ErrorCode
import com.example.lib.api.callError
import com.example.lib.database.tryGetProps
import com.example.model.review.ReviewTable
import com.example.model.room.RoomTable
import com.example.model.room.option.RoomOptionTable
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.avg
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.substring


@Serializable
@Resource("/room")
data class Room(
    val latitude: Double,
    val longitude: Double,
    val page: Long
) {
    companion object{
        @Serializable
        @Resource("")
        data class Out (
            val id: Int,
            val image_directory: String?,
            val location: String?,
            val latitude: Double?,
            val longitude: Double?,
            val type: String?,
            val name: String?,
            val guest: Int?,
            val bedroom: Int?,
            val bed: Int?,
            val bathroom: Int?,
            val is_parking: Byte?,
            val is_wifi: Byte?,
            val avg_star: Double?,
            val count_comment: Long?,
        )
        val of: (ResultRow)-> Out = {
            Out(
                id = it.tryGetProps(RoomTable.id)!!.value,
                image_directory = it.tryGetProps(RoomTable.image_directory),
                location = it.tryGetProps(RoomTable.location.substring(6,7)),
                latitude = it.tryGetProps(RoomTable.latitude)?.toDouble(),
                longitude = it.tryGetProps(RoomTable.longitude)?.toDouble(),
                type = it.tryGetProps(RoomTable.type),
                name = it.tryGetProps(RoomTable.name),
                guest = it.tryGetProps(RoomTable.guest),
                bedroom = it.tryGetProps(RoomOptionTable.bedroom),
                bed = it.tryGetProps(RoomOptionTable.bed),
                bathroom = it.tryGetProps(RoomOptionTable.bathroom),
                is_parking = it.tryGetProps(RoomOptionTable.is_parking),
                is_wifi = it.tryGetProps(RoomOptionTable.is_wifi),
                avg_star = it.tryGetProps(ReviewTable.star.avg())?.toDouble(),
                count_comment = it.tryGetProps(ReviewTable.comment.count()),
            )
        }
    }
}

fun Route.room(){
    route("") {
        get{
            val params = call.request.queryParameters
            val latitude = params["latitude"]?: return@get call.respond(
                HttpStatusCode.BadRequest, callError(
                    ErrorCode.NO_RESOURCES.name,
                    "latitude",
                    "params",
                    "does not exist",
                    "",
                )
            )
            val longitude = params["longitude"]?: return@get call.respond(
                HttpStatusCode.BadRequest, callError(
                    ErrorCode.NO_RESOURCES.name,
                    "longitude",
                    "params",
                    "does not exist",
                    "",
                )
            )
            val page = params["page"]?: return@get call.respond(
                HttpStatusCode.BadRequest, callError(
                    ErrorCode.NO_RESOURCES.name,
                    "page",
                    "params",
                    "does not exist",
                    "",
                )
            )
            val room = Room(
                latitude.toDouble(),
                longitude.toDouble(),
                page.toLong()
            )
            val roomList = RoomController.getRoomList(room)

            if (roomList.isNotEmpty()){
                return@get call.respond(HttpStatusCode.Accepted, roomList)
            }else{
                return@get call.respond(
                    HttpStatusCode.NotFound, callError(
                        ErrorCode.NO_RESOURCES.name,
                        "roomList",
                        "server",
                        "does not exist",
                        "''",
                    )
                )
            }
        }
        route("/info"){
            roomInfo()
        }
    }
}



