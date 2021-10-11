import com.typesafe.config.{Config, ConfigFactory}
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

object Main extends App {

  val parallelism                   = Runtime.getRuntime.availableProcessors() + 1
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(new java.util.concurrent.ForkJoinPool(parallelism))
  implicit val config: Config       = ConfigFactory.load.resolve()
  implicit val db: Database         = Database.forConfig("postgresql", config)

  MyServer.start

}
