package object shared {

  abstract class FormRW[T] {
    def read(values: Map[String, List[String]]): T
  }

  case class Auth(login: String, password: String)

  object Auth {
    implicit val formRW: FormRW[Auth] = (values: Map[String, List[String]]) => Auth(values("login").head, values("password").head)
  }


}
