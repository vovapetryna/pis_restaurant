package handlers

import jakarta.servlet.http.HttpServlet

trait RoutableServlet extends HttpServlet {
  val route: String
}
