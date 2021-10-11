import postgres.Profile.api._

import scala.concurrent.ExecutionContext

package object postgres {

  def init(implicit ec: ExecutionContext): DBIO[Unit] =
    for {
      _ <- Users.init
      _ <- Orders.init
      _ <- OrderRecords.init
      _ <- MenuRecords.init
    } yield ()

}
