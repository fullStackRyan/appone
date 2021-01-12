package com.fullstackryan.appone

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]) =
    ApponeServer.stream[IO].compile.drain.as(ExitCode.Success)
}
