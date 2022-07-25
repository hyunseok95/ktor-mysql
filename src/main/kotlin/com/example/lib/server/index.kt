package com.example.lib.server

import com.example.lib.App
import com.typesafe.config.ConfigValueFactory
import io.ktor.server.engine.*
import io.ktor.util.logging.*
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore


fun getLogger(): Logger {
    val logger = KtorSimpleLogger(App.config.ktor.application.id)

    val contentHiddenValue = ConfigValueFactory.fromAnyRef("***", "Content hidden")
    if (App.getConfig.hasPath("ktor")) {
        logger.trace(
            App.getConfig.getObject("ktor")
                .withoutKey("ssl")
                .withValue("ssl", contentHiddenValue)
                .render())
    } else {
        logger.trace("No configuration provided: neither application.conf ")
    }
    return logger
}

fun ApplicationEngineEnvironmentBuilder.getSslConnector(
    host: String,
    sslPort: Int,
    keyAlias: String,
    keyStorePath: String,
    keyStorePassword: String,
    privateKeyPassword: String,
) {

    val keyStoreFile = File(keyStorePath).let { file ->
        if (file.exists() || file.isAbsolute) file else File(".", keyStorePath).absoluteFile
    }

    val keyStore = KeyStore.getInstance("JKS").apply {
        FileInputStream(keyStoreFile).use {
            load(it, keyStorePassword.toCharArray())
        }

        requireNotNull(getKey(keyAlias, privateKeyPassword.toCharArray())) {
            "The specified key $keyAlias doesn't exist in the key store $keyStorePath"
        }
    }

    sslConnector(
        keyStore,
        keyAlias,
        { keyStorePassword.toCharArray() },
        { privateKeyPassword.toCharArray() }
    ) {
        this.host = host
        this.port = sslPort
        this.keyStorePath = keyStoreFile
    }
}
