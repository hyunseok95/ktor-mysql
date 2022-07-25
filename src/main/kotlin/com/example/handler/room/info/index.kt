package com.example.handler.room.info

import com.example.controller.room.RoomController.getRoomInfo
import com.example.lib.api.ErrorCode
import com.example.lib.api.callError
import com.example.lib.database.tryGetProps
import com.example.model.host.HostTable
import com.example.model.review.ReviewTable
import com.example.model.room.RoomTable
import com.example.model.room.option.RoomOptionTable
import com.example.model.user.UserTable
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.avg
import org.jetbrains.exposed.sql.count

@Serializable
@Resource("/room/info")
data class RoomInfo(
    val roomId: Int,
) {
    companion object{
        @Serializable
        @Resource("")
        data class Host(
            val nickname: String?,
            val profile: String?,
        )
        @Serializable
        @Resource("")
        data class Room(
            val type: String?,
            val name: String?,
            val introduction: String?,
            val guest: Int?,
            val location: String?,
            val image_directory: String?,
        )
        @Serializable
        @Resource("")
        data class RoomOption(
            val bedroom: Int?,
            val bed: Int?,
            val bathroom: Int?,
            val avg_star: Double?,
            val count_comment: Long?,
        )
        @Serializable
        @Resource("")
        data class Review(
            val name: String?,
            val profile: String?,
            val star: Byte?,
            val comment: String?,
            val create_at: String?,
        )

        val ofHost: (ResultRow)-> Host = {
            Host(
                nickname = it.tryGetProps(HostTable.nickname),
                profile = it.tryGetProps(UserTable.profile)
            )
        }
        val ofRoom: (ResultRow)-> Room = {
            Room(
                type = it.tryGetProps(RoomTable.type),
                name =it.tryGetProps(RoomTable.name),
                introduction= it.tryGetProps(RoomTable.introduction),
                guest= it.tryGetProps(RoomTable.guest),
                location=  it.tryGetProps(RoomTable.location),
                image_directory= it.tryGetProps(RoomTable.image_directory),
            )
        }
        val ofRoomOption: (ResultRow)-> RoomOption = {
            RoomOption(
                bedroom = it.tryGetProps(RoomOptionTable.bedroom),
                bed = it.tryGetProps(RoomOptionTable.bed),
                bathroom = it.tryGetProps(RoomOptionTable.bathroom),
                avg_star = it.tryGetProps(ReviewTable.star.avg(3))?.toDouble(),
                count_comment = it.tryGetProps(ReviewTable.comment.count()),
            )
        }
        val ofReview: (ResultRow)-> Review = {
            Review(
                name = it.tryGetProps(UserTable.name),
                profile = it.tryGetProps(UserTable.profile),
                star = it.tryGetProps(ReviewTable.star),
                comment = it.tryGetProps(ReviewTable.comment),
                create_at = it.tryGetProps(ReviewTable.create_at)?.toString(),
            )
        }

        @Serializable
        @Resource("")
        data class Out (
            val host: Host,
            val room: Room,
            val roomOption: RoomOption,
            val review: List<Review>,
        ){
            override fun equals(other: Any?): Boolean {
                return super.equals(other)
            }

            override fun hashCode(): Int {
                return super.hashCode()
            }
        }
    }
}

fun Route.roomInfo(){
    route(""){
        get{
            val queryParams = call.request.queryParameters
            val roomId = queryParams["roomId"]?: return@get call.respond(
                HttpStatusCode.BadRequest, callError(
                    ErrorCode.NO_RESOURCES.name,
                    "roomId",
                    "queryParams",
                    "does not exist",
                    "",
                )
            )

            getRoomInfo(roomId.toInt().also {

            }).run {
                val triple = this.first?: return@get (call.respond(
                    HttpStatusCode.NotFound, callError(
                        ErrorCode.NO_RESOURCES.name,
                        "room",
                        "server",
                        "does not exist",
                        "",
                    )
                ))
                return@get call.respond(HttpStatusCode.Accepted,
                    RoomInfo.Companion.Out(
                        triple.first, triple.second, triple.third, this.second
                    )
                )
            }
        }
    }
}
