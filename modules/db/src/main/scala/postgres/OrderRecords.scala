package postgres

import postgres.Profile.api._

class OrderRecords(tag: Tag) extends Table[models.OrderRecord](tag, "order_records") {
  val id           = column[Long]("id", O.Unique, O.AutoInc)
  val orderId      = column[Long]("order_id")
  val menuRecordId = column[Long]("menu_record_id")
  val count        = column[Long]("count")

  val * = (id, orderId, menuRecordId, count) <> (models.OrderRecord.apply _ tupled, models.OrderRecord.unapply)

  val orderFk = foreignKey("order_records_orders_id", orderId, postgres.Orders.query)(_.id, onDelete = ForeignKeyAction.Cascade)
  val menuFk =
    foreignKey("order_records_menu_record_id", menuRecordId, postgres.MenuRecords.query)(_.id, onDelete = ForeignKeyAction.Cascade)

  val pk = primaryKey("order_records_pk", id)
}

object OrderRecords {
  val query = TableQuery[OrderRecords]

  def create(record: models.OrderRecord): DBIO[Int] = query += record

  def createAll(records: List[models.OrderRecord]): DBIO[Option[Int]] = query ++= records

  def getById(id: Long): DBIO[Option[models.OrderRecord]] = query.filter(_.id === id).result.headOption

  def getByIds(ids: Set[Long]): DBIO[Seq[models.OrderRecord]] = query.filter(_.orderId inSet ids).result

  def getAll: DBIO[Seq[models.OrderRecord]] = query.result

  def deleteById(id: Long): DBIO[Int] = query.filter(_.id === id).delete

  def init: DBIO[Unit] = query.schema.create
}
