package ru.afanasev.todo.domain

import org.http4s.HttpRoutes
import org.http4s.EntityEncoder
import org.http4s.Response
import cats.effect.Sync
import org.http4s.Status
import org.http4s.dsl.Http4sDsl
import org.http4s._
import org.http4s.circe._
import ru.afanasev.todo.domain.subscriber.model.Subscriber
import ru.afanasev.todo.domain.auth.model.User

trait Route[F[_]] {
  def request: HttpRoutes[F]

  def authRequest: AuthedRoutes[User, F]
}
