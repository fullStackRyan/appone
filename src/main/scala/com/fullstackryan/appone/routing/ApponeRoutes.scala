package com.fullstackryan.appone.routing

import cats.effect.Sync
import cats.implicits._
import com.fullstackryan.appone.model.{Book, BookDB}
import com.fullstackryan.appone.repo.BookSwap
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Request, Response}

import java.util.UUID

object ApponeRoutes {


  def bookRoutes[F[_] : Sync](B: BookSwap[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    def getBooks: F[Response[F]] = for {
      book <- B.get
      resp <- Ok(book)
    } yield resp

    def postABook(req: Request[F]): F[Response[F]] =
      for {
        book <- req.as[Book]
        generatedUUID = UUID.randomUUID()
        _ <- B.post(BookDB(generatedUUID, book.title, book.author, book.yearOfRelease))
        resp <- Ok()
      } yield resp

    def updateABook(req: Request[F]): F[Response[F]] = for {
      book <- req.as[Book]
      _ <- B.update(book)
      resp <- Ok()
    } yield resp

    def deleteABook(req: Request[F]): F[Response[F]] = for {
      book <- req.as[Book]
      _ <- B.delete(book)
      resp <- Ok()
    } yield resp


    HttpRoutes.of[F] {
      case GET -> Root => Ok("APP ONE BACKEND HOME PAGE")
      case GET -> Root / "book" => getBooks
      case req@POST -> Root / "book" => postABook(req)
      case req@PUT -> Root / "book" => updateABook(req)
      case req@DELETE -> Root / "book" => deleteABook(req)
    }
  }

}
