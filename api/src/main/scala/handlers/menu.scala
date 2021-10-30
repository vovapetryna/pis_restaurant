package handlers

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import postgres.Profile.api._

import scala.concurrent.ExecutionContext

object menu {

  class Menu(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.menu

    override def doGet(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        sessions.withAdmin { _ =>
          asyncHandle {
            db.run(postgres.MenuRecords.getAll).map { records =>
              resp.getWriter.print(pages.menu.build(records))
            }
          }
        }
      }

    override def doPost(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        sessions.withAdmin { _ =>
          asyncHandle {
            println(req.to[models.MenuRecord.Create])
            db.run(postgres.MenuRecords.create(models.MenuRecord.fromCreate(req.to[models.MenuRecord.Create])))
              .map(_ => resp.sendRedirect(paths.menu))
          }
        }
      }
  }

  class MenuUpdate(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.menuUpdate

    def delete(uE: models.MenuRecord.Update): DBIO[Boolean] =
      if (uE.delete) postgres.MenuRecords.deleteById(uE.id).map(_ == 1) else DBIO.successful(true)

    def update(uE: models.MenuRecord.Update): DBIO[Boolean] =
      if (!uE.delete) postgres.MenuRecords.update(models.MenuRecord.fromUpdate(uE)).map(_ == 1) else DBIO.successful(true)

    override def doPost(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        sessions.withAdmin { _ =>
          asyncHandle {
            val uE = req.to[models.MenuRecord.Update]
            db.run(for {
                updated <- update(uE)
                deleted <- delete(uE)
              } yield updated && deleted)
              .map(_ => resp.sendRedirect(paths.menu))
          }
        }
      }
  }

}
