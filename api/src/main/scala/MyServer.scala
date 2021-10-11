import org.eclipse.jetty.server.{NetworkConnector, Server}
import org.eclipse.jetty.servlet.{ServletHandler, ServletHolder}
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

object MyServer {
  private val server: Server = createServer()
  private val handler        = new ServletHandler()

  private def createServer() = new Server(9000)

  def port: Int = server.getConnectors()(0) match { case connector: NetworkConnector => connector.getLocalPort }

  private def build(implicit ec: ExecutionContext, db: Database): Unit = {
    server.setHandler(handler)

    (new handlers.Hello :: Nil)
      .map(servlet => new ServletHolder(servlet) -> servlet.route)
      .foreach { case (holder, route) => handler.addServletWithMapping(holder, route) }
  }

  def start(implicit ec: ExecutionContext, db: Database): Unit = {
    build
    server.start()
    println(s"Server started at port: $port")
    server.join()
  }
}
