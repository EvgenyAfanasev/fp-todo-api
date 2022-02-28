package ru.afanasev.todo.utils

import java.security.MessageDigest

object implicits {

  implicit class Md5(value: String) {
    def md5 = MessageDigest
      .getInstance("MD5")
      .digest(value.getBytes("UTF-8"))
      .map("%02x".format(_))
      .mkString
  }
}
