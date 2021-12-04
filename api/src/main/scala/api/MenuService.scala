package api

import scala.concurrent.Future

trait MenuService {
  def getAll: Future[Seq[models.MenuRecord]]

  def create(model: models.MenuRecord.Create): Future[Int]

  def update(model: models.MenuRecord.Update): Future[Boolean]
}
