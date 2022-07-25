package com.example.lib.api

import io.ktor.resources.*
import io.ktor.util.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("*")
data class ServerResponse(
    val location: String? ="",
    val param: String?="",
    val value: String?="",
    val error: String?="",
    val message: String?=""
){
    @Serializable
    @Resource("*")
    data class Message(
        val message: String,
        val detail: ServerResponse? = ServerResponse()
    ){
        @Serializable
        @Resource("*")
        data class Error(
            val errors: Message
        )
    }
}
fun callError(
    error:String,
    key:String,
    location:String,
    message: String,
    value:String,
) = ServerResponse.Message.Error(
    ServerResponse.Message(
        "[${error.toUpperCasePreservingASCIIRules()}] '$key'($location) $message, input '$key': $value",
        ServerResponse(
            location,
            key,
            value,
            error,
            message,
        )
    )
)

enum class ErrorCode {
   INVALIDED_DATA, NO_RESOURCES
}