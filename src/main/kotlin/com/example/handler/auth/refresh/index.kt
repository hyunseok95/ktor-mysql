package com.example.handler.auth.refresh


import com.example.controller.auth.AuthController
import com.example.lib.api.ServerResponse
import com.example.lib.jwt.getTokenPair
import com.example.model.auth.Auth
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant

fun Route.authRefresh() {

    route("") {
        post<Auth.Refresh> {

            val refresh = AuthController.getRefresh(it) ?: return@post

            if (refresh.expire_at is Instant
                && refresh.expire_at > Instant.now()
            ) {
                val tokenPair: Pair<String, String> = getTokenPair("user", refresh.id)
                AuthController.postOrPatchRefresh(refresh.id, tokenPair)
                call.respond(tokenPair)
            } else
                call.respond(HttpStatusCode.BadRequest,ServerResponse.Message("invalid token"))
        }
    }
}



