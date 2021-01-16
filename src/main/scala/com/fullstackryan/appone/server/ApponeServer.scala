package com.fullstackryan.appone.server

import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import cats.implicits._
import com.fullstackryan.appone.config.{Config, LoadConfig}
import com.fullstackryan.appone.database.Database
import com.fullstackryan.appone.repo.{BookSwap, HelloWorld, Jokes}
import com.fullstackryan.appone.routing.ApponeRoutes
import fs2.Stream
import org.flywaydb.core.Flyway
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext.global



object ApponeServer {

  def initFlyway[F[_]: Sync](url: String, username: String, password: String): F[Int] = Sync[F].delay {
    val flyway = Flyway.configure().dataSource(url, username, password).baselineOnMigrate(true).load()
    println("inside flyway")
    flyway.migrate()
  }

  def stream[F[_] : ConcurrentEffect: ContextShift: Timer]: Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      config <- Stream.eval(LoadConfig[F, Config].load)
      _ = println(s"=========> ${sys.env.get("DATABASE_URL")}")
      _ <- Stream.eval(initFlyway(config.dbConfig.url, config.dbConfig.username, config.dbConfig.password))
      xa <- Stream.resource(Database.transactor(config.dbConfig))
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)
      bookAlg = BookSwap.buildInstance[F](xa)
      httpApp = (
        ApponeRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
          ApponeRoutes.bookRoutes[F](bookAlg) <+>
          ApponeRoutes.jokeRoutes[F](jokeAlg)
        ).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
