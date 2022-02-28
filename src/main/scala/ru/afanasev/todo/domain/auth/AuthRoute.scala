package ru.afanasev.todo.domain.auth

import cats._
import cats.data._
import cats.implicits._

import cats.effect._

import org.http4s._
import org.http4s.server.AuthMiddleware
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._

import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._


import com.typesafe.scalalogging.Logger

import ru.afanasev.todo.domain.Route
import ru.afanasev.todo.domain.auth.model.Auth._
import ru.afanasev.todo.domain.auth.model._
import ru.afanasev.todo.domain.subscriber.model._

class AuthRoute[F[_]: Defer: Sync](log: Logger, authService: AuthService[F]) extends Route[F] {

  implicit val dsl = Http4sDsl.apply[F]
  import dsl._

  override def request = HttpRoutes.of[F] {

    case req @ POST -> Root / "auth" / "register" => for {
      form     <- req.as[Form]
      result   <- authService.register(
        mkUsername(form.username),
        mkPassword(form.password)
      )
      response <- result match {
        case Right(value) => Ok(value)
        case Left(value)  => BadRequest(value)
      }
    } yield response  

    case req @ GET -> Root / "auth" / "login" => for {
      form     <- req.as[Form]
      result   <- authService.authenticate(
        mkUsername(form.username),
        mkPassword(form.password)
      )
      response <- result match {
        case Right(value) => value match {
          case Some(value) => Ok(value)
          case None        => Forbidden()
        }
        case Left(value)  => BadRequest(value)
      }
    } yield response
  }
  def authRequest: AuthedRoutes[User, F] = 

    AuthedRoutes.of[User, F] {
      case req @ PUT -> Root / "auth" / "refresh" as user => for {
        token  <- req.req.as[Token]
        result <- authService.refreshAuth(token)
        response <- result match {
          case None => Forbidden()
          case Some(value) => Ok(value)
        }
      } yield response
    }
}

object AuthRoute {
  def apply[F[_]: Defer: Sync](authService: AuthService[F]): Kleisli[F, Logger, AuthRoute[F]] = 
    Kleisli(log => new AuthRoute(log, authService).pure[F])
}
