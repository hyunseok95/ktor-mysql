package com.example.controller.room

import com.example.handler.room.Room
import com.example.handler.room.info.RoomInfo
import com.example.lib.database.query
import com.example.model.host.HostTable
import com.example.model.review.ReviewTable
import com.example.model.room.RoomTable
import com.example.model.room.option.RoomOptionTable
import com.example.model.user.UserTable
import org.jetbrains.exposed.sql.*


object RoomController {
    suspend fun getRoomList(room: Room) = query {
        RoomTable.join(
            RoomOptionTable,
            JoinType.INNER,
            RoomTable.id,
            RoomOptionTable.room_id
        ).join(
            ReviewTable,
            JoinType.INNER,
            RoomTable.id,
            ReviewTable.room_id
        ).slice(
            RoomTable.id,
            RoomTable.image_directory,
            RoomTable.location.substring(6,7),
            RoomTable.latitude,
            RoomTable.longitude,
            RoomTable.type,
            RoomTable.name,
            RoomTable.guest,
            RoomOptionTable.bedroom,
            RoomOptionTable.bed,
            RoomOptionTable.bathroom,
            RoomOptionTable.is_parking,
            RoomOptionTable.is_wifi,
            ReviewTable.star.avg(),
            ReviewTable.comment.count()
        ).selectAll().limit(10,(room.page-1)*10)
            .andWhere { RoomTable.latitude greater room.latitude - 0.04 }
            .andWhere { RoomTable.longitude greater room.longitude - 0.04 }
            .andWhere { RoomTable.latitude less room.latitude + 0.04 }
            .andWhere { RoomTable.longitude less room.longitude + 0.04 }
            .groupBy(RoomTable.id)
            .map {
                Room.of(it) }
    }

    suspend fun getRoomInfo(roomId: Int) = query {
        val triple = RoomTable.join(
            HostTable,
            JoinType.INNER,
            RoomTable.host_id,
            HostTable.id
        ){
            RoomTable.id eq roomId
        }.join(
            UserTable,
            JoinType.INNER,
            HostTable.user_id,
            UserTable.id
        ){
            RoomTable.id eq roomId
        }.join(
            RoomOptionTable,
            JoinType.INNER,
            RoomTable.id,
            RoomOptionTable.room_id
        ){
            RoomTable.id eq roomId
        }.join(
            ReviewTable,
            JoinType.INNER,
            RoomTable.id,
            ReviewTable.room_id
        ){
            RoomTable.id eq roomId
        }.slice(
            /** 유저 */
            UserTable.profile,
            /** 호스트 */
            HostTable.nickname,
            /** 룸 */
            RoomTable.type,
            RoomTable.name,
            RoomTable.introduction,
            RoomTable.guest,
            RoomTable.location,
            RoomTable.image_directory,
            /** 룸 옵션 */
            RoomOptionTable.bedroom,
            RoomOptionTable.bed,
            RoomOptionTable.bathroom,
            ReviewTable.star.avg(3),
            ReviewTable.comment.count()
        ).selectAll()
            .andWhere {
                RoomTable.id eq roomId
            }
            .groupBy(RoomTable.id)
            .map {
                Triple(
                    RoomInfo.ofHost(it),
                    RoomInfo.ofRoom(it),
                    RoomInfo.ofRoomOption(it)
                )
            }.firstOrNull()

        val arrayReview = ReviewTable.join(
            UserTable,
            JoinType.INNER,
            ReviewTable.user_id,
            UserTable.id
        ){
            ReviewTable.room_id eq roomId
        }.slice(
            UserTable.name,
            UserTable.profile,
            ReviewTable.star,
            ReviewTable.comment,
            ReviewTable.create_at,
        ).selectAll().andWhere {
            ReviewTable.room_id eq roomId
        }.map { RoomInfo.ofReview(it) }

        return@query triple to arrayReview
    }

}



