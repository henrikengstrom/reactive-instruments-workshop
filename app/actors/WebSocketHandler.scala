package actors

import java.net.ConnectException
import java.util.concurrent.TimeoutException

import akka.actor._
import scala.concurrent.duration._
import play.api.libs.ws.WS

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

import play.api.libs.json._
import play.api.libs.functional.syntax._

class WebSocketHandler(out: ActorRef) extends Actor with ActorLogging {
  import WebSocketHandler._
  var lastPrice = 0.0
  var instrument: Option[String] = None

  def receive = {
    case json: JsValue =>
      instrument = (json \ "instrument").asOpt[String]
      instrument map { id =>
        instrument = Some(id)
        getCurrentPrice()
      }
    case GetCurrentPrice =>
      getCurrentPrice()
    case r: Result =>
      handleResult(r) map { json =>
        out ! json
        context.system.scheduler.scheduleOnce(2 seconds, self, GetCurrentPrice)
      }
    case e: Error =>
      out ! Json.obj("error" -> e.description)
      context.system.scheduler.scheduleOnce(2 seconds, self, GetCurrentPrice)
  }

  def getCurrentPrice(): Unit = {
    instrument map { id =>
      WS.url(priceServerUrl + id).withRequestTimeout(5000).get.map { resp =>
        self ! Result(resp.body)
      } recover {
        case e: ConnectException =>
          log.error(s"Could not connect to price server with ${priceServerUrl + id}. Make sure the server is running!")
          context.system.stop(self)
        case e: TimeoutException =>
          self ! Error(e.toString)
      }
    }
  }

  def handleResult(r: Result): Option[JsValue] = {
    val json = Json.parse(r.json)
    val priceInfoResult = json.validate[PriceInfo].asOpt
    for {
      pi <- priceInfoResult
    } yield {
      Json.obj(
        "instrument" -> pi.instrument,
        "price" -> pi.price,
        "timestamp" -> pi.timestamp,
        "fluctuation" -> fluctuation(pi.price)
      )
    }
  }

  def fluctuation(price: Double): Double = {
    val result =
      if (lastPrice == 0 || price == 0) 0.0
      else Math.abs(price - lastPrice) / price
    lastPrice = price
    result
  }
}

object WebSocketHandler {
  def props(out: ActorRef) = Props(new WebSocketHandler(out))
  final val priceServerUrl = "http://localhost:8080/instrument/"

  case object GetCurrentPrice
  case class Result(json: String)
  case class PriceInfo(instrument: String, price: Int, timestamp: Long)
  case class Error(description: String)

  implicit val priceInfoReads: Reads[PriceInfo] = (
    (JsPath \ "instrument").read[String] and
      (JsPath \ "price").read[Int] and
      (JsPath \ "timestamp").read[Long]
    )(PriceInfo.apply _)
}
