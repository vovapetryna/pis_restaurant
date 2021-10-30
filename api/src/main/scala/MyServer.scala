import jakarta.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server.{NetworkConnector, Server}
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

object MyServer {
  private val server: Server   = new Server(9000)
  private val handler          = new ServletContextHandler()
  private val rootPath: String = "/"

  handler.setContextPath(rootPath)
  server.setHandler(handler)

  def port: Int = server.getConnectors()(0) match { case connector: NetworkConnector => connector.getLocalPort }

  class RootServlet(services: List[handlers.Routable.Service]) extends HttpServlet {
    override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit =
      services.find(_.route == req.getRequestURI).foreach(_.doGet(req, resp))

    override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit =
      services.find(_.route == req.getRequestURI).foreach(_.doPost(req, resp))
  }

  private def build(implicit ec: ExecutionContext, db: Database): Unit = {
    val services = new handlers.index.Menu ::
      new handlers.auth.Registration ::
      new handlers.auth.Authorization ::
      new handlers.auth.Logout ::
      new handlers.order.Order ::
      new handlers.order.Orders ::
      new handlers.order.OrderUpdate ::
      new handlers.menu.Menu ::
      new handlers.menu.MenuUpdate ::
      Nil

    val rootServlet = new RootServlet(services)
    val rootHolder  = new ServletHolder(rootServlet)
    rootHolder.setAsyncSupported(true)
    handler.addServlet(rootHolder, rootPath)
  }

  def start(implicit ec: ExecutionContext, db: Database): Unit = {
    build
    server.start()
    println(s"Server started at port: $port")
    server.join()
  }
}
