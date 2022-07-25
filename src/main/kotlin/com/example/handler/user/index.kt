package com.example.handler.user

import com.example.controller.user.UserController
import com.example.handler.user.login.userLogin
import com.example.lib.api.ServerResponse
import com.example.lib.api.callError
import com.example.model.user.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.user(){
    route("") {
        options {
            call.response.header("Allow","POST,OPTIONS,HEAD")
            call.respond(HttpStatusCode.OK)
        }
        head {
            call.response.headers.append("Content-Length", "120")
            call.respond(HttpStatusCode.OK)
        }
        post<User> {
            val isSignUp = UserController.postUser(it)
            if (isSignUp) {
                return@post call.respond(HttpStatusCode.Created,
                    ServerResponse.Message("You have successfully signed up."))
            } else {
                return@post call.respond(HttpStatusCode.NotAcceptable, callError(
                    "Duplication Data",
                    "email",
                    "body",
                    "already exists",
                    it.email,
                ))
            }
        }

        route("/login"){
            userLogin()
        }
    }
}



