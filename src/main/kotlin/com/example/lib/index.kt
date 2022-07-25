package com.example.lib

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import java.io.File

object App {
    val getConfig = ConfigFactory.load().resolve()!!
    object config {
        object ktor {
            object application {
                val id = getConfig.tryGetString("ktor.application.id")?: "Application"
                val developmentMode = getConfig.tryGetString("ktor.application.developmentMode")?.toBoolean()?: true
                val rootPath = getConfig.tryGetString("ktor.application.rootPath")?: ""
                val watchPaths = getConfig.tryGetStringList("ktor.application.watchPaths")?: listOf(File(".").canonicalPath)
            }
            object deployment {
                val host = getConfig.tryGetString("ktor.deployment.host")?: "localhost"
                val port = getConfig.tryGetString("ktor.deployment.port")?.toInt()?: 8080
                val sslPort = getConfig.tryGetString("ktor.deployment.sslPort")?.toInt()?: 8081
                val frontHost = getConfig.tryGetString("ktor.deployment.frontHost")?: "localhost"
                val frontPort = getConfig.tryGetString("ktor.deployment.frontPort")?.toInt()?: 3000
            }
            object ssl {
                val keyAlias = getConfig.tryGetString("ktor.ssl.keyAlias")?: "ktor"
                val keyStorePath = getConfig.tryGetString("ktor.ssl.keyStorePath")?: "keystore.jks"
                val keyStorePassword = getConfig.tryGetString( "ktor.ssl.keyStorePassword")?: "123456"
                val privateKeyPassword = getConfig.tryGetString("ktor.ssl.privateKeyPassword")?: "1234"
            }
        }
        object netty {
            val requestQueueLimit = getConfig.tryGetString("netty.requestQueueLimit")?.toInt()?: 16
            val runningLimit =  getConfig.tryGetString("netty.runningLimit")?.toInt()?: 10
            val shareWorkGroup =  getConfig.tryGetString("netty.shareWorkGroup")?.toBoolean()?: false
            val responseWriteTimeoutSeconds = getConfig.tryGetString("netty.responseWriteTimeoutSeconds")?.toInt()?: 10
            val requestReadTimeoutSeconds =getConfig.tryGetString("netty.requestReadTimeoutSeconds")?.toInt()?: 0
            val tcpKeepAlive = getConfig.tryGetString("netty.tcpKeepAlive")?.toBoolean()?: true
        }
        object jwt {
            val issuer = getConfig.tryGetString("jwt.issuer")?: "http://localhost:8080/auth"
            object lifetime {
                val access = getConfig.tryGetString("jwt.lifetime.access")?.toLong()?: 60
                val refresh = getConfig.tryGetString("jwt.lifetime.refresh")?.toLong()?: 14
            }
            object user {
                val audience = getConfig.tryGetString("jwt.user.audience")?: "http://localhost:8080/auth/user"
                val realm = getConfig.tryGetString("jwt.user.realm")?: "Request to get user permission"
                val secret = getConfig.tryGetString("jwt.user.secret")?: "User secret code"
            }
            object host {
                val audience = getConfig.tryGetString("jwt.host.audience")?: "http://localhost:8080/auth/host"
                val realm = getConfig.tryGetString("jwt.host.realm")?: "Request to get host permission"
                val secret = getConfig.tryGetString("jwt.host.secret")?: "Host secret code"
            }
        }
        object db {
            val host = getConfig.tryGetString("db.host")?: "localhost"
            val port = getConfig.tryGetString("db.port")?: "3306"
            val database = getConfig.tryGetString("db.database")?: "test"
            val username = getConfig.tryGetString("db.username")?: "root"
            val password = getConfig.tryGetString("db.password")?: "1234"
        }
    }
}


