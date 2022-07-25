package com.example.handler.auth

import com.example.handler.auth.refresh.authRefresh
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.auth() {
    route("") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
            call.respondText("your userId is, $userId! Token is expired at $expiresAt ms.")
        }

        route("/refresh") {
            authRefresh()
        }
    }

}



