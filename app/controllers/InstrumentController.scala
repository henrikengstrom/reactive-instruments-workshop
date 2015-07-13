package controllers

import actors.WebSocketHandler
import play.api.mvc.{WebSocket, Action, Controller}
import play.twirl.api.Html
import play.api.Play.current

object InstrumentController extends Controller {
  def index = Action {
    Ok(Html("<h1>Instruments</h1>"))
  }

  def prices = WebSocket.acceptWithActor[String, String] { req => out =>
    WebSocketHandler.props(out)
  }
}
