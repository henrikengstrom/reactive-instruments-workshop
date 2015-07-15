package priceserver.frontend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import priceserver.backend.PriceController

import scala.concurrent.duration._

/**
 * Price service that handles price information for instruments.
 * When running it starts an Akka Http server on port 8080 and handles calls to "instrument".
 *
 * Start with:
 * > sbt "runMain priceserver.frontend.RestMain"
 */
object RestMain extends App {
  implicit val system = ActorSystem("priceSystem")
  implicit val materializer = ActorMaterializer()

  val priceController = system.actorOf(PriceController.props, "priceCache")
  implicit val timeout: Timeout = Timeout(60 seconds) // Note that this is for simulation only - never use this in production!

  val route = {
    pathPrefix("instrument") {
      (get & path(Segment)) { id =>
        complete {
          (priceController ? PriceController.RetrieveCurrentPrice(id)).mapTo[String]
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
