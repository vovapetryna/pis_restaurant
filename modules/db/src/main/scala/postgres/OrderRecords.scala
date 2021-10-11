package postgres

import postgres.Profile.api._

import scala.concurrent.{ExecutionContext, Future}

class OrderRecords(query: TableQuery[OrderRecords.Mapping])(implicit db: Database, ec: ExecutionContext) extends dao.OrderRecords {
  def create(record: models.OrderRecord): Future[Int] =
    db.run(sqlu"insert into order_records (order_id, menu_record_id) values (${record.orderId}, ${record.menuRecordId})")
  def getById(id: Long): Future[Option[models.OrderRecord]] =
    db.run(sql"select * from order_records where id = $id".as[models.OrderRecord].headOption)
  def getAll: Future[List[models.OrderRecord]] = db.run(sql"select * from order_records".as[models.OrderRecord].map(_.toList))
  def deleteById(id: Long): Future[Int]        = db.run(sqlu"delete from order_records where id = $id")

  def init: Future[Unit] = db.run(query.schema.create)
}

object OrderRecords {

  class Mapping(tag: Tag) extends Table[models.OrderRecord](tag, "order_records") {
    val id           = column[Long]("id", O.Unique, O.AutoInc)
    val orderId      = column[Long]("order_id")
    val menuRecordId = column[Long]("menu_record_id")

    val * = (id, orderId, menuRecordId) <> (models.OrderRecord.apply _ tupled, models.OrderRecord.unapply)

    val pk = primaryKey("order_records_pk", id)
  }

  object Mapping {
    val query = TableQuery[Mapping]
  }

}
