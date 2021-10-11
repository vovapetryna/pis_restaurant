package postgres

import com.github.tminglei.slickpg.{ExPostgresProfile, PgDate2Support, PgEnumSupport}
import slick.ast.BaseTypedType
import slick.basic.Capability
import slick.jdbc._

import scala.reflect.ClassTag

trait Profile extends ExPostgresProfile with PgDate2Support with PgEnumSupport {
  override protected def computeCapabilities: Set[Capability] = super.computeCapabilities + JdbcCapabilities.insertOrUpdate

  override val api: MyAPI = new MyAPI {}

  trait MyAPI extends API with DateTimeImplicits {

    def enumTypeMap[T <: models.StringEnumEntry](enum: models.Enum[T])(implicit ct: ClassTag[T]): JdbcType[T] with BaseTypedType[T] =
      MappedColumnType.base[T, String](_.value, s => enum.find(s).getOrElse(throw new Exception("Incorrect Enum entry value")))

    implicit val roleMap: JdbcType[models.Role] with BaseTypedType[models.Role] = enumTypeMap(models.Role)
    implicit val statusMap: JdbcType[models.Status] with BaseTypedType[models.Status] = enumTypeMap(models.Status)

    implicit val getRoleResults: GetResult[models.Role] =
      GetResult(r => models.Role.find(r.nextString()).getOrElse(throw new Exception("Unsupported Enum value")))
    implicit object SetRole extends SetParameter[models.Role] { def apply(v: models.Role, pp: PositionedParameters): Unit = { pp.setString(v.value) } }
    implicit val getStatusResults: GetResult[models.Status] =
      GetResult(r => models.Status.find(r.nextString()).getOrElse(throw new Exception("Unsupported Enum value")))
    implicit object SetStatus extends SetParameter[models.Status] { def apply(v: models.Status, pp: PositionedParameters): Unit = { pp.setString(v.value) } }
    implicit val getUsersResults: GetResult[models.User] = GetResult(r => models.User(r.<<, r.<<, r.<<, r.<<, r.<<))
    implicit val getOrderResults: GetResult[models.Order] = GetResult(r => models.Order(r.<<, r.<<, r.<<))
    implicit val getOrderRecordResults: GetResult[models.OrderRecord] = GetResult(r => models.OrderRecord(r.<<, r.<<, r.<<))
    implicit val getMenuResults: GetResult[models.MenuRecord] = GetResult(r => models.MenuRecord(r.<<, r.<<, r.<<))
  }
}

object Profile extends Profile
