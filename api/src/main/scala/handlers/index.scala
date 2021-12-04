package handlers

import akka.http.scaladsl.server.Route
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

object index {

  class Menu(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.index

    override def doGet(): Route =
      sessions.withSession { session =>
        if (session.role == models.Role.Client) paths.order.redirect
        else paths.menu.redirect
      }
  }

}
