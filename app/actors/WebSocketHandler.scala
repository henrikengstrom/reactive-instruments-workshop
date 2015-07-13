package actors

import akka.actor.{Props, ActorRef, Actor, ActorLogging}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class WebSocketHandler(out: ActorRef) extends Actor with ActorLogging {
  import WebSocketHandler._

  def receive = {
    case x: String =>
      out ! s"Websocket connection initiated"
      self ! SendResponse
    case SendResponse =>
      out ! s"Hello from WebSocketHandler at ${new java.util.Date}"
      context.system.scheduler.scheduleOnce(2 seconds, self, SendResponse)
  }
}

object WebSocketHandler {
  def props(out: ActorRef) = Props(new WebSocketHandler(out))
  case object SendResponse
}
