import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.model.{ContentTypes, DateTime, HttpEntity, StatusCodes}
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.LazyLogging
import pdi.jwt.{JwtAlgorithm, JwtUpickle}
import upickle.default._

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

package object handlers {
  case class BadServiceRejection(error: String) extends Rejection

  implicit class htmlXmlOps(value: scala.xml.Elem) extends Directives {
    def complete: Route = complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, value.toString))
  }

  implicit class redirectOps(value: String) extends Directives {
    def redirect: Route = redirect(s"http://localhost:9001/$value", StatusCodes.MovedPermanently)
  }

  implicit class FutureRouteOpsT[T](value: Future[Route])(implicit ec: ExecutionContext) extends Directives with LazyLogging {
    def orRejection(msg: String): Route =
      onComplete(value) {
        case Success(value) => value
        case Failure(ex) =>
          logger.error("Service error", ex)
          reject(BadServiceRejection(msg))
      }
  }

  implicit class FormMapOps(value: Map[String, List[String]]) { def toModel[T](implicit formRW: shared.FormRW[T]): T = formRW.read(value) }

  object cookies extends Directives with LazyLogging {
    case object EmptyCookieRejection extends Rejection

    def set(name: String, value: String, duration: FiniteDuration): Directive0 = {
      val toDateTime = DateTime.now.plus(duration.toMillis)
      val cookie     = HttpCookie(name, value, expires = Option(toDateTime))
      setCookie(cookie)
    }

    def expire(name: String): Directive0 = set(name, "", 0.seconds)

    def withCookie(
        name: String
    ): Directive1[String] =
      optionalCookie(name).flatMap {
        case Some(cookie) => provide(cookie.value)
        case None =>
          logger.info(s"Got empty cookies $name")
          reject(EmptyCookieRejection)
      }
  }

  object sessions extends Directives with LazyLogging {
    case object SessionDecodingRejection  extends Rejection
    case object PermissionDeniedRejection extends Rejection

    val algo: JwtAlgorithm.HS256.type   = JwtAlgorithm.HS256
    val key: String                     = "very_secret_key"
    val cookieKey: String               = "session"
    val sessionDuration: FiniteDuration = 24.hours

    def encode[T](model: T)(implicit rw: ReadWriter[T]): String = JwtUpickle.encode(writeJs(model), key, algo)

    def decode[T](token: String)(implicit rw: ReadWriter[T]): T =
      JwtUpickle.decodeJson(token, key, Seq(algo)).map(read[T](_)) match {
        case Success(value)     => value
        case Failure(exception) => throw exception
      }

    def setSession(model: shared.Session): Directive0 = cookies.set(cookieKey, encode(model), sessionDuration)

    def withSession: Directive1[shared.Session] =
      cookies.withCookie(cookieKey).flatMap { cookie =>
        Try(decode[shared.Session](cookie)) match {
          case Success(value) => provide(value)
          case Failure(ex) =>
            logger.error(s"Failed to decode session", ex)
            reject(SessionDecodingRejection)
        }
      }

    def withPredicateSession(predicate: shared.Session => Boolean): Directive1[shared.Session] =
      withSession.flatMap { session =>
        if (predicate(session)) provide(session) else reject(PermissionDeniedRejection)
      }

    def withClient: Directive1[shared.Session] = withPredicateSession(_.role == models.Role.Client)
    def withAdmin: Directive1[shared.Session]  = withPredicateSession(_.role == models.Role.Admin)
  }

  object paths extends Directives {
    val index = ""

    val registration  = "registration"
    val authorization = "authorization"
    val logout        = "logout"

    val order       = "order"
    val orders      = "orders"
    val orderUpdate = "order_update"

    val menu       = "menu"
    val menuUpdate = "menu_update"
  }
}
