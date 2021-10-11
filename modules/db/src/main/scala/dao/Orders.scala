package dao

import scala.concurrent.Future

trait Orders {

  def create(order: models.Order): Future[Int]
  def getById(id: Long): Future[Option[models.Order]]
  def getAll: Future[List[models.Order]]
  def deleteById(id: Long): Future[Int]
  def setStatus(id: Long, status: models.Status): Future[Int]

  def init: Future[Unit]

}
