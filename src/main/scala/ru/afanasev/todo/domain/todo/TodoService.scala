package ru.afanasev.todo.domain.todo

import cats.data.Reader
import com.typesafe.scalalogging.Logger
import ru.afanasev.todo.config.Properties
import ru.afanasev.todo.domain.todo.repository.TodoRepositoryAlgebra
import cats.Applicative
import cats.data.Kleisli
import cats.implicits._
import ru.afanasev.todo.config.Properties._
import cats.Monad
import ru.afanasev.todo.domain.todo.model.Todo
import cats.data.OptionT

class TodoService[F[_]: Applicative](log: Logger, todoRepository: TodoRepositoryAlgebra[F]) {

  def findTodosBySubscriberId(id: Long): F[List[Todo]] = 
    todoRepository.findBySubscriberId(id)

  def createTodo(todo: Todo): F[Todo] = 
    todoRepository.save(todo)

  def findTodoById(id: Long, subscriberId: Long): F[Option[Todo]] = 
    todoRepository.findByIdAndSubscriberId(id, subscriberId)

  def updateTodo(todo: Todo): F[Todo] = 
    todoRepository.update(todo)

}

object TodoService {
  def apply[F[_]: Applicative](todoRepository: TodoRepositoryAlgebra[F]): Kleisli[F, Logger, TodoService[F]] = 
    Kleisli(log => new TodoService(log, todoRepository).pure[F])
}
