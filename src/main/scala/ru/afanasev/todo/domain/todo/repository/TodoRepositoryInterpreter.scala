package ru.afanasev.todo.domain.todo.repository

import cats.data.Reader
import com.typesafe.scalalogging.Logger
import ru.afanasev.todo.domain.todo.model._
import cats.Applicative
import cats.data.Kleisli
import cats.syntax.applicative._
import cats.data.OptionT

final class TodoRepositoryInterpreter[F[_]](log: Logger) extends TodoRepositoryAlgebra[F] {

  override def findByIdAndSubscriberId(id: Long, subscriberId: Long): F[Option[Todo]] = ???

  override def findBySubscriberId(id: Long): F[List[Todo]] = ???

  override def save(todo: Todo): F[Todo] = ???

  override def update(todo: Todo): F[Todo] = ???
}

object TodoRepositoryInterpreter {
  def apply[F[_]: Applicative]: Kleisli[F, Logger, TodoRepositoryInterpreter[F]] = Kleisli(log => new TodoRepositoryInterpreter(log).pure[F])
}
