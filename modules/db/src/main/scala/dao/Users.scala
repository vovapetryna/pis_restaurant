package dao

import scala.concurrent.Future

trait Users {

  def create(user: models.User): Future[Int]
  def getById(id: Long): Future[Option[models.User]]
  def getAll: Future[List[models.User]]
  def deleteById(id: Long): Future[Int]

  def init: Future[Unit]
}
