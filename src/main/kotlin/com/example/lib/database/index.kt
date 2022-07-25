package com.example.lib.database

import com.example.lib.App
import com.example.model.auth.AuthTable
import com.example.model.host.HostTable
import com.example.model.reservation.ReservationTable
import com.example.model.review.ReviewTable
import com.example.model.review.image.ReviewImageTable
import com.example.model.room.RoomTable
import com.example.model.room.option.RoomOptionTable
import com.example.model.user.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun <T> query(
    block: () -> T
): T =  coroutineScope {
    async(context = Dispatchers.Unconfined, start = CoroutineStart.LAZY) {
        transaction {
            block()
        }
    }.await()
}

fun <T> ResultRow.tryGetProps(
    expression: Expression<T>
): T? =  if(this.hasValue(expression)) this[expression] else null


suspend fun getDatabase(){

    val config = HikariConfig().apply{
        jdbcUrl         = "jdbc:mysql://${App.config.db.host}:${App.config.db.port}/${App.config.db.database}"
        driverClassName =  "com.mysql.cj.jdbc.Driver"
        username        =  App.config.db.username
        password        =  App.config.db.password
        maximumPoolSize =  10
    }

    val dataSource = HikariDataSource(config)

    Database.connect(dataSource)

    runBlocking {
        async(start = CoroutineStart.LAZY) {
            transaction {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(
                    UserTable,
                    HostTable,
                    RoomTable,
                    RoomOptionTable,
                    ReservationTable,
                    ReviewTable,
                    ReviewImageTable,
                    AuthTable,
                )
            }
        }.await()
    }
}
