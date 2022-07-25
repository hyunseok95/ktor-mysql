package com.example.model.room

import com.example.model.host.HostClass
import com.example.model.host.HostTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object RoomTable : IntIdTable("room"){
    val type = varchar("type", 20)
    val name = varchar("name", 50).uniqueIndex()
    val introduction = text("introduction", eagerLoading = false).clientDefault { "" }
    val guest = integer("guest").default(1)
    val location = varchar("location", 100)
    val latitude = decimal("latitude",9,6).uniqueIndex()
    val longitude = decimal("longitude",9,6).uniqueIndex()
    val image_directory = varchar("image_directory",50).uniqueIndex()
    val host_id = reference("host_id", HostTable)
}

class RoomClass(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<RoomClass>(RoomTable)
    var type by RoomTable.type
    var name by RoomTable.name
    var introduction by RoomTable.introduction
    var guest by RoomTable.guest
    var location by RoomTable.location
    var latitude by RoomTable.latitude
    var longitude by RoomTable.longitude
    var image_directory by RoomTable.image_directory
    var host_id by HostClass referencedOn RoomTable.host_id
}

