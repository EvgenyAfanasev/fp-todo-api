package ru.afanasev.todo.domain.todo.repository

import ru.afanasev.todo.domain.todo.model._
import cats.data.OptionT

trait TodoRepositoryAlgebra[F[_]] {
  
  def findByIdAndSubscriberId(id: Long, subscriberId: Long): F[Option[Todo]]

  def findBySubscriberId(id: Long): F[List[Todo]]

  def save(todo: Todo): F[Todo]

  def update(todo: Todo): F[Todo]
}
