package com.example.model.auth

import com.example.lib.database.registerTimestamp
import com.example.lib.database.tryGetProps
import com.example.model.user.UserClass
import com.example.model.user.UserTable
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import java.time.Instant

object AuthTable : IntIdTable("auth") {
    val refresh_token = varchar("refresh_token", 50).uniqueIndex()
    val expire_at = registerTimestamp("expire_at")
    val user_id = reference("user_id", UserTable).uniqueIndex()
}

class AuthClass(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<AuthClass>(AuthTable)
    var refresh_token by AuthTable.refresh_token
    var expire_at by  AuthTable.expire_at
    var user_id by UserClass referencedOn AuthTable.user_id

}

@Serializable
@Resource("/auth")
data class Auth(
    val refresh_token: String,
) {
    @Serializable
    @Resource("refresh")
    data class Refresh(
        val refresh_token: String
    )
    companion object {
        data class Out(
            val id: Int,
            val refresh_token: String?,
            val expire_at: Instant?,
            val user_id: Int?
        )

        val of: (ResultRow) -> Out = {
            Out(
                id = it.tryGetProps(AuthTable.id)!!.value,
                refresh_token = it.tryGetProps(AuthTable.refresh_token),
                expire_at = it.tryGetProps(AuthTable.expire_at),
                user_id = it.tryGetProps(AuthTable.user_id)?.value
            )
        }
    }
}