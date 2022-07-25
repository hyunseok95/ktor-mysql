package com.example.model.host

import com.example.model.user.UserClass
import com.example.model.user.UserTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object HostTable: IntIdTable("host"){
    val nickname = varchar("nickname", 20).uniqueIndex()
    val address = varchar("address", 50).uniqueIndex()
    val user_id = reference("user_id", UserTable).uniqueIndex()
}

class HostClass(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<HostClass>(HostTable)
    var nickname by HostTable.nickname
    var address by HostTable.address
    var user_id by UserClass referencedOn HostTable.user_id
}

