package ru.afanasev.todo.domain.subscriber.repository

import ru.afanasev.todo.domain.subscriber.model.Subscriber

import com.typesafe.scalalogging.Logger

import cats.implicits._
import cats.data._
import cats._

class SubscriberRepositoryInterpreter[F[_]](log: Logger) extends SubscriberRepositoryAlgebra[F] {

  override def findByUsernameAndPassword(username: String, password: String): F[Option[Subscriber]] = ???

  override def updateByIdUsernameAndPassword(id: Long, username: String, password: String): F[Option[Subscriber]] = ???

  override def findById(id: Long): F[Option[Subscriber]] = ???

  override def findByUsername(username: String): F[Option[Subscriber]] = ???

  override def save(subscriber: Subscriber): F[Subscriber] = ???
}

object SubscriberRepositoryInterpreter {
  def apply[F[_]: Applicative]: Kleisli[F, Logger, SubscriberRepositoryInterpreter[F]] =
    Kleisli(log => new SubscriberRepositoryInterpreter[F](log).pure[F])
}
