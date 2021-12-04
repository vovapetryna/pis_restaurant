package handlers

import akka.http.scaladsl.server.Route
import postgres.Profile.api._

import scala.concurrent.ExecutionContext

object order {

  class Order(orderService: api.OrderService, menuService: api.MenuService)(implicit ec: ExecutionContext, db: Database)
      extends Routable.Service {
    val route: String = paths.order

    override def doGet(): Route =
      sessions.withClient { _ =>
        menuService.getAll
          .map(records => pages.order.create(records).complete)
          .orRejection("Failed to get orders")
      }

    override def doPost(form: Map[String, List[String]]): Route =
      sessions.withClient { session =>
        orderService
          .create(session.id, form.toModel[models.Order.Create])
          .map(_ => paths.orders.redirect)
          .orRejection("Failed to create order")
      }
  }

  class Orders(orderService: api.OrderService)(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.orders

    override def doGet(): Route =
      sessions.withClient { session =>
        orderService
          .getRepresentForClient(session.id)
          .map(records => pages.order.list(records).complete)
          .orRejection("Failed to get orders")
      } ~
        sessions.withAdmin { _ =>
          orderService.getRepresentForAdmin
            .map(records => pages.order.updateList(records).complete)
            .orRejection("Failed to get orders")
        }
  }

  class OrderUpdate(orderService: api.OrderService)(implicit ec: ExecutionContext, db: Database) extends Routable.Service {
    val route: String = paths.orderUpdate

    override def doPost(form: Map[String, List[String]]): Route =
      sessions.withAdmin { _ =>
        orderService
          .update(form.toModel[models.Order.Update])
          .map(_ => paths.orders.redirect)
          .orRejection("Failed to update order")
      }
  }

}
