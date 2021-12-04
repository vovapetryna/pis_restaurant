import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.typesafe.config.{Config, ConfigFactory}
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

object Main extends App {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "api")
  implicit val ec: ExecutionContext         = system.executionContext
  implicit val config: Config               = ConfigFactory.load.resolve()
  implicit val db: Database                 = Database.forConfig("postgresql", config)

  db.run(postgres.init).onComplete(println)

  val host: String = "localhost"
  val port: Int    = 9001

  val binding = Http().newServerAt(host, port).bind(MyServer.routes)

  binding.onComplete {
    case scala.util.Success(_) =>
      println(s"=== Server is UP at http://$host:$port/ ===")
    case scala.util.Failure(ex) =>
      println(s"Failed to bind to $host:$port!")
      println(ex)
      sys.exit(1)
  }

  sys.addShutdownHook {
    binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }

}
