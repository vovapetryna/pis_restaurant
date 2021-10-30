package handlers

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

object Routable {
  trait Service {
    val route: String
    def doGet(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit  = resp.getWriter.println("undefined")
    def doPost(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit = resp.getWriter.println("undefined")
  }
}
