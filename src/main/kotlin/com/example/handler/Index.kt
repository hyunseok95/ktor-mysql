package com.example.handler

import com.example.handler.auth.auth
import com.example.handler.room.room
import com.example.handler.user.user
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*


fun Application.getRoutes() {
        routing {
                trace { application.log.trace(it.buildText()) }
                route("/user"){
                        user()
                }
                route("/auth"){
                        authenticate("user-access") {
                                auth()
                        }
                }
                route("/room"){
                        room()
                }
        }
}
