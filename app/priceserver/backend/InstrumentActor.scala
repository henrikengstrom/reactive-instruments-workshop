package priceserver.backend

import akka.actor.Props

class InstrumentActor(instrumentId: String) extends PriceActor {

  def receive: Receive = {
    case PriceActor.RetrieveCurrentPrice(responseTime) =>
      Thread.sleep(responseTime)
      updateTrendCount()

      // poor mans JSON
      sender !
        s"""
          |{
          |  "instrument": "$instrumentId",
          |  "price": ${getCurrentPrice()},
          |  "timestamp": ${System.currentTimeMillis}
          |}
        """.stripMargin
  }
}

object InstrumentActor {
  def props(instrumentId: String) = Props(new InstrumentActor(instrumentId))
}