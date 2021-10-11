package dao

import scala.concurrent.Future

trait OrderRecords {

  def create(record: models.OrderRecord): Future[Int]
  def getById(id: Long): Future[Option[models.OrderRecord]]
  def getAll: Future[List[models.OrderRecord]]
  def deleteById(id: Long): Future[Int]

  def init: Future[Unit]

}
