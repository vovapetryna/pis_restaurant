import org.eclipse.jetty.server.{NetworkConnector, Server}
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

object MyServer {
  private val server: Server = new Server(9000)
  private val handler        = new ServletContextHandler()

  def port: Int = server.getConnectors()(0) match { case connector: NetworkConnector => connector.getLocalPort }

  private def build(implicit ec: ExecutionContext, db: Database): Unit = {
    handler.setContextPath("/")
    server.setHandler(handler)

    (new handlers.auth.Registration ::
      new handlers.auth.Authorization ::
      Nil)
      .map { servlet =>
        val holder = new ServletHolder(servlet)
        holder.setAsyncSupported(true)
        holder -> servlet.route
      }
      .foreach { case (holder, route) => handler.addServlet(holder, route) }
  }

  def start(implicit ec: ExecutionContext, db: Database): Unit = {
    build
    server.start()
    println(s"Server started at port: $port")
    server.join()
  }
}
