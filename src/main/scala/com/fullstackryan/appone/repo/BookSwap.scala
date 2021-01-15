package com.fullstackryan.appone.repo

import cats.effect.Sync
import com.fullstackryan.appone.database.BookSwapQueries
import com.fullstackryan.appone.model.Book
import doobie.implicits._
import doobie.util.transactor.Transactor

import java.util.UUID

trait BookSwap[F[_]] {
  def get: F[List[(UUID, String)]]
  def post(book: Book): F[Int]
  def update(book: Book): F[Int]
  def delete(book: Book): F[Int]
}

object BookSwap {


  def buildInstance[F[_]: Sync](xa: Transactor[F]): BookSwap[F] = new BookSwap[F] {

    override def get: F[List[(UUID, String)]] = BookSwapQueries.getAll.to[List].transact(xa)

    override def post(book: Book): F[Int] = ???

    override def update(book: Book): F[Int] = ???

    override def delete(book: Book): F[Int] = ???
  }





}
