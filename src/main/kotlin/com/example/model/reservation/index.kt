package com.example.model.reservation

import com.example.model.room.RoomClass
import com.example.model.room.RoomTable
import com.example.model.user.UserClass
import com.example.model.user.UserTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date


object ReservationTable: IntIdTable("reservation"){
    val check_in_date = date("check_in_date")
    val check_out_date = date("check_out_date")
    val adult = short("adult")
    val child = short("child")
    val baby = short("baby")
    val room_id = reference("room_id", RoomTable).uniqueIndex()
    val user_id = reference("user_id", UserTable).uniqueIndex()
}

class ReservationClass(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<ReservationClass>(ReservationTable)
    var check_in_date by ReservationTable.check_in_date
    var check_out_date by ReservationTable.check_out_date
    var adult by ReservationTable.adult
    var child by ReservationTable.child
    var baby by ReservationTable.baby
    var room_id by RoomClass referencedOn ReservationTable.room_id
    var user_id by UserClass referencedOn ReservationTable.user_id
}

