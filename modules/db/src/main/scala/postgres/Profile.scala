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

    implicit val roleMap: JdbcType[models.Role] with BaseTypedType[models.Role]       = enumTypeMap(models.Role)
    implicit val statusMap: JdbcType[models.Status] with BaseTypedType[models.Status] = enumTypeMap(models.Status)
  }
}

object Profile extends Profile
