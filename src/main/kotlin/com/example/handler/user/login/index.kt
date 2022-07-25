package com.example.handler.user.login

import com.example.controller.auth.AuthController
import com.example.controller.user.UserController
import com.example.lib.api.ServerResponse
import com.example.lib.api.callError
import com.example.lib.jwt.getTokenPair
import com.example.model.user.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userLogin(){
    route(""){
        post<User.Login>{
            val user = UserController.getLogin(it)?: return@post call.respond(
                HttpStatusCode.NotAcceptable, callError(
                "Invalided Data",
                "email",
                "body",
                "does not exist",
                it.email,
            ))

            if (user.password == it.password) {
                val tokenPair = getTokenPair("user", user.id)
                val result = AuthController.postOrPatchRefresh(user.id,tokenPair)

                if(result.first < 400){
                    call.response.cookies.append(Cookie("access-token",tokenPair.first, httpOnly = true))
                    call.response.cookies.append(Cookie("refresh-token",tokenPair.second, httpOnly = true))
                    return@post call.respond(
                        HttpStatusCode.Accepted,
                        ServerResponse.Message("${result.second} ${result.third}"))
                }else{
                    return@post call.respond(
                        HttpStatusCode.Forbidden, callError(
                        "Invalided ${result.second}",
                        result.second,
                        "body",
                        result.third,
                        "hidden"
                    )
                    )
                }
            } else {
                return@post call.respond(
                    HttpStatusCode.NotAcceptable, callError(
                    "Invalided Data",
                    "password",
                    "body",
                    "does not matches",
                    it.password
                )
                )
            }
        }
    }
}