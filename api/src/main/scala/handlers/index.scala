package handlers

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

object index {

  class Menu(implicit ec: ExecutionContext, db: Database) extends RoutableServlet {
    val route: String = paths.index

    override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
      implicit val (r, s) = (req, resp)
      base {
        sessions.withSession[shared.Session] { session =>
          if (session.role == models.Role.Client) resp.sendRedirect(paths.order)
          else resp.sendRedirect(paths.menu)
        }
      }
    }
  }

}
