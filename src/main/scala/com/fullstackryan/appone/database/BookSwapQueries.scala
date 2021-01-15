package com.fullstackryan.appone.database


import doobie.implicits._
import doobie.postgres.implicits._

import java.util.UUID

object BookSwapQueries {

  def getAll: doobie.Query0[(UUID, String)] = {
    sql"""
         |SELECT * FROM book
       """.stripMargin
      .query[(UUID, String)]
  }


}

