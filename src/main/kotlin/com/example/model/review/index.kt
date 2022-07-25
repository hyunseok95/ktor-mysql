package com.example.model.review

import com.example.lib.database.registerTimestamp
import com.example.model.room.RoomClass
import com.example.model.room.RoomTable
import com.example.model.user.UserClass
import com.example.model.user.UserTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ReviewTable: IntIdTable("review"){
    val star = byte("star")
    val comment = text("comment", eagerLoading = false).clientDefault { "" }
    val create_at = registerTimestamp("create_at") // mysql 에서 수동으로 설정
    val room_id = reference("room_id", RoomTable)
    val user_id = reference("user_id", UserTable)
}

class ReviewClass(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<ReviewClass>(ReviewTable)
    var star by ReviewTable.star
    var comment by ReviewTable.comment
    var create_at by ReviewTable.create_at
    var room_id by RoomClass referencedOn ReviewTable.room_id
    var user_id by UserClass referencedOn ReviewTable.user_id
}
