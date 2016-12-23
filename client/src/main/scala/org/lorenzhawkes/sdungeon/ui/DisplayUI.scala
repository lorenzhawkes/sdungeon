package org.lorenzhawkes.sdungeon.ui

import org.scalajs.jquery.{jQuery=>$}

import akka.actor._

import org.scalajs.dom.document.{getElementById, location}

import scalatags.JsDom._
import scalatags.JsDom.all._

import org.scalajs.dom.raw._

import org.lorenzhawkes.sdungeon.shared.DisplayMsgs._
import org.lorenzhawkes.sdungeon.shared.DisplayMsgs.DisplayMessage._

import pushka.json._

object DisplayUI {

  implicit lazy val system = ActorSystem("displayUI")

  def start =
    system.actorOf(Props(ChatUI()), "page")

  case class ChatUI() extends DomActor {
    override val domElement = Some(getElementById("root"))

    val defaultAddress = s"${location.host}/display/api/chat"
    val urlBox = input("placeholder".attr := defaultAddress).render

    def template() = div(cls := "pure-g")(
      div(cls := "pure-u-1-3")(
        h2("Add chat server:"),
        div(cls := "pure-form")(
          urlBox,
          button(
            cls := "pure-button pure-button-primary",
            onclick := {
              val address = if (urlBox.value.isEmpty()) defaultAddress else urlBox.value
              () => context.actorOf(Props(ChatBox(address)))
          })("Connect")
        )
      )
    )
  }

  case class ChatBox(wsUrl: String) extends DomActorWithParams[List[String]] {

    val ws = new WebSocket(s"ws://$wsUrl")
    ws.onmessage = { (event: MessageEvent) => {println(s"recieved ${event.data.toString}"); self ! read[Envelope](event.data.toString).message}}

    val initValue = List()

    val msgBox = input("placeholder".attr := "enter message").render

    def template(txt: List[String]) = div(cls := "pure-u-1-3")(
      h3(s"Server: $wsUrl"),
      msgBox,
      button(
        cls := "pure-button pure-button-primary",
        onclick := {() => ws.send(write(Envelope(MonkeyMessage(msgBox.value))))})("Send"),
      ul(cls := "pure-menu-list")(
        for (t <- txt) yield li(cls := "pure-menu-item")(t)
      ),
      hr(),
      button(
        cls := "pure-button pure-button-primary",
        onclick := {() => self ! PoisonPill})("Close")
    )

    override def operative = withText(initValue)

    def withText(last: List[String]): Receive = domManagement orElse {      
      case ChatMessage(txt) =>
        val newTxt = (last :+ txt).takeRight(5)
        self ! UpdateValue(newTxt)
        context.become(withText(newTxt))
    }
  }

}