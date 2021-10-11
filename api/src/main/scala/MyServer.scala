import db.DbFactory
import org.eclipse.jetty.server.{NetworkConnector, Server}
import org.eclipse.jetty.servlet.ServletHandler

import scala.concurrent.ExecutionContext

object MyServer {
  private val server: Server = createServer()
  private val handler        = new ServletHandler()

  private def createServer() = new Server(9000)

  def port: Int = server.getConnectors()(0) match { case connector: NetworkConnector => connector.getLocalPort }

  private def build(implicit ec: ExecutionContext, dbFactory: DbFactory): Unit = {
    server.setHandler(handler)



    handler.addServletWithMapping(classOf[handlers.Hello], "/hello")
  }

  def start(implicit ec: ExecutionContext, dbFactory: DbFactory): Unit = {
    build
    server.start()
    println(s"Server started at port: $port")
    server.join()
  }
}
