import upickle.default._

package object shared {

  def now(): java.time.ZonedDateTime = java.time.ZonedDateTime.now(java.time.ZoneId.of("UTC"))

  abstract class FormRW[T] {
    def read(values: Map[String, List[String]]): T
  }

  case class Session(id: Long, role: models.Role)

  object Session { implicit val rw: ReadWriter[Session] = macroRW }

  case class Auth(login: String, password: String)

  object Auth {
    implicit val formRW: FormRW[Auth] = (values: Map[String, List[String]]) => Auth(values("login").head, values("password").head)
  }

}
