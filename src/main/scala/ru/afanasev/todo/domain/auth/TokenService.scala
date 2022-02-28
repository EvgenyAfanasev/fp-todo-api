package ru.afanasev.todo.domain.auth

import ru.afanasev.todo.domain.auth.repository.TokenRepositoryAlgebra
import ru.afanasev.todo.domain.subscriber.model.Subscriber
import ru.afanasev.todo.domain.auth.model._
import ru.afanasev.todo.utils.implicits._
import ru.afanasev.todo.config.Properties._

import cats._
import cats.implicits._
import cats.data._

import com.typesafe.scalalogging.Logger

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser.decode

import pdi.jwt._

import scala.util.Random

import java.time.Instant
import java.time.LocalDateTime

class TokenService[F[_]: Applicative](log: Logger, tokenRepository: TokenRepositoryAlgebra[F], authConfig: AuthProperties) {

  val random = new Random(System.currentTimeMillis)

  def generateToken(subscriber: Subscriber): F[Token] = 
    Token(
      id = 0, jwt = "jwt", 
      refresh = random.nextString(10).md5, 
      expireDate = LocalDateTime.now()
    ).pure[F]

  def decodeJwt(token: String) = {
    for {
      claims  <- JwtCirce.decode(
        token      = token,
        key        = authConfig.secret,
        algorithms = Seq(JwtAlgorithm.RS256)
      ).toOption
      subject <- claims.subject
      user    <- decode[User](subject).toOption
    } yield user
  }


  def encodeJwt(user: User) = {
    val claim = JwtClaim(
      expiration = Instant.now.plusSeconds(157784760).getEpochSecond.some,
      issuedAt = Instant.now.getEpochSecond.some,
      subject = user.asJson.show.some
    )
    JwtCirce.encode(claim, authConfig.secret, JwtAlgorithm.RS256)
  }
}

object TokenService {
  def apply[F[_]: Applicative](tokenRepository: TokenRepositoryAlgebra[F], authConfig: AuthProperties): Kleisli[F, Logger, TokenService[F]] =
    Kleisli(log => new TokenService[F](log, tokenRepository, authConfig).pure[F])
}