package ru.afanasev.todo

import com.typesafe.scalalogging.LazyLogging
import ru.afanasev.todo.config.Properties

import ru.afanasev.todo.domain.todo.TodoService
import ru.afanasev.todo.domain.todo.repository.TodoRepositoryInterpreter
import ru.afanasev.todo.domain.subscriber.repository.SubscriberRepositoryInterpreter
import ru.afanasev.todo.domain.subscriber.SubscriberService
import ru.afanasev.todo.domain.auth.TokenService
import ru.afanasev.todo.domain.auth.AuthService
import ru.afanasev.todo.domain.auth.repository.TokenRepositoryInterpreter
import ru.afanasev.todo.config.Properties._

import org.http4s.server._
import org.http4s.server.blaze._
import org.http4s.implicits._

import cats._
import cats.data._
import cats.implicits._
import cats.effect._

import com.typesafe.scalalogging.Logger
import ru.afanasev.todo.domain.subscriber.SubscriberRoute
import org.http4s.server.Router
import org.http4s.blaze.server.BlazeServerBuilder
import ru.afanasev.todo.domain.todo.TodoRoute
import ru.afanasev.todo.domain.auth.AuthRoute
import  scala.concurrent.ExecutionContext.Implicits.global
import org.http4s.HttpRoutes

object Server extends LazyLogging with IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    program[IO].run(logger)
            .map(routes => Router("/api" -> routes).orNotFound)
            .map(router => BlazeServerBuilder[IO](global).bindHttp(8080, "localhost").withHttpApp(router))
            .flatMap(server => server.serve.compile.drain).as(ExitCode.Success)
  }

  def program[F[_]: Monad: Async: Parallel] = for {
    config               <- Properties[F]
    todoRepository       <- TodoRepositoryInterpreter[F]
    subscriberRepository <- SubscriberRepositoryInterpreter[F]
    tokenRepository      <- TokenRepositoryInterpreter[F]
    subscriberService    <- SubscriberService[F](subscriberRepository)
    tokenService         <- TokenService[F](tokenRepository, config.auth)
    todoService          <- TodoService[F](todoRepository)
    authService          <- AuthService[F](subscriberService, tokenService)
    subscriberRoute      <- SubscriberRoute[F](subscriberService)
    todoRoute            <- TodoRoute[F](todoService)
    authRoute            <- AuthRoute[F](authService)
  } yield  List(authRoute, subscriberRoute, todoRoute)
    .map(routes => (routes.authRequest, routes.request))
    .map(routes => authService.middlware(routes._1) <+> routes._2)
    .reduce(_ <+> _)

  
}
