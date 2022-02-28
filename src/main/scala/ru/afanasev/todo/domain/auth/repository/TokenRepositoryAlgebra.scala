package ru.afanasev.todo.domain.auth.repository

import cats.data.OptionT
import ru.afanasev.todo.domain.auth.model.Token

trait TokenRepositoryAlgebra[F[_]] {

  def findByJwtAndRefresh(jwt: String, refresh: String): F[Option[Token]]

  def save(token: Token): F[Token]

  def update(token: Token): F[Token]
}
