package priceserver.backend

import akka.actor.{Props, ActorLogging, Actor}
import akka.cluster.Cluster
import akka.contrib.pattern.ClusterReceptionistExtension
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.FromConfig

class PriceService extends Actor with ActorLogging {
  val c = Cluster(context.system)

  log.info("**** Price Service is started ****")

  // Register this instance to the receptionist so that external clients (AkkaHttp) can call it
  ClusterReceptionistExtension(context.system).registerService(self)

  val router = context.actorOf(FromConfig.props(Props[InstrumentActor]), "instrumentActor")

  def receive = {
    case rcp @ PriceActor.RetrieveCurrentPrice(instrument, 0L) =>
      router forward ConsistentHashableEnvelope(rcp, rcp.instrument)
  }
}

object PriceService {
  def props = Props[PriceService]
}
