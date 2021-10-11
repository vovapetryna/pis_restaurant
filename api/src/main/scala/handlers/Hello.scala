package handlers

import db.DbFactory
import jakarta.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import scala.concurrent.ExecutionContext

class Hello(implicit ec: ExecutionContext, dbFactory: DbFactory) extends HttpServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    resp.setContentType("application/json")
    resp.setStatus(HttpServletResponse.SC_OK)
    resp.getWriter.println(""" {"is_ok": true} """)
  }

}
