package ru.afanasev.todo.domain.todo

import cats.effect._
import cats.syntax.all._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import cats._
import cats.data.Kleisli
import com.typesafe.scalalogging.Logger
import ru.afanasev.todo.domain.Route
import ru.afanasev.todo.domain.subscriber.model.Subscriber
import org.http4s.dsl.Http4sDsl
import ru.afanasev.todo.domain.auth.model.User

import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s.circe._
import org.http4s.circe.CirceEntityCodec._
import ru.afanasev.todo.domain.todo.model.Todo

class TodoRoute[F[_]: Defer: Sync](log: Logger, todoService: TodoService[F]) extends Route[F] {

  implicit val dsl = Http4sDsl.apply[F]
  import dsl._

  override def request = HttpRoutes.of[F] {
    case _ => NotFound()
  }

  override def authRequest: AuthedRoutes[User, F] =
    AuthedRoutes.of[User, F] {
      case req @ GET -> Root / "todo" as user => for {
        result   <- todoService.findTodosBySubscriberId(user.id)
        response <- Ok(result)
      } yield response

      case req @ POST -> Root / "todo" as user => for {
        todo     <- req.req.as[Todo]
        result   <- todoService.createTodo(todo.sign(user.id))
        response <- Ok(result)
      } yield response

      case req @ GET -> Root / "todo" / id as user => for {
        todo     <- todoService.findTodoById(id.toLong, user.id)
        response <- todo match {
          case Some(value) => Ok(value)
          case None        => NotFound()
        }
      } yield response
    }

}

object TodoRoute {
  def apply[F[_]: Defer: Sync](todoService: TodoService[F]): Kleisli[F, Logger, TodoRoute[F]] = 
    Kleisli(log => new TodoRoute(log, todoService).pure[F])
}