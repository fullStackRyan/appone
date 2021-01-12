package com.fullstackryan.appone

import cats.effect.Sync
import com.fullstackryan.appone.model.Book

trait BookSwap[F[_]] {
  def get: F[Book]
  def post(book: Book): F[Int]
  def update(book: Book): F[Int]
  def delete(book: Book): F[Int]
}

object BookSwap {


  def buildInstance[F[_]: Sync]: BookSwap[F] = new BookSwap[F] {
    override def get: F[Book] = ???

    override def post(book: Book): F[Int] = ???

    override def update(book: Book): F[Int] = ???

    override def delete(book: Book): F[Int] = ???
  }





}
