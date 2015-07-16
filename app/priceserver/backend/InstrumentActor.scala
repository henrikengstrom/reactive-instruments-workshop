package priceserver.backend

import akka.actor.Props

class InstrumentActor extends PriceActor {

  def receive: Receive = {
    case PriceActor.RetrieveCurrentPrice(instrument, responseTime) =>
      log.info(s"**** price request for instrument ${instrument} in ${context.self.path.name}")

      simulateSomeHeavyWork(responseTime)
      updateTrendCount()

      // poor mans JSON
      sender !
        s"""
          |{
          |  "instrument": "$instrument",
          |  "price": ${getCurrentPrice()},
          |  "timestamp": ${System.currentTimeMillis}
          |}
        """.stripMargin
  }
  
  def simulateSomeHeavyWork(workTime: Long): Unit = Thread.sleep(workTime)
}

object InstrumentActor {
  def props = Props[InstrumentActor]
}