package postgres

import postgres.Profile.api._

import scala.concurrent.{ExecutionContext, Future}

class Orders(query: TableQuery[Orders.Mapping])(implicit db: Database, ec: ExecutionContext) extends dao.Orders {
  def create(order: models.Order): Future[Int]                = db.run(sqlu"insert into orders (user_id, status) values (${order.userId}, ${order.status})")
  def getById(id: Long): Future[Option[models.Order]]         = db.run(sql"select * from orders where id = $id".as[models.Order].headOption)
  def getAll: Future[List[models.Order]]                      = db.run(sql"select * from orders".as[models.Order].map(_.toList))
  def deleteById(id: Long): Future[Int]                       = db.run(sqlu"delete from orders where id = $id")
  def setStatus(id: Long, status: models.Status): Future[Int] = db.run(sqlu"update orders set status = $status")

  def init: Future[Unit] = db.run(query.schema.create)
}

object Orders {

  class Mapping(tag: Tag) extends Table[models.Order](tag, "orders") {
    val id     = column[Long]("id", O.Unique, O.AutoInc)
    val userId = column[Long]("user_id")
    val status = column[models.Status]("status")

    val * = (id, userId, status) <> (models.Order.apply _ tupled, models.Order.unapply)

    val pk = primaryKey("orders_pk", id)
  }

  object Mapping {
    val query = TableQuery[Mapping]
  }

}
