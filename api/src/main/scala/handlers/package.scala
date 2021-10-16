import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

package object handlers {

  implicit class RequestOps(value: HttpServletRequest) {
    def toMap: Map[String, List[String]] =
      value.getParameterMap.asScala.toMap.view.mapValues(_.toList).toMap

    def to[T](implicit formRW: shared.FormRW[T]): T =
      formRW.read(value.toMap)
  }

  def asyncHandle(body: => Future[Unit])(implicit req: HttpServletRequest, resp: HttpServletResponse, ec: ExecutionContext): Unit = {
    val ctx = req.startAsync()
    body
      .recover { case ex => resp.getWriter.println(pages.error.build(ex.getMessage, ex.toString)) }
      .onComplete(_ => ctx.complete())
  }
}
