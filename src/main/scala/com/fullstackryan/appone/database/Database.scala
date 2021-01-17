
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

    config.setJdbcUrl(dbConfig.url)
    config.setUsername(dbConfig.username)
    config.setPassword(dbConfig.password)
    config.setMaximumPoolSize(dbConfig.poolSize)

    val resources: Resource[F, HikariTransactor[F]] = for {
      cachedThreadPool <- ExecutionContexts.cachedThreadPool[F]
      connectionThreadPool <- ExecutionContexts.fixedThreadPool[F](10)
      transactor <- Resource.liftF(Sync[F].delay(HikariTransactor.apply[F](new HikariDataSource(config), connectionThreadPool, Blocker.liftExecutionContext(cachedThreadPool))))
    } yield transactor

    resources
  }

  type BracketThrowable[F[_]] = Bracket[F, Throwable]

}