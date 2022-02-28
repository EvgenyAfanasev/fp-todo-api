package ru.afanasev.todo.domain.subscriber.repository

import ru.afanasev.todo.domain.subscriber.model.Subscriber
import cats.data.OptionT

trait SubscriberRepositoryAlgebra[F[_]] {

  def findByUsernameAndPassword(username: String, password: String): F[Option[Subscriber]]

  def updateByIdUsernameAndPassword(id: Long, username: String, password: String): F[Option[Subscriber]]

  def findById(id: Long): F[Option[Subscriber]]

  def findByUsername(username: String): F[Option[Subscriber]]

  def save(subscriber: Subscriber): F[Subscriber]
}
