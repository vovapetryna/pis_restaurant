package models

case class Order(id: Long, userId: Long, status: Status)

object Order {

  case class WithRecords(order: Order, records: List[OrderRecord])

}
