package postgres

import postgres.Profile.api._

import scala.concurrent.ExecutionContext

class Orders(tag: Tag) extends Table[models.Order](tag, "orders") {
  val id     = column[Long]("id", O.Unique, O.AutoInc)
  val userId = column[Long]("user_id")
  val status = column[models.Status]("status")

  val * = (id, userId, status) <> (models.Order.apply _ tupled, models.Order.unapply)

  val orderFk = foreignKey("orders_users_id", userId, postgres.Users.query)(_.id, onDelete = ForeignKeyAction.Cascade)

  val pk = primaryKey("orders_pk", id)
}

object Orders {
  val query                                                       = TableQuery[Orders]
  def clientQuery(userId: Long): Query[Orders, models.Order, Seq] = query.filter(_.userId === userId)

  def create(order: models.Order): DBIO[models.Order] = query returning query += order

  def getById(id: Long): DBIO[Option[models.Order]] = query.filter(_.id === id).result.headOption

  def getAll: DBIO[Seq[models.Order]] = query.result

  def getWithRecords(searchQuery: Query[Orders, models.Order, Seq])(implicit ec: ExecutionContext): DBIO[Seq[models.Order.Represent]] =
    for {
      orders <- searchQuery.result
      users  <- postgres.Users.getByIds(orders.map(_.userId).toSet)
      usersMap = users.map(r => r.id -> r).toMap
      records <- postgres.OrderRecords.getByIds(orders.map(_.id).toSet)
      recordsMap = records.groupBy(_.orderId).withDefaultValue(Nil)
      menuRecords <- postgres.MenuRecords.getByIds(records.map(_.menuRecordId).toSet)
      menuMap = menuRecords.map(r => r.id -> r).toMap
    } yield {
      orders
        .map { order =>
          models.Order.Represent(
            order.id,
            order.status,
            usersMap(order.userId),
            recordsMap(order.id)
              .map(record => models.OrderRecord.Represent(record.id, menuMap(record.menuRecordId).name, record.count))
              .toList
              .sortBy(_.id)
          )
        }
        .sortBy(_.id)
    }

  def update(id: Long, status: models.Status): DBIO[Int] = query.filter(_.id === id).map(_.status).update(status)

  def deleteById(id: Long): DBIO[Int] = query.filter(_.id === id).delete

  def setStatus(id: Long, status: models.Status): DBIO[Int] = query.filter(_.id === id).map(_.status).update(status)

  def init: DBIO[Unit] = query.schema.create
}
