package com.example.model.room.option

import com.example.model.room.RoomClass
import com.example.model.room.RoomTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object RoomOptionTable: IntIdTable("room_option"){
    val bedroom = integer("bedroom").default(1)
    val bed = integer("bed").default(0)
    val bathroom = integer("bathroom").default(0)
    val check_in_hour = varchar("check_in_hour", 5)
    val check_out_hour = varchar("check_out_hour", 5)
    val price_per_night = integer("price_per_night")
    val is_parking = byte("is_parking").default(0)
    val is_wifi = byte("is_wifi").default(0)
    val room_id = reference("room_id", RoomTable).uniqueIndex()
}

class RoomOptionClass(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<RoomOptionClass>(RoomOptionTable)
    val bedroom  by RoomOptionTable.bedroom
    val bed  by RoomOptionTable.bed
    val bathroom  by RoomOptionTable.bathroom
    var check_in_hour by RoomOptionTable.check_in_hour
    var check_out_hour by RoomOptionTable.check_out_hour
    var price_per_night by RoomOptionTable.price_per_night
    var is_parking by RoomOptionTable.is_parking
    var is_wifi by RoomOptionTable.is_wifi
    var room_id by RoomClass referencedOn RoomOptionTable.room_id
}

