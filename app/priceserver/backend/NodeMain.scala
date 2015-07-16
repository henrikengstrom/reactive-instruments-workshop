package priceserver.backend

import akka.actor.{ActorSystem, PoisonPill}
import akka.contrib.pattern.{ClusterSingletonManager, ClusterSingletonProxy}
import com.typesafe.config.ConfigFactory

/**
 * Serves as a cluster node in the backend.
 *
 * Should be started like this:
 * {{{
 * sbt "run-main priceserver.backend.NodeMain <PORT>"
 * }}}
 */
object NodeMain {
  val conf =
    """
    akka {
      loglevel = "INFO"
      log-dead-letters = 0

      actor {
        provider = "akka.cluster.ClusterActorRefProvider"

        deployment {
          /singletonManager/priceService/instrumentActor {
            router = consistent-hashing-pool
            nr-of-instances = 10
            cluster {
              enabled = on
              max-nr-of-instances-per-node = 2
              allow-local-routees = on
            }
          }
        }
      }
      remote {
        log-remote-lifecycle-events = off
        netty.tcp {
          hostname = "127.0.0.1"
          port = 0
        }
      }

      cluster {
        seed-nodes = [
          "akka.tcp://ClusterSystem@127.0.0.1:2551",
          "akka.tcp://ClusterSystem@127.0.0.1:2552"]
      }

      extensions = ["akka.contrib.pattern.ClusterReceptionistExtension"]
    }
    """

  def main(args: Array[String]): Unit = {
    val port = if(args.isEmpty) 0 else args(0)
    val system = ActorSystem("ClusterSystem", ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").withFallback(ConfigFactory.parseString(conf)))

    system.actorOf(ClusterSingletonManager.props(
      singletonProps = PriceService.props,
      singletonName = "priceService",
      terminationMessage = PoisonPill,
      role = None),
      name = "singletonManager")

    // Needs to be started on every node to keep track of where the single master exists
    system.actorOf(ClusterSingletonProxy.props(
      singletonPath = "/user/singleton/priceService",
      role = None))

    println(s"Cluster node started.\nPress RETURN to stop...")
    Console.in.readLine()
    system.shutdown()
  }
}
