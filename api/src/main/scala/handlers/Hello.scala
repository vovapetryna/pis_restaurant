package handlers

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

class Hello(implicit ec: ExecutionContext, db: Database) extends RoutableServlet {

  val route = "/hello"

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    resp.getWriter.println(pages.error.build("sheet happens"))

}
