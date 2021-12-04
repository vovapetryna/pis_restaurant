package api

import scala.concurrent.Future

trait OrderService {
  def getAll: Future[Seq[models.OrderRecord]]
  def getRepresentForClient(sessionId: Long): Future[Seq[models.Order.Represent]]
  def getRepresentForAdmin: Future[Seq[models.Order.Represent]]

  def create(sessionId: Long, model: models.Order.Create): Future[Boolean]

  def update(model: models.Order.Update): Future[Boolean]
}
