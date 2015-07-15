package controllers

import actors.WebSocketHandler
import play.api.mvc.{WebSocket, Action, Controller}
import play.api.Play.current
import views.html.price

object InstrumentController extends Controller {
  def index = Action {
    Ok(views.html.price())
  }

  def prices = WebSocket.acceptWithActor[String, String] { req => out =>
    WebSocketHandler.props(out)
  }
}
