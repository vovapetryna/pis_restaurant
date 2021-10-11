package postgres

import postgres.Profile.api._

import scala.concurrent.{ExecutionContext, Future}

class MenuRecords(query: TableQuery[MenuRecords.Mapping])(implicit db: Database, ec: ExecutionContext) extends dao.MenuRecords {
  def create(record: models.MenuRecord): Future[Int]       = db.run(sqlu"insert into menu_records (name, price) values (${record.name}, ${record.price})")
  def getById(id: Long): Future[Option[models.MenuRecord]] = db.run(sql"select * from menu_records where id = $id".as[models.MenuRecord].headOption)
  def getAll: Future[List[models.MenuRecord]]              = db.run(sql"select * from menu_records".as[models.MenuRecord].map(_.toList))
  def deleteById(id: Long): Future[Int]                    = db.run(sqlu"delete from menu_records where id = $id")

  def init: Future[Unit] = db.run(query.schema.create)
}

object MenuRecords {

  class Mapping(tag: Tag) extends Table[models.MenuRecord](tag, "menu_records") {
    val id    = column[Long]("id", O.Unique, O.AutoInc)
    val name  = column[String]("name")
    val price = column[Double]("price")

    val * = (id, name, price) <> (models.MenuRecord.apply _ tupled, models.MenuRecord.unapply)

    val pk = primaryKey("menu_records_pk", id)
  }

  object Mapping {
    val query = TableQuery[Mapping]
  }

}
