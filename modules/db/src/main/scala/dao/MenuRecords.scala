package dao

import scala.concurrent.Future

trait MenuRecords {

  def create(record: models.MenuRecord): Future[Int]
  def getById(id: Long): Future[Option[models.MenuRecord]]
  def getAll: Future[List[models.MenuRecord]]
  def deleteById(id: Long): Future[Int]

  def init: Future[Unit]

}
