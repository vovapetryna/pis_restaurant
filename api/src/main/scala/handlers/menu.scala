package handlers

import akka.http.scaladsl.server.Route
import postgres.Profile.api._

import scala.concurrent.ExecutionContext

object menu {

  class Menu(menuService: api.MenuService)(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.menu

    override def doGet(): Route =
      sessions.withAdmin { _ =>
        menuService.getAll
          .map(records => pages.menu.build(records).complete)
          .orRejection("Failed to get menu")
      }

    override def doPost(form: Map[String, List[String]]): Route =
      sessions.withAdmin { _ =>
        menuService
          .create(form.toModel[models.MenuRecord.Create])
          .map(_ => paths.menu.redirect)
          .orRejection("Failed to add menu record")
      }
  }

  class MenuUpdate(menuService: api.MenuService)(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.menuUpdate

    override def doPost(form: Map[String, List[String]]): Route =
      sessions.withAdmin { _ =>
        menuService
          .update(form.toModel[models.MenuRecord.Update])
          .map(_ => paths.menu.redirect)
          .orRejection("Failed to update or delete menu record")
      }
  }

}
