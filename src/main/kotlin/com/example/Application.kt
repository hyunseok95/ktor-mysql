package com.example

import com.example.handler.getRoutes
import com.example.lib.App
import com.example.lib.api.ServerResponse
import com.example.lib.database.getDatabase
import com.example.lib.jwt.getJWTVerifier
import com.example.lib.server.getLogger
import com.example.lib.server.getSslConnector
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.netty.NettyApplicationEngine.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

fun main() {
    val appConfig = applicationEngineEnvironment {
        log = getLogger()
        config = HoconApplicationConfig(App.getConfig)
        developmentMode = App.config.ktor.application.developmentMode
        rootPath = App.config.ktor.application.rootPath
        watchPaths =  App.config.ktor.application.watchPaths


        connector {
            host = App.config.ktor.deployment.host
            port = App.config.ktor.deployment.port
        }

        getSslConnector(
            host = App.config.ktor.deployment.host,
            sslPort = App.config.ktor.deployment.sslPort,
            keyAlias = App.config.ktor.ssl.keyAlias,
            keyStorePath = App.config.ktor.ssl.keyStorePath,
            keyStorePassword = App.config.ktor.ssl.keyStorePassword,
            privateKeyPassword =  App.config.ktor.ssl.privateKeyPassword,
        )}

    val nettyConfig: Configuration.() -> Unit = {
        requestQueueLimit =  App.config.netty.requestQueueLimit
        runningLimit = App.config.netty.runningLimit
        shareWorkGroup = App.config.netty.shareWorkGroup
        responseWriteTimeoutSeconds = App.config.netty.responseWriteTimeoutSeconds
        requestReadTimeoutSeconds = App.config.netty.requestReadTimeoutSeconds
        tcpKeepAlive = App.config.netty.tcpKeepAlive
        configureBootstrap = {}
    }

    val netty = Netty.create(appConfig, nettyConfig)

    netty.addShutdownHook {
        netty.stop(3, 5, TimeUnit.SECONDS)
    }

    netty.start(true)
}

fun Application.module() {
    runBlocking {
        async(start = CoroutineStart.LAZY) {
            getDatabase()
        }.await()
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    install(PartialContent)

    install(AutoHeadResponse)

    install(Resources)

    install(Authentication) {
        jwt("user-access") {
            realm = App.config.jwt.user.realm
            verifier {
                getJWTVerifier("user")
            }
            validate {
                if (it.payload.getClaim("userId").asString() != ""
                    && it.payload.expiresAt.time > System.currentTimeMillis())
                    JWTPrincipal(it.payload)
                else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized,
                    ServerResponse.Message("Token is not valid or has expired"))
            }
        }
    }

    install(CORS) {
        allowNonSimpleContentTypes = true
        maxAgeInSeconds = 3600
        allowHost("${App.config.ktor.deployment.frontHost}:${App.config.ktor.deployment.frontPort}", schemes = listOf("http", "https"))
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeadersPrefixed("custom-")
    }

    getRoutes()

}
