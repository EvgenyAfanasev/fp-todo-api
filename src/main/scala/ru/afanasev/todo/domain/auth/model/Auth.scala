package ru.afanasev.todo.domain.auth.model

import cats.effect.Sync
import cats.implicits._
import cats.data.EitherT
import io.estatico.newtype.macros.newtype
import ru.afanasev.todo.utils.implicits._

object Auth {

  private val usernamePattern = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$".r

  private val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{6,}".r


  @newtype sealed abstract case class Username(val value: String)

  @newtype sealed abstract case class Password(val value: String)

  final case class Form(username: String, password: String)

  def mkUsername[F[_]: Sync](username: String): Either[List[String], Username] = {
    username match {
      case usernamePattern(value) => Username(value).asRight
      case _                      => List("incorrect username").asLeft
    }
  }

  def mkPassword[F[_]: Sync](password: String): Either[List[String], Password] = {
    password match {
      case passwordPattern(value) => Password(value.md5).asRight
      case _                      => List("incorrect password").asLeft
    }
  }
}
