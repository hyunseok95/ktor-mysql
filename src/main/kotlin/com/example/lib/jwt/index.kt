package com.example.lib.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.lib.App
import java.time.Duration
import java.util.*


fun getAudienceToSecret(target: String): Pair<String?, String?> =
    if(target == "user") App.config.jwt.user.audience to App.config.jwt.user.secret
    else if(target == "host") App.config.jwt.host.audience to App.config.jwt.host.secret
    else null to null

fun getJWTVerifier(target: String): JWTVerifier? {
    val audienceToSecret = getAudienceToSecret(target)
    return JWT.require(Algorithm.HMAC256(audienceToSecret.second))
        .withAudience(audienceToSecret.first)
        .withIssuer(App.config.jwt.issuer)
        .build()
}

fun getTokenPair(target: String, id: Int): Pair<String, String> {
    val audienceToSecret = getAudienceToSecret(target)
    val accessToken = JWT.create()
        .withIssuer(App.config.jwt.issuer)
        .withExpiresAt(Date(System.currentTimeMillis() + Duration.ofMinutes(
            App.config.jwt.lifetime.access).toMillis()))
        .withClaim("${target}Id", id.toString())
        .withAudience(audienceToSecret.first)
        .sign(Algorithm.HMAC256(audienceToSecret.second))
    val refreshToken = UUID.randomUUID().toString()
    return accessToken to refreshToken
}


