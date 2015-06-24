package priceserver.backend

import akka.actor.{Actor, ActorLogging, Props}

class PriceController extends Actor with ActorLogging {
  import PriceController._
  var runningActors = 0
  val thresholdRunningActors = 2
  var responseTime = 0

  def receive = {
    case RetrieveCurrentPrice(instrument) =>
      // Each instrument has its own actor, forward the request to the responsible actor
      context.child(instrument).getOrElse ({
        incrementRunningActors()
        log.info(s"Creating new instrument actor: $instrument")
        context.actorOf(InstrumentActor.props(instrument), instrument)
      }) forward PriceActor.RetrieveCurrentPrice(responseTime)
  }

  def incrementRunningActors(): Unit = {
    runningActors = runningActors + 1
    log.info(s"Running actors is now: $runningActors")
    if (runningActors > thresholdRunningActors) responseTime = responseTime + 1000
  }
}

object PriceController {
  def props = Props[PriceController]
  case class RetrieveCurrentPrice(instrument: String)
  case class UpdateResponseTime(responseTimeMillis: Long)
}
