import jakarta.servlet.http.{Cookie, HttpServletRequest, HttpServletResponse}
import pdi.jwt.{JwtAlgorithm, JwtUpickle}
import upickle.default._

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

package object handlers {

  implicit class RequestOps(value: HttpServletRequest) {
    def toMap: Map[String, List[String]] =
      value.getParameterMap.asScala.toMap.view.mapValues(_.toList).toMap

    def to[T](implicit formRW: shared.FormRW[T]): T = {
      value.setCharacterEncoding("utf-8")
      formRW.read(value.toMap)
    }
  }

  def base(body: => Unit)(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    req.setCharacterEncoding("utf-8")
    resp.setCharacterEncoding("utf-8")
    Try {
      body
    } match {
      case Success(_) => ()
      case Failure(ex) =>
        resp.getWriter.println(pages.error.build(ex.getMessage))
    }
  }

  def asyncHandle(body: => Future[Unit])(implicit req: HttpServletRequest, resp: HttpServletResponse, ec: ExecutionContext): Unit = {
    val ctx = req.startAsync()
    resp.setCharacterEncoding("utf-8")
    Try {
      body
        .recover {
          case ex =>
            println(ex.toString, ex.getStackTrace.mkString("\n"))
            resp.getWriter.println(pages.error.build(ex.getMessage))
        }
        .onComplete(_ => ctx.complete())
    } match {
      case Success(_) => ()
      case Failure(ex) =>
        resp.getWriter.println(pages.error.build(ex.getMessage))
        ctx.complete()
    }
  }

  object cookies {
    def set(name: String, value: String, duration: FiniteDuration): Cookie = {
      val cookie = new Cookie(name, value)
      cookie.setMaxAge(duration.toSeconds.toInt)
      cookie
    }

    def expire(name: String): Cookie = set(name, "", 0.seconds)

    def withCookie(
        name: String
    )(body: Cookie => Unit)(error: Throwable => Unit)(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      Try(req.getCookies.find(_.getName == name)).toOption.flatten match {
        case Some(cookie) => body(cookie)
        case _            => error(new Exception(s"Failed to get cookie with name $name"))
      }
  }

  object sessions {
    val algo: JwtAlgorithm.HS256.type   = JwtAlgorithm.HS256
    val key: String                     = "very_secret_key"
    val cookieKey: String               = "session"
    val sessionDuration: FiniteDuration = 24.hours

    def encode[T](model: T)(implicit rw: ReadWriter[T]): String =
      JwtUpickle.encode(writeJs(model), key, algo)

    def decode[T](token: String)(implicit rw: ReadWriter[T]): T =
      JwtUpickle.decodeJson(token, key, Seq(algo)).map(read[T](_)) match {
        case Success(value)     => value
        case Failure(exception) => throw exception
      }

    def setSession[T](model: T, resp: HttpServletResponse)(implicit rw: ReadWriter[T]): Unit =
      resp.addCookie(cookies.set(cookieKey, encode(model), sessionDuration))

    def withSessionOrError[T: ReadWriter](
        body: T => Unit
    )(error: Throwable => Unit)(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      cookies.withCookie(cookieKey) { cookie =>
        Try(decode[T](cookie.getValue)) match {
          case Success(value)     => body(value)
          case Failure(exception) => error(exception)
        }
      }(error)

    def withSession[T: ReadWriter](body: T => Unit)(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      withSessionOrError(body) { ex =>
        println(ex.getMessage)
        resp.sendRedirect(paths.authorization)
      }

    def withPredicateSession(
        predicate: shared.Session => Boolean
    )(body: shared.Session => Unit)(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      withSession[shared.Session] { session =>
        if (predicate(session)) body(session) else resp.sendRedirect(paths.index)
      }

    def withClient(body: shared.Session => Unit)(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      withPredicateSession(_.role == models.Role.Client)(body)

    def withAdmin(body: shared.Session => Unit)(implicit req: HttpServletRequest, resp: HttpServletResponse): Unit =
      withPredicateSession(_.role == models.Role.Admin)(body)
  }

  object paths {
    val index = "/"

    val registration  = "/registration"
    val authorization = "/authorization"
    val logout        = "/logout"

    val order       = "/order"
    val orders      = "/orders"
    val orderUpdate = "/order_update"

    val menu       = "/menu"
    val menuUpdate = "/menu_update"
  }
}
