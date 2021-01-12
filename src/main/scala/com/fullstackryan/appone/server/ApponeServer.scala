package com.fullstackryan.appone.server

import cats.effect.{ConcurrentEffect, Timer}
import cats.implicits._
import com.fullstackryan.appone.repo.{HelloWorld, Jokes}
import com.fullstackryan.appone.routing.ApponeRoutes
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object ApponeServer {

  def stream[F[_] : ConcurrentEffect](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)
      //      bookAlg = BookSwap.buildInstance[F]

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        ApponeRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
          ApponeRoutes.bookRoutes[F] <+>
          ApponeRoutes.jokeRoutes[F](jokeAlg)
        ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
