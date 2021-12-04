package handlers

import akka.http.scaladsl.server.{Directives, Route}

object Routable {
  trait Service extends Directives {
    def route: String

    def doGet(): Route                                   = complete("undefined")
    def doPost(fields: Map[String, List[String]]): Route = complete("undefined")

    def routes: Route =
      path(route) {
        get { doGet() } ~
          post { formFieldMultiMap { doPost } }
      }
  }
}
