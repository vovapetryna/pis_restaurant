package postgres

import postgres.Profile.api._

import scala.concurrent.ExecutionContext

class MenuRecords(tag: Tag) extends Table[models.MenuRecord](tag, "menu_records") {
  val id    = column[Long]("id", O.Unique, O.AutoInc)
  val name  = column[String]("name", O.Unique)
  val price = column[Double]("price")

  val * = (id, name, price) <> (models.MenuRecord.apply _ tupled, models.MenuRecord.unapply)

  val pk = primaryKey("menu_records_pk", id)

}

object MenuRecords {
  val query = TableQuery[MenuRecords]

  def create(record: models.MenuRecord): DBIO[Int] = query += record

  def getById(id: Long): DBIO[Option[models.MenuRecord]] = query.filter(_.id === id).result.headOption

  def getAll(implicit ec: ExecutionContext): DBIO[Seq[models.MenuRecord]] = query.result

  def getByIds(ids: Set[Long]): DBIO[Seq[models.MenuRecord]] = query.filter(_.id inSet ids).result

  def update(record: models.MenuRecord): DBIO[Int] =
    query.filter(_.id === record.id).map(r => r.name -> r.price).update(record.name -> record.price)

  def deleteById(id: Long): DBIO[Int] = query.filter(_.id === id).delete

  def init: DBIO[Unit] = query.schema.create
}
