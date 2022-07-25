package com.example.model.user

import com.example.lib.database.tryGetProps
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object UserTable: IntIdTable("user"){
    val name = varchar("name", 12)
    val phone_number = varchar("phone_number", 13).uniqueIndex()
    val email = varchar("email", 30).uniqueIndex()
    val password = varchar("password", 20)
    val profile = varchar("profile", 50).nullable().uniqueIndex()
}

class UserClass(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<UserClass>(UserTable)
    var name by UserTable.name
    var phone_number by UserTable.phone_number
    var email by UserTable.email
    var password by UserTable.password
    var profile by UserTable.profile
}

@Serializable
@Resource("/user")
data class User(
    var email : String,
    val phone_number : String,
    val name : String,
    var password : String
) {
    @Serializable
    @Resource("login")
    data class Login(
        var email: String,
        var password: String
    )
    companion object{
        data class Out (
            val id: Int,
            var email: String?,
            val phone_number: String?,
            val name: String?,
            var password: String?,
        )
        val of: (ResultRow)-> Out = {
            Out(
                id = it.tryGetProps(UserTable.id)!!.value,
                email = it.tryGetProps(UserTable.email),
                phone_number = it.tryGetProps(UserTable.phone_number),
                name = it.tryGetProps(UserTable.name),
                password = it.tryGetProps(UserTable.password)
            )
        }
    }
}