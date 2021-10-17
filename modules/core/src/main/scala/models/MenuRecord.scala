package models

case class MenuRecord(id: Long, name: String, price: Double)

object MenuRecord {
  def fromCreate(create: Create): MenuRecord = MenuRecord(-1L, create.name, create.price)

  def fromUpdate(update: Update): MenuRecord = MenuRecord(update.id, update.name, update.price)

  case class Create(name: String, price: Double)

  object Create {
    implicit val formRW: shared.FormRW[Create] = (values: Map[String, List[String]]) =>
      Create(values("name").head, values("price").head.toDouble)
  }

  case class Update(id: Long, name: String, price: Double, delete: Boolean)

  object Update {
    implicit val formRW: shared.FormRW[Update] = (values: Map[String, List[String]]) =>
      Update(
        values("id").head.toLong,
        values("name").head,
        values("price").head.toDouble,
        values.get("delete").flatMap(_.headOption).contains("on")
    )
  }
}
