package services

import models.Order
import postgres.Profile.api._

import scala.concurrent.{ExecutionContext, Future}

class OrderServiceImpl(implicit ec: ExecutionContext, db: Database) extends api.OrderService {
  override def getAll: Future[Seq[models.OrderRecord]] = db.run(postgres.OrderRecords.getAll)

  override def create(sessionId: Long, model: models.Order.Create): Future[Boolean] =
    db.run((for {
      order <- postgres.Orders.create(models.Order.forUser(sessionId))
      records = models.OrderRecord.fromCreate(order.id, model)
      Some(recordsCount) <- postgres.OrderRecords.createAll(records) if recordsCount == records.size
    } yield true).transactionally)

  override def getRepresentForClient(sessionId: Long): Future[Seq[Order.Represent]] =
    db.run(postgres.Orders.getWithRecords(postgres.Orders.clientQuery(sessionId)))

  override def getRepresentForAdmin: Future[Seq[Order.Represent]] = db.run(postgres.Orders.getWithRecords(postgres.Orders.query))

  override def update(model: Order.Update): Future[Boolean] = {
    def update(uE: models.Order.Update): DBIO[Boolean] =
      postgres.Orders.update(uE.id, uE.status).map(_ == 1)
    db.run(update(model))
  }
}
