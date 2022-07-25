package com.example.controller.auth

import com.example.lib.App
import com.example.lib.database.query
import com.example.model.auth.Auth
import com.example.model.auth.AuthClass
import com.example.model.auth.AuthTable
import com.example.model.user.UserClass
import com.example.model.user.UserTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant
import java.time.temporal.ChronoUnit

object AuthController {
    suspend fun getRefresh(
        refresh: Auth.Refresh
    ) = query {
        AuthTable.slice(AuthTable.columns)
            .select{AuthTable.refresh_token eq refresh.refresh_token}
            .map{ Auth.of(it) }.firstOrNull()
    }

    suspend fun postOrPatchRefresh(
        userId: Int,
        tokenPair: Pair<String, String>
    ) : Triple<Int, String, String> = query {
        val userClass = UserClass.findById(userId)?: return@query Triple(400,"user" , "does not exist")

        val authId: EntityID<Int>? = AuthTable.join(
            UserTable,
            JoinType.INNER,
            AuthTable.user_id,
            UserTable.id){
            UserTable.id eq userId
        }
            .slice(AuthTable.id)
            .selectAll()
            .map {it[AuthTable.id]}.firstOrNull()

        if (authId is EntityID<Int>){
            AuthClass.findById(authId).apply {
                if(this !is AuthClass) return@query Triple(410,"token" , "does not exist") // actually always not null
                this.refresh_token = tokenPair.second
                this.expire_at = Instant.now().plus(App.config.jwt.lifetime.refresh, ChronoUnit.DAYS)
            }
            return@query Triple(210,"refresh_token","has been renewed")
        }else {
            AuthClass.new {
                this.refresh_token = tokenPair.second
                this.expire_at = Instant.now().plus(App.config.jwt.lifetime.refresh, ChronoUnit.DAYS)
                this.user_id = userClass
            }
        }
        return@query Triple(200,"refresh_token","has been issued" )
    }
}

