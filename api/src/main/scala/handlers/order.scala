package handlers

import akka.http.scaladsl.server.Route
import postgres.Profile.api._

import scala.concurrent.ExecutionContext

object order {

  class Order(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.order

    override def doGet(): Route =
      sessions.withClient { _ =>
        db.run(postgres.MenuRecords.getAll)
          .map(records => pages.order.create(records).complete)
          .orRejection("Failed to get orders")
      }

    override def doPost(form: Map[String, List[String]]): Route =
      sessions.withClient { session =>
        println("from data", form)
        val create = form.toModel[models.Order.Create]
        println(create)
        db.run((for {
            order <- postgres.Orders.create(models.Order.forUser(session.id))
            records = models.OrderRecord.fromCreate(order.id, create)
            Some(recordsCount) <- postgres.OrderRecords.createAll(records) if recordsCount == records.size
          } yield true).transactionally)
          .map(_ => paths.orders.redirect)
          .orRejection("Failed to create order")
      }
  }

  class Orders(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.orders

    override def doGet(): Route =
      sessions.withClient { session =>
        db.run(postgres.Orders.getWithRecords(postgres.Orders.clientQuery(session.id)))
          .map(records => pages.order.list(records).complete)
          .orRejection("Failed to get orders")
      } ~
        sessions.withAdmin { _ =>
          db.run(postgres.Orders.getWithRecords(postgres.Orders.query))
            .map(records => pages.order.updateList(records).complete)
            .orRejection("Failed to get orders")
        }
  }

  class OrderUpdate(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.orderUpdate

    def update(uE: models.Order.Update): DBIO[Boolean] =
      postgres.Orders.update(uE.id, uE.status).map(_ == 1)

    override def doPost(form: Map[String, List[String]]): Route =
      sessions.withAdmin { _ =>
        val uE = form.toModel[models.Order.Update]
        db.run(update(uE))
          .map(_ => paths.orders.redirect)
          .orRejection("Failed to update order")
      }
  }

}
