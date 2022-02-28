package ru.afanasev.todo.domain.auth.model

import java.time.LocalDateTime

final case class Token(id: Long, jwt: String, refresh: String, expireDate: LocalDateTime)