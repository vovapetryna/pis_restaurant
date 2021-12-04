package handlers

import akka.http.scaladsl.server.Route
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

object auth {

  class Registration(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.registration

    override def doGet(): Route = pages.auth.registration.complete

    override def doPost(form: Map[String, List[String]]): Route = {
      val model: shared.Auth = form.toModel[shared.Auth]
      db.run(postgres.Users.create(models.User.fromAuth(model)))
        .map(_ => paths.authorization.redirect)
        .orRejection("Failed to register user")
    }

  }

  class Authorization(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.authorization

    override def doGet(): Route = pages.auth.authorization.complete

    override def doPost(form: Map[String, List[String]]): Route = {
      val model = form.toModel[shared.Auth]
      db.run(postgres.Users.getByLogin(model.login))
        .map {
          case Some(account) if utils.passwordHashing.verifyPassword(account.passwordHash, model.password, account.salt) =>
            sessions.setSession(account.session) {
              paths.index.redirect
            }
        }
        .orRejection("Failed to Authorize")
    }
  }

  class Logout(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.logout

    override def doGet(): Route =
      cookies.expire(sessions.cookieKey) {
        pages.auth.logout.complete
      }
  }

}
