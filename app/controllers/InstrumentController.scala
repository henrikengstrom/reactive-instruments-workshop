package controllers

import play.api.mvc.{Action, Controller}
import play.twirl.api.Html

object InstrumentController extends Controller {
  def index = Action {
    Ok(Html("<h1>Instruments</h1>"))
  }
}
