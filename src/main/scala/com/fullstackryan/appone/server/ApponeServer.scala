package com.fullstackryan.appone.server

import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import cats.implicits._
import com.fullstackryan.appone.config.{Config, DbConfig, ServerConfig}
import com.fullstackryan.appone.database.Database
import com.fullstackryan.appone.repo.{BookSwap, HelloWorld, Jokes}
import com.fullstackryan.appone.routing.ApponeRoutes
import fs2.Stream
import org.flywaydb.core.Flyway
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.util.Properties
//import pureconfig.generic.auto._

import java.net.URI
import scala.concurrent.ExecutionContext.global


object ApponeServer {

  def initFlyway[F[_] : Sync](url: String, username: String, password: String): F[Int] = Sync[F].delay {
    val flyway = Flyway.configure().dataSource(url, username, password).baselineOnMigrate(true).load()
    println("inside flyway")
    flyway.migrate()
  }

  def prodConfig(): Config = {
    val dbUri = new URI(System.getenv("DATABASE_URL"))
    val username = dbUri.getUserInfo.split(":")(0)
    val password = dbUri.getUserInfo.split(":")(1)
    val dbUrl = "jdbc:postgresql://" + dbUri.getHost + dbUri.getPath

    Config(ServerConfig(5432, dbUri.getHost), DbConfig(dbUrl, username, password, 10))
  }

  def stream[F[_] : ConcurrentEffect : ContextShift : Timer]: Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
//    config <- Stream.eval(LoadConfig[F, Config].load)
      conifg = prodConfig()
      _ <- Stream.eval(initFlyway(conifg.dbConfig.url, conifg.dbConfig.username, conifg.dbConfig.password))
      xa <- Stream.resource(Database.transactor(conifg.dbConfig))

      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)
      bookAlg = BookSwap.buildInstance[F](xa)
      httpApp = (
        ApponeRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
          ApponeRoutes.bookRoutes[F](bookAlg) <+>
          ApponeRoutes.jokeRoutes[F](jokeAlg)
        ).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)
       port = Properties.envOrElse("PORT", "8080").toInt
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(port, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}