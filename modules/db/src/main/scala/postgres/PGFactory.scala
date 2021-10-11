package postgres

import com.typesafe.config.Config
import postgres.Profile.api.Database

import scala.concurrent.{ExecutionContext, Future}

class PGFactory(val conf: Config)(implicit val ec: ExecutionContext) extends db.DbFactory {

  implicit val db: Database = Database.forConfig("postgresql", conf)

  def users: dao.Users               = new Users(Users.Mapping.query)
  def orders: dao.Orders             = new Orders(Orders.Mapping.query)
  def orderRecords: dao.OrderRecords = new OrderRecords(OrderRecords.Mapping.query)
  def menuRecords: dao.MenuRecords   = new MenuRecords(MenuRecords.Mapping.query)

  def init: Future[Unit] = for {
    _ <- users.init
    _ <- orders.init
    _ <- orderRecords.init
    _ <- menuRecords.init
  } yield ()

}
