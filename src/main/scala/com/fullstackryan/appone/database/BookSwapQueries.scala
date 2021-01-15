package com.fullstackryan.appone.database


import com.fullstackryan.appone.model.Book
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

  def insert(book: Book): doobie.Update0 = {
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


}

