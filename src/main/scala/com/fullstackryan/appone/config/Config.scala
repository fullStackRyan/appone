package com.fullstackryan.appone.config

import cats.effect.{IO, Resource}
import io.circe.{Decoder, parser}
import io.circe.generic.semiauto.deriveDecoder

import scala.io.{BufferedSource, Source}

case class ServerConfig(port: Int, host: String)

case class DbConfig(url: String, username: String, password: String, poolSize: Int)

case class Config(serverConfig: ServerConfig, dbConfig: DbConfig)

object Config {
  implicit val configDecoder: Decoder[Config] = deriveDecoder[Config]
  implicit val serverDecoder: Decoder[ServerConfig] = deriveDecoder[ServerConfig]
  implicit val dbDecoder: Decoder[DbConfig] = deriveDecoder[DbConfig]


  def load(): IO[Config] = {
    val src: Resource[IO, BufferedSource] = Resource.make(IO {
      Source.fromResource("application.conf")
    })(x => IO {
      x.close()
    })
    src.use { source: BufferedSource =>
      val dbConf: String = source.getLines().mkString
      parser.decode[Config](dbConf) match {
        case Left(value) => IO.raiseError[Config](new Exception(s"failed to load config: $value"))
        case Right(value) => IO.pure(value)
      }
    }
  }
}
