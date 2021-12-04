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

  def routes(implicit ec: ExecutionContext, db: Database): Route =
    directives.DebuggingDirectives.logRequestResult("api", Logging.InfoLevel) {
      handleRejections(myRejectionHandler) {
        (new auth.Registration).routes ~
          (new auth.Authorization).routes ~
          (new auth.Logout).routes ~
          (new index.Menu).routes ~
          (new menu.Menu).routes ~
          (new menu.MenuUpdate).routes ~
          (new order.Order).routes ~
          (new order.Orders).routes ~
          (new order.OrderUpdate).routes
      }
    }

}
