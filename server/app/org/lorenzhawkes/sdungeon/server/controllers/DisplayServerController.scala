package org.lorenzhawkes.sdungeon.server.controllers



import akka.actor._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.stream._
import akka.stream.actor._
import play.api.libs.streams._
import play.api.http.websocket.{Message, TextMessage}
import org.lorenzhawkes.sdungeon.shared.DisplayMsgs._
import org.lorenzhawkes.sdungeon.shared.DisplayMsgs.DisplayMessage._
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.inject.ApplicationLifecycle
import play.api.http.websocket.{Message => PlayMessage}
import play.api.http.websocket.{TextMessage => PlayTextMessage}
import pushka.json._

@Singleton
class DisplayServerController @Inject() (appLifecycle: ApplicationLifecycle) extends Controller {

  def msgFlow = run
  
  def run = {
    import DisplayServer.system
    implicit val flowMaterializer = ActorMaterializer()

    case class SourceWSHandler() extends ActorPublisher[Message] {
      import ActorPublisherMessage._

      override def preStart() = DisplayServer.manager ! DisplayServer.AddClient(self)

      def receive = {
        case msg: ChatMessage => onNext(TextMessage(write(Envelope(msg))))
      }

      override def postStop() = DisplayServer.manager ! DisplayServer.RemoveClient(self)
    }

    case class SinkWSHandler() extends ActorSubscriber {
      import ActorSubscriberMessage._

      override val requestStrategy = new MaxInFlightRequestStrategy(max = 1) {
        override def inFlightInternally: Int = 0
      }

      def receive = {
        case OnNext(TextMessage(text)) => DisplayServer.manager ! read[Envelope](text).message
      }
    }

    def actorSource = Source.actorPublisher[Message](Props(new SourceWSHandler()))
    def actorSink = Sink.actorSubscriber(Props(new SinkWSHandler()))

    Flow.fromSinkAndSource(actorSink, actorSource)
  }

  
  def display = Action {
    Ok(views.html.display("SDungeon"))
  }
        
  def api = WebSocket.accept[Message, Message] { request => msgFlow }
}
