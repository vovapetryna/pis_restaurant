import akka.event.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server._
import postgres.Profile.api.Database

import scala.concurrent.ExecutionContext

object MyServer extends Directives {
  import handlers._

  private implicit def myRejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle { case BadServiceRejection(error) => complete(HttpResponse(StatusCodes.BadRequest, entity = s"Service error: $error")) }
      .handle { case cookies.EmptyCookieRejection => paths.authorization.redirect }
      .handle { case sessions.SessionDecodingRejection => paths.authorization.redirect }
      .handle { case sessions.PermissionDeniedRejection => paths.authorization.redirect }
      .handleNotFound { complete(HttpResponse(StatusCodes.NotFound, entity = "Not here!")) }
      .result()

  def routes(implicit ec: ExecutionContext, db: Database): Route = {
    val accountService: api.AccountService = new services.AccountServiceImpl()
    val menuService: api.MenuService       = new services.MenuServiceImpl()
    val orderService: api.OrderService     = new services.OrderServiceImpl()

    directives.DebuggingDirectives.logRequestResult("api", Logging.InfoLevel) {
      handleRejections(myRejectionHandler) {
        new auth.Registration(accountService).routes ~
          new auth.Authorization(accountService).routes ~
          (new auth.Logout).routes ~
          (new index.Menu).routes ~
          new menu.Menu(menuService).routes ~
          new menu.MenuUpdate(menuService).routes ~
          new order.Order(orderService, menuService).routes ~
          new order.Orders(orderService).routes ~
          new order.OrderUpdate(orderService).routes
      }
    }
  }

}
