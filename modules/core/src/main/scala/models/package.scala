import upickle.default._

package object models {

  trait Enum[E <: StringEnumEntry] {
    val values: List[E]
    def find(value: String): Option[E] = values.find(_.value.contains(value))
    implicit val rw: ReadWriter[E] =
      readwriter[String].bimap[E](_.value, value => find(value).getOrElse(throw new Exception(s"$value is invalid id for enum")))
  }

  trait StringEnumEntry {
    val value: String
  }

  sealed abstract class Role(val value: String) extends StringEnumEntry
  object Role extends Enum[Role] {
    case object Client extends Role("client")
    case object Admin  extends Role("admin")

    override val values: List[Role] = Client :: Admin :: Nil
  }

  sealed abstract class Status(val value: String) extends StringEnumEntry
  object Status extends Enum[Status] {
    case object Pending    extends Status("pending")
    case object NotPayed   extends Status("not_payed")
    case object InProgress extends Status("in_progress")
    case object Delivering extends Status("delivering")
    case object Done       extends Status("done")

    override val values: List[Status] = Pending :: NotPayed :: InProgress :: Delivering :: Done :: Nil
  }

}
