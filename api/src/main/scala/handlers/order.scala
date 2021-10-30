package handlers

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import postgres.Profile.api._

import scala.concurrent.ExecutionContext

object order {

  class Order(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.order

    override def doGet(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        sessions.withClient { _ =>
          asyncHandle {
            db.run(postgres.MenuRecords.getAll).map { records =>
              resp.getWriter.print(pages.order.create(records))
            }
          }
        }
      }

    override def doPost(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        sessions.withClient { session =>
          asyncHandle {
            val create = req.to[models.Order.Create]
            db.run((for {
                order <- postgres.Orders.create(models.Order.forUser(session.id))
                records = models.OrderRecord.fromCreate(order.id, create)
                Some(recordsCount) <- postgres.OrderRecords.createAll(records) if recordsCount == records.size
              } yield true).transactionally)
              .map(_ => resp.sendRedirect(paths.orders))
          }
        }
      }
  }

  class Orders(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.orders

    override def doGet(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        sessions.withSession[shared.Session] { session =>
          if (session.role == models.Role.Client)
            asyncHandle {
              db.run(postgres.Orders.getWithRecords(postgres.Orders.clientQuery(session.id)))
                .map(records => resp.getWriter.print(pages.order.list(records)))
            } else
            asyncHandle {
              db.run(postgres.Orders.getWithRecords(postgres.Orders.query))
                .map(records => resp.getWriter.print(pages.order.updateList(records)))
            }
        }
      }
  }

  class OrderUpdate(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.orderUpdate

    def update(uE: models.Order.Update): DBIO[Boolean] =
      postgres.Orders.update(uE.id, uE.status).map(_ == 1)

    override def doPost(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      base {
        sessions.withAdmin { _ =>
          asyncHandle {
            val uE = req.to[models.Order.Update]
            db.run(update(uE))
              .map(_ => resp.sendRedirect(paths.orders))

          }
        }
      }
  }

}
