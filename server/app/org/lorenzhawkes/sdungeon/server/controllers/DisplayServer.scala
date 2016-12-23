package org.lorenzhawkes.sdungeon.server.controllers

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor
import akka.actor.ActorRef

object DisplayServer {

  case class AddClient(ref: ActorRef)
  case class RemoveClient(ref: ActorRef)
  
  implicit lazy val system = ActorSystem("display")

  lazy val manager = system.actorOf(Props(Manager()), "manager")

  case class Manager() extends Actor {
    import org.lorenzhawkes.sdungeon.shared.DisplayMsgs._

    def receive = operative()

    def operative(clients: List[ActorRef] = List()): Receive = {
      case AddClient(ref)    => context.become(operative(clients :+ ref))
      case RemoveClient(ref) => context.become(operative(clients.filterNot(_ == ref)))
      case msg: ChatMessage  => clients.foreach{c => c ! msg}
    }
  }

}