package controllers

import actors.WebSocketHandler
import play.api.libs.json.JsValue
import play.api.mvc.{WebSocket, Action, Controller}
import play.twirl.api.Html
import play.api.Play.current

object InstrumentController extends Controller {
  def index = Action {
    Ok(views.html.price())
  }

  def prices = WebSocket.acceptWithActor[JsValue, JsValue] { req => out =>
    WebSocketHandler.props(out)
  }
}
