package priceserver.frontend

import akka.actor.ActorSystem
import akka.contrib.pattern.ClusterClient
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import priceserver.backend.PriceActor

import scala.concurrent.duration._

/**
 * Price service that handles price information for instruments.
 * When running it starts an Akka Http server on port 8080 and handles calls to "instrument".
 *
 * Start with:
 * > sbt "runMain priceserver.frontend.RestMain"
 */
object RestMain extends App {
  implicit val system = ActorSystem("priceSystem", ConfigFactory.parseString(
    """
    akka {
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
      }

      remote {
        transport = "akka.remote.netty.NettyRemoteTransport"
        log-remote-lifecycle-events = off
        netty.tcp {
          hostname = "127.0.0.1"
          port = 5001
        }
      }
    }
    """
  ))

  implicit val materializer = ActorMaterializer()
  implicit val timeout: Timeout = Timeout(60 seconds) // Note that this is for simulation only - never use this long timeout in production!

  val initialContacts = Set(
    system.actorSelection("akka.tcp://ClusterSystem@127.0.0.1:2551/user/receptionist"),
    system.actorSelection("akka.tcp://ClusterSystem@127.0.0.1:2552/user/receptionist"))

  val clusterClient = system.actorOf(ClusterClient.props(initialContacts), "clusterClient")

  val route = {
    get {
      path("instrument" / Segment) { id =>
        complete {
          (clusterClient ? ClusterClient.Send("/user/singletonManager/priceService", PriceActor.RetrieveCurrentPrice(id), true)).mapTo[String]
        }
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  Console.in.readLine()

  import system.dispatcher // for the future transformations
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ system.shutdown()) // and shutdown when done
}
