package handlers

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

object auth {

  class Registration(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.registration

    override def doGet(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        resp.getWriter.println(pages.auth.registration)
      }

    override def doPost(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        asyncHandle {
          db.run(postgres.Users.create(models.User.fromAuth(req.to[shared.Auth])))
            .map(_ => resp.sendRedirect(paths.authorization))
        }
      }

  }

  class Authorization(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.authorization

    override def doGet(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        resp.getWriter.println(pages.auth.authorization)
      }

    override def doPost(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        asyncHandle {
          val model = req.to[shared.Auth]
          db.run(postgres.Users.getByLogin(model.login)).map {
            case Some(account) if utils.passwordHashing.verifyPassword(account.passwordHash, model.password, account.salt) =>
              sessions.setSession(account.session, resp)
              resp.sendRedirect(paths.index)
            case _ => throw new Exception("Authentication failed")
          }
        }
      }

  }

  class Logout(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.logout

    override def doGet(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        resp.addCookie(cookies.expire(sessions.cookieKey))
        resp.getWriter.println(pages.auth.logout)
      }

  }

}
