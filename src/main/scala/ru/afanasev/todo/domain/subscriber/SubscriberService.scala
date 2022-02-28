package ru.afanasev.todo.domain.subscriber

import ru.afanasev.todo.domain.subscriber.model.Subscriber
import ru.afanasev.todo.utils.implicits._
import ru.afanasev.todo.domain.subscriber.repository.SubscriberRepositoryAlgebra
import cats.implicits._
import cats.data.OptionT
import com.typesafe.scalalogging.Logger
import cats.data.Kleisli
import cats.Applicative
import cats.Monad
import cats.data.EitherT
import cats.Foldable
import ru.afanasev.todo.domain.auth.model.Auth._

class SubscriberService[F[_]: Monad](log: Logger, subscriberRepository: SubscriberRepositoryAlgebra[F]) {

  def findSubscriberByUsernameAndPassword(username: String, password: String): F[Option[Subscriber]] = 
    subscriberRepository.findByUsernameAndPassword(username, password)

  def changeProfile(subscriberId: Long, username: Either[List[String], Username], password: Either[List[String], Password]) = 
    EitherT((username, password).parTupled.pure[F]).flatMap {
      case (username, password) => 
        EitherT(subscriberRepository.updateByIdUsernameAndPassword(subscriberId, username.value, password.value)
          .map(_.asRight[List[String]]))
    }.value

  def addSubscriber(subscriber: Subscriber): F[Either[List[String], Subscriber]] = 
    for {
      exists     <- subscriberRepository.findByUsername(subscriber.username)
      subscriber <- (exists match {
        case None        => subscriberRepository.save(subscriber).asRight
        case Some(value) => List("subscriber already exists").asLeft
      }).sequence
    } yield subscriber
}

object SubscriberService {
  def apply[F[_]: Monad](subscriberRepository: SubscriberRepositoryAlgebra[F]): Kleisli[F, Logger, SubscriberService[F]] =
    Kleisli(log => new SubscriberService[F](log, subscriberRepository).pure[F])
}
