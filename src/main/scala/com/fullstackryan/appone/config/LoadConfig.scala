package com.fullstackryan.appone.config

import cats.ApplicativeError
import cats.implicits._
import pureconfig.error.ConfigReaderException
import pureconfig.{ConfigReader, ConfigSource, Derivation}
import shapeless.the

trait LoadConfig[F[_], TConfig] {
  def load: F[TConfig]
}

object LoadConfig {
  def load[F[_], TConfig](implicit loadConfig: LoadConfig[F, TConfig]): F[TConfig] =
    the[LoadConfig[F, TConfig]].load

  def apply[F[_], TConfig](
                            implicit reader: Derivation[ConfigReader[TConfig]], ae: ApplicativeError[F, Throwable]
                          ): LoadConfig[F, TConfig] =
    new LoadConfig[F, TConfig] {
      def load: F[TConfig] = ApplicativeError[F, Throwable].fromEither {
        ConfigSource.default
          .load[TConfig]
          .leftMap(ConfigReaderException(_))
      }
    }

}
