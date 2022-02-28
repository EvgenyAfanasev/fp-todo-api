package ru.afanasev.todo.config

import derevo.derive
import tofu.config._
import tofu.config.typesafe._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import cats._
import cats.implicits._
import cats.data.Kleisli
import com.typesafe.scalalogging.LazyLogging
import java.util.IllegalFormatException


class Properties[F[_]: Applicative] {

  import  Properties._

  def load = {
    val cfg = ConfigFactory.parseResources("application.conf")
    (syncParseConfig[ApplicationProperties](cfg) match {
      case Left(error)   => throw new Exception(error.show)
      case Right(config) => config 
    }).pure[F]
  }
}

object Properties {

  case class DatabaseProperties(url: String, user: String, password: String)

  case class AuthProperties(secret: String)

  @derive(Configurable)
  case class ApplicationProperties(database: DatabaseProperties, auth: AuthProperties)

  def apply[F[_]: Applicative]: Kleisli[F, Logger, ApplicationProperties] = 
    Kleisli(_ => new Properties[F].load)
}
