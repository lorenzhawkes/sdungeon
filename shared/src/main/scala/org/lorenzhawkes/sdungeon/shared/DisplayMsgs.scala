package org.lorenzhawkes.sdungeon.shared

import akka.actor._
import pushka.annotation.pushka
import pushka.json._
import pushka.RW
import pushka.Ast

object DisplayMsgs {

  @pushka case class ChatMessage(value: String)
  
}
