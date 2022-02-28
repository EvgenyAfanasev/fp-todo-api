package ru.afanasev.todo.domain.auth.repository

import com.typesafe.scalalogging.Logger

import cats.implicits._
import cats.data._
import cats._
import ru.afanasev.todo.domain.auth.model.Token

class TokenRepositoryInterpreter[F[_]](log: Logger) extends TokenRepositoryAlgebra[F] {

    def findByJwtAndRefresh(jwt: String, refresh: String): F[Option[Token]] = ???

    def save(token: Token): F[Token] = ???

    def update(token: Token): F[Token] = ???
}

object TokenRepositoryInterpreter {
  def apply[F[_]: Applicative]: Kleisli[F, Logger, TokenRepositoryInterpreter[F]] =
    Kleisli(log => new TokenRepositoryInterpreter[F](log).pure[F])
}