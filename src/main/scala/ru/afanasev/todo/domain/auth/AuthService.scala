package ru.afanasev.todo.domain.auth

import ru.afanasev.todo.domain.subscriber.SubscriberService
import ru.afanasev.todo.domain.subscriber.model._
import ru.afanasev.todo.domain.auth.repository.TokenRepositoryAlgebra
import ru.afanasev.todo.domain.auth.model._
import ru.afanasev.todo.domain.auth.model.Auth._

import cats._
import cats.data._
import cats.implicits._
import cats.effect._
import org.typelevel.ci._

import com.typesafe.scalalogging.Logger

import scala.util.Random
import org.http4s.Request
import org.http4s.server.AuthMiddleware

class AuthService[F[_]: Monad: Parallel: Sync](log: Logger, subscriberService: SubscriberService[F], tokenService: TokenService[F]) {

  def authenticate(username: Either[List[String], Username], password:Either[List[String], Password]) = 
    EitherT((username, password).parTupled.pure[F]).flatMap {
      case (username, password) => {
        val token = for {
          subscriber <- OptionT(subscriberService.findSubscriberByUsernameAndPassword(username.value, password.value))
          token      <- OptionT.liftF(tokenService.generateToken(subscriber))
        } yield token
        EitherT(token.value.map(_.asRight[List[String]]))
      }
    }.onError {
      case errors => EitherT.liftF(log.error(s"user ${username} can not authenticated because $errors").pure[F])
    }.value
  
  def register(username: Either[List[String], Username], password: Either[List[String], Password]) = 
    EitherT((username, password).parTupled.pure[F]).flatMap {
      case (username, password) => 
        EitherT(subscriberService.addSubscriber(Subscriber(username.value, password.value)))
    }.onError {
      case errors => EitherT.liftF(log.error(s"user ${username} can not registered because $errors").pure[F])
    }.value

  def refreshAuth(token: Token): F[Option[Token]] = ???

  val middlware = AuthMiddleware(authUser)

  private def authUser: Kleisli[OptionT[F, *], Request[F], User] = 
    Kleisli { request => for {
        header <- OptionT.fromOption[F](request.headers.get(ci"auth-token"))
        jwt    <- OptionT.pure[F](header.head.value)
        user   <- OptionT.fromOption[F](tokenService.decodeJwt(jwt))
      } yield user
    } 
  
}

object AuthService {
  def apply[F[_]: Monad: Parallel: Sync](subscriberService: SubscriberService[F], tokenService: TokenService[F]): Kleisli[F, Logger, AuthService[F]] =
    Kleisli(log => new AuthService[F](log, subscriberService, tokenService).pure[F])
}