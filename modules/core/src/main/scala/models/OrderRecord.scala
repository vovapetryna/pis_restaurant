package models

case class OrderRecord(id: Long, orderId: Long, menuRecordId: Long, count: Long)

object OrderRecord {
  def fromCreate(orderId: Long, create: models.Order.Create): List[OrderRecord] =
    create.records
      .map {
        case (menuRecordId, count) =>
          OrderRecord(-1L, orderId, menuRecordId, count)
      }
      .toList
      .filter(_.count > 0)

  case class Represent(id: Long, recordName: String, count: Long)
}
