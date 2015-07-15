package priceserver.backend

import akka.actor.{Actor, ActorLogging}

import scala.util.Random

abstract class PriceActor extends Actor with ActorLogging {
  var maxPrice = 100
  var currentPrice = Random.nextInt(maxPrice)
  var maxFluctuation = 5

  var currentTrendIsPositive = Random.nextBoolean()
  var maxTrendInterval = 50
  var currentTrendInterval = Random.nextInt(maxTrendInterval)

  def getCurrentPrice(): Int = {
    val difference: Int = Random.nextInt(maxFluctuation)

    if (currentTrendIsPositive) {
      val potentialPrice = currentPrice + difference
      if (potentialPrice <= maxPrice) currentPrice = potentialPrice
    } else {
      val potentialPrice = currentPrice - difference
      if (potentialPrice > 0) currentPrice = potentialPrice
      else currentPrice = 0
    }

    currentPrice
  }
  
  def changePriceDirection() = {
    currentTrendIsPositive = !currentTrendIsPositive
    currentTrendInterval = Random.nextInt(maxTrendInterval)
  }

  def updateTrendCount(): Unit = {
    currentTrendInterval = currentTrendInterval - 1
    if (currentTrendInterval == 0) changePriceDirection() // dictates if the prices should be pos or neg
  }

}

object PriceActor {
  case class RetrieveCurrentPrice(responseTime: Long)
}