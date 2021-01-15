package com.fullstackryan.appone.model

import doobie.Meta
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.util.UUID


case class Book(id: UUID, title: String, author: String, yearOfRelease: Int)

object Book {
  //for decoding json into JobPostDetails
  implicit val circeDecoder: Decoder[Book] =
    deriveDecoder[Book]

  //for encoding JobPostDetails into json
  implicit val circeEncoder: Encoder[Book] =
    deriveEncoder[Book]

  implicit val uuidMeta: Meta[UUID] =
    Meta[String].imap[UUID](UUID.fromString)(_.toString)
}
