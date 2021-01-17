package com.fullstackryan.appone.database

import cats.effect.{Async, Blocker, Bracket, ContextShift, Resource, Sync}
import com.fullstackryan.appone.config.DbConfig
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

object Database {

  def transactor[F[_]: ContextShift: Async](dbConfig: DbConfig): Resource[F, HikariTransactor[F]] = {
    val config = new HikariConfig()
    config.setDriverClassName("org.postgresql.Driver")

    config.setJdbcUrl("jdbc:postgresql://ec2-52-17-53-249.eu-west-1.compute.amazonaws.com:5432/ddtlm7p23o6ils")
    config.setUsername("bfzsxjhkfqgdtm")
    config.setPassword("80f283335a2ff9247dffc3b937b90a77f120c8cf8cf9db432c1f7e9cb7b0fcb6")
    config.setMaximumPoolSize(10)

    val resources: Resource[F, HikariTransactor[F]] = for {
      cachedThreadPool <- ExecutionContexts.cachedThreadPool[F]
      connectionThreadPool <- ExecutionContexts.fixedThreadPool[F](10)
      transactor <- Resource.liftF(Sync[F].delay(HikariTransactor.apply[F](new HikariDataSource(config), connectionThreadPool, Blocker.liftExecutionContext(cachedThreadPool))))
    } yield transactor

    resources
  }

  type BracketThrowable[F[_]] = Bracket[F, Throwable]

}
