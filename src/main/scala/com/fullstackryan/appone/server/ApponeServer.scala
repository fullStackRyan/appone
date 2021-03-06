package com.fullstackryan.appone.server

import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import com.fullstackryan.appone.config.{Config, DbConfig, ServerConfig}
import com.fullstackryan.appone.database.Database
import com.fullstackryan.appone.repo.BookSwap
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

  def devOrProdConfig(): Config = {

    if (System.getenv("ENV") == "dev") {
      val username = System.getenv("DATABASE_USERNAME")
      val password = System.getenv("DATABASE_PASSWORD")
      val url = System.getenv("DATABASE_URL")

      Config(ServerConfig(5400, "localhost:8080"), DbConfig(url, username, password, 10))
    } else {
      val dbUri = new URI(System.getenv("DATABASE_URL"))
      val username = dbUri.getUserInfo.split(":")(0)
      val password = dbUri.getUserInfo.split(":")(1)
      val dbUrl = "jdbc:postgresql://" + dbUri.getHost + dbUri.getPath

      Config(ServerConfig(5432, dbUri.getHost), DbConfig(dbUrl, username, password, 10))
    }

  }

  def stream[F[_] : ConcurrentEffect : ContextShift : Timer]: Stream[F, Nothing] = {
    for {
      _ <- BlazeClientBuilder[F](global).stream
      config = devOrProdConfig()
      _ <- Stream.eval(initFlyway(config.dbConfig.url, config.dbConfig.username, config.dbConfig.password))
      xa <- Stream.resource(Database.transactor(config.dbConfig))
      bookAlg = BookSwap.buildInstance[F](xa)


      httpApp = ApponeRoutes.bookRoutes[F](bookAlg).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      port = Properties.envOrElse("PORT", "8080").toInt
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(port, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}