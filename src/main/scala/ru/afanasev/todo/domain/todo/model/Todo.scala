package ru.afanasev.todo.domain.todo.model

case class Todo(id: Long, title: String, description: String, subscriberId: Long) {
  def sign(subscriberId: Long) = this.copy(subscriberId = subscriberId)
}
