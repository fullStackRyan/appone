package com.fullstackryan.appone.model

//import doobie.Meta
//import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import io.circe.{Decoder, Encoder}

import java.util.UUID


case class BookDB(id: UUID, title: String, author: String, yearOfRelease: Int)

object BookDB {

  //  implicit val circeDecoder: Decoder[BookDB] =
  //    deriveDecoder[BookDB]
  //
  //  implicit val circeEncoder: Encoder[BookDB] =
  //    deriveEncoder[BookDB]
  //
  //  implicit val uuidMeta: Meta[UUID] =
  //    Meta[String].imap[UUID](UUID.fromString)(_.toString)
  //

  implicit val bookEncoder: Encoder[BookDB] = Encoder.forProduct4(
    "id",
    "title",
    "author",
    "yearOfRelease"
  )(x => BookDB.unapply(x).get)

  implicit val bookDecoder: Decoder[BookDB] = Decoder.forProduct4(
    "id",
    "title",
    "author",
    "yearOfRelease"
  )(BookDB.apply)


}