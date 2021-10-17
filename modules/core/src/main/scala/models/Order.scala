package models

import scala.util.Try

case class Order(id: Long, userId: Long, status: Status)

object Order {

  def forUser(userId: Long): Order = Order(-1L, userId, Status.Pending)

  case class Create(records: Map[Long, Long])

  object Create {
    implicit val formRW: shared.FormRW[Create] = (values: Map[String, List[String]]) => {
      val record_ids = values("record_id").map(_.toLong)
      val counts     = values("count").map(r => Try(r.toLong).toOption.getOrElse(0L))
      Create(record_ids.zip(counts).toMap)
    }
  }

  case class Update(id: Long, status: Status)

  object Update {
    implicit val formRW: shared.FormRW[Update] = (values: Map[String, List[String]]) =>
      Update(values("id").head.toLong, Status.find(values("status").head).get)
  }

  case class Represent(id: Long, status: Status, user: models.User, records: List[models.OrderRecord.Represent])

}
