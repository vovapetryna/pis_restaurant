package handlers

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import postgres.Profile.api.Database

import scala.concurrent.{ExecutionContext, Future}

object auth {

  class Registration(implicit ec: ExecutionContext, db: Database) extends RoutableServlet {
    val route = "/registration"

    override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit =
      resp.getWriter.println(pages.authentication.build("Registration", route))

    override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit =
      asyncHandle {
        db.run(postgres.Users.create(models.User.fromAuth(req.to[shared.Auth])))
          .map(inserted => resp.getWriter.println(s"inserted $inserted"))
      }(req, resp, ec)

  }

  class Authorization(implicit ec: ExecutionContext, db: Database) extends RoutableServlet {
    val route = "/authorization"

    override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit =
      resp.getWriter.println(pages.authentication.build("Authorization", route))

    override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit =
      asyncHandle {
        Future.successful(println(req.to[shared.Auth]))
      }(req, resp, ec)

  }

}
