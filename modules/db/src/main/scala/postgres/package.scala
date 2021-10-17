import postgres.Profile.api._

import scala.concurrent.ExecutionContext

package object postgres {

  def init(implicit ec: ExecutionContext): DBIO[Unit] =
    for {
      _ <- Users.init
      _ <- Orders.init
      _ <- MenuRecords.init
      _ <- OrderRecords.init
      _ <- Users.create(models.User.fromAuth(shared.Auth("admin", "admin"), models.Role.Admin))
    } yield ()

}
