package handlers

import akka.http.scaladsl.server.Route
import postgres.Profile.api._

import scala.concurrent.ExecutionContext

object menu {

  class Menu(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.menu

    override def doGet(): Route =
      sessions.withAdmin { _ =>
        db.run(postgres.MenuRecords.getAll)
          .map(records => pages.menu.build(records).complete)
          .orRejection("Failed to get menu")
      }

    override def doPost(form: Map[String, List[String]]): Route =
      sessions.withAdmin { _ =>
        db.run(postgres.MenuRecords.create(models.MenuRecord.fromCreate(form.toModel[models.MenuRecord.Create])))
          .map(_ => paths.menu.redirect)
          .orRejection("Failed to add menu record")
      }
  }

  class MenuUpdate(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.menuUpdate

    def delete(uE: models.MenuRecord.Update): DBIO[Boolean] =
      if (uE.delete) postgres.MenuRecords.deleteById(uE.id).map(_ == 1) else DBIO.successful(true)

    def update(uE: models.MenuRecord.Update): DBIO[Boolean] =
      if (!uE.delete) postgres.MenuRecords.update(models.MenuRecord.fromUpdate(uE)).map(_ == 1) else DBIO.successful(true)

    override def doPost(form: Map[String, List[String]]): Route =
      sessions.withAdmin { _ =>
        val uE = form.toModel[models.MenuRecord.Update]
        db.run(for {
            updated <- update(uE)
            deleted <- delete(uE)
          } yield updated && deleted)
          .map(_ => paths.menu.redirect)
          .orRejection("Failed to update or delete menu record")
      }
  }

}
