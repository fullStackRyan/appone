package com.fullstackryan.appone

import cats.effect.{ExitCode, IO, IOApp}
import com.fullstackryan.appone.server.ApponeServer

object Main extends IOApp {
  def run(args: List[String]) =
    ApponeServer.stream[IO].compile.drain.as(ExitCode.Success)
}
