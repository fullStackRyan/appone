package com.fullstackryan.appone.repo

import cats.effect.Sync
import com.fullstackryan.appone.database.BookSwapQueries
import com.fullstackryan.appone.model.{Book, BookDB}
import doobie.implicits._
import doobie.util.transactor.Transactor

import java.util.UUID

trait BookSwap[F[_]] {
  def get: F[List[(UUID, String, String, Int)]]
  def post(book: BookDB): F[Int]
  def update(book: Book): F[Int]
  def delete(book: Book): F[Int]
}

object BookSwap {


  def buildInstance[F[_]: Sync](xa: Transactor[F]): BookSwap[F] = new BookSwap[F] {

    override def get: F[List[(UUID, String, String, Int)]] = BookSwapQueries.getAll.to[List].transact(xa)

    override def post(book: BookDB): F[Int] =
      BookSwapQueries.insert(book).run.transact(xa)

    override def update(book: Book): F[Int] = BookSwapQueries.update(book).run.transact(xa)

    override def delete(book: Book): F[Int] = BookSwapQueries.delete(book).run.transact(xa)
  }





}
