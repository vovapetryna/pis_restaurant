package handlers

import akka.http.scaladsl.server.Route
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

object auth {

  class Registration(accountService: api.AccountService)(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.registration

    override def doGet(): Route = pages.auth.registration.complete

    override def doPost(form: Map[String, List[String]]): Route =
      accountService
        .create(form.toModel[shared.Auth])
        .map(_ => paths.authorization.redirect)
        .orRejection("Failed to register user")
  }

  class Authorization(accountService: api.AccountService)(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.authorization

    override def doGet(): Route = pages.auth.authorization.complete

    override def doPost(form: Map[String, List[String]]): Route =
      accountService
        .authorize(form.toModel[shared.Auth])
        .map(account => sessions.setSession(account.session) { paths.index.redirect })
        .orRejection("Failed to Authorize")
  }

  class Logout(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.logout

    override def doGet(): Route = cookies.expire(sessions.cookieKey) { pages.auth.logout.complete }
  }

}
