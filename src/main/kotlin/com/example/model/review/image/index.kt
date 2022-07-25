package com.example.model.review.image

import com.example.lib.database.registerTimestamp
import com.example.model.review.ReviewClass
import com.example.model.review.ReviewTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object ReviewImageTable : IntIdTable("review_image"){
    val name_uuid = varchar("name_uuid", 255).uniqueIndex()
    val name = varchar("name", 30)
    val location = varchar("location", 50)
    val expire_at = registerTimestamp("expire_at") // mysql 에서 수동으로 설정
    val metadata = text("metadata").nullable()
    val review_id = reference("review_id", ReviewTable).uniqueIndex()
}

class ReviewImageClass(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<ReviewImageClass>(ReviewImageTable)
    var name_uuid by ReviewImageTable.name_uuid
    var name by ReviewImageTable.name
    var location by ReviewImageTable.location
    var expire_at by ReviewImageTable.expire_at
    var metadata by ReviewImageTable.metadata
    var review_id by ReviewClass referencedOn ReviewImageTable.review_id
}

