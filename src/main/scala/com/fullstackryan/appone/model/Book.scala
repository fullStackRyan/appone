package com.fullstackryan.appone.model

//import doobie.Meta
//import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}



case class Book(title: String, author: String, yearOfRelease: Int)

object Book {

//  implicit val circeDecoder: Decoder[Book] =
//    deriveDecoder[Book]
//
//  implicit val circeEncoder: Encoder[Book] =
//    deriveEncoder[Book]
//
//  implicit val uuidMeta: Meta[UUID] =
//    Meta[String].imap[UUID](UUID.fromString)(_.toString)
//

  implicit val bookEncoder: Encoder[Book] = Encoder.forProduct3(
    "title",
    "author",
    "yearOfRelease"
  )(x => Book.unapply(x).get)

  implicit val bookDecoder: Decoder[Book] = Decoder.forProduct3(
    "title",
    "author",
    "yearOfRelease"
  )(Book.apply)


}
