package org.lorenzhawkes.sdungeon.shared

import akka.actor._
import pushka.annotation.pushka
import pushka.json._
import pushka.RW
import pushka.Ast

object DisplayMsgs {

	@pushka case class Envelope(id: Int, message: DisplayMessage)	
	
  @pushka sealed trait DisplayMessage
  
  object DisplayMessage {
    case class ChatMessage(value: String) extends DisplayMessage
    case class MonkeyMessage(value: String) extends DisplayMessage
	}
	
	object Envelope {
	  
		def apply(message: DisplayMessage) : Envelope = {
			import DisplayMessage._
			message match {
				case msg : ChatMessage => Envelope(1, msg)
				case msg : MonkeyMessage => Envelope(2, msg)
			}
		}  	  
	}
  
}
