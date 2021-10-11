package postgres

import postgres.Profile.api._

class Orders(tag: Tag) extends Table[models.Order](tag, "orders") {
  val id     = column[Long]("id", O.Unique, O.AutoInc)
  val userId = column[Long]("user_id")
  val status = column[models.Status]("status")

  val * = (id, userId, status) <> (models.Order.apply _ tupled, models.Order.unapply)

  val pk = primaryKey("orders_pk", id)
}

object Orders {
  val query = TableQuery[Orders]

  def create(order: models.Order): DBIO[Int] = query += order

  def getById(id: Long): DBIO[Option[models.Order]] = query.filter(_.id === id).result.headOption

  def getAll: DBIO[Seq[models.Order]] = query.result

  def deleteById(id: Long): DBIO[Int] = query.filter(_.id === id).delete

  def setStatus(id: Long, status: models.Status): DBIO[Int] = query.filter(_.id === id).map(_.status).update(status)

  def init: DBIO[Unit] = query.schema.create
}
