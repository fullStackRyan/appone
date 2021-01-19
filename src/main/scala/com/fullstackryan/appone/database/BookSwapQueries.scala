package com.fullstackryan.appone.database


import com.fullstackryan.appone.model.{Book, BookDB}
import doobie.implicits._
import doobie.postgres.implicits._

import java.util.UUID

object BookSwapQueries {

  def getAll: doobie.Query0[(UUID, String, String, Int)] = {
    sql"""
         |SELECT * FROM book
       """.stripMargin
      .query[(UUID, String, String, Int)]
  }

  def insert(book: BookDB): doobie.Update0 = {
    sql"""
         |INSERT INTO book (
         |  id,
         |  title,
         |  author,
         |  yearOfRelease
         |)
         |VALUES (
         |  ${book.id},
         |  ${book.title},
         |  ${book.author},
         |  ${book.yearOfRelease}
         |)
        """.stripMargin
      .update
  }

  def update(book: Book): doobie.Update0 = {
    sql"""
         |UPDATE book
         |SET title = ${book.title}, author = ${book.author}, yearOfRelease = ${book.yearOfRelease}
         |WHERE title = ${book.title}
       """.stripMargin
      .update
  }

  def delete(book: Book): doobie.Update0 = {
    sql"""
         |DELETE FROM book
         |WHERE title=${book.title}
       """.stripMargin
      .update
  }


}

