package postgres

import postgres.Profile.api._

class Users(tag: Tag) extends Table[models.User](tag, "users") {
  val id           = column[Long]("id", O.Unique, O.AutoInc)
  val login        = column[String]("login")
  val passwordHash = column[String]("password_hash")
  val salt         = column[String]("salt")
  val role         = column[models.Role]("role")

  val * = (id, login, passwordHash, salt, role) <> (models.User.apply _ tupled, models.User.unapply)

  val pk = primaryKey("users_pk", id)
}

object Users {
  val query = TableQuery[Users]

  def create(user: models.User): DBIO[Int] = query += user

  def getById(id: Long): DBIO[Option[models.User]] = query.filter(_.id === id).result.headOption

  def getAll: DBIO[Seq[models.User]] = query.result

  def deleteById(id: Long): DBIO[Int] = query.filter(_.id === id).delete

  def init: DBIO[Unit] = query.schema.create

}
