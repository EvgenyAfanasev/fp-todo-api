package ru.afanasev.todo

sealed trait Error

final case class IncorrectUsernameError() extends Error
final case class IncorrectPasswordError() extends Error