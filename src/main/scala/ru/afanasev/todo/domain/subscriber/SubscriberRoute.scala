package ru.afanasev.todo.domain.subscriber

import cats.effect._
import cats.syntax.all._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

import cats.Defer
import com.typesafe.scalalogging.Logger
import cats.data.Kleisli
import cats.Applicative
import ru.afanasev.todo.domain.Route
import ru.afanasev.todo.domain.subscriber.model.Subscriber
import org.http4s.dsl.Http4sDsl
import ru.afanasev.todo.domain.auth.model._
import ru.afanasev.todo.domain.auth.model.Auth._

import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._

class SubscriberRoute[F[_]: Defer: Sync](log: Logger, subscriberService: SubscriberService[F]) extends Route[F] {

  implicit val dsl = Http4sDsl.apply[F]
  import dsl._

  override def request = HttpRoutes.of[F] {
    case _ => NotFound()
  }

  override def authRequest: AuthedRoutes[User, F] = 
    AuthedRoutes.of[User, F] {
      case req @ PUT -> Root / "subscriber" / "password" as user => for {
        form     <- req.req.as[Form]
        result   <- subscriberService.changeProfile(
          subscriberId = user.id,
          username     = mkUsername(form.username),
          password     = mkPassword(form.password)
        )
        response <- result match {
          case Left(value)  => BadRequest(value)
          case Right(value) => value match {
            case None        => NotFound()
            case Some(value) => Ok(value)
          }
        }
      } yield response
    }
}

object SubscriberRoute {
  def apply[F[_]: Defer: Sync](subscriberService: SubscriberService[F]): Kleisli[F, Logger, SubscriberRoute[F]] = 
    Kleisli(log => new SubscriberRoute(log, subscriberService).pure[F])
}
