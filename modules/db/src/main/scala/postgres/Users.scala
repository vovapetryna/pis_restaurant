package postgres

import postgres.Profile.api._

import scala.concurrent.{ExecutionContext, Future}

class Users(query: TableQuery[Users.Mapping])(implicit db: Database, ec: ExecutionContext) extends dao.Users {
  def create(user: models.User): Future[Int] =
    db.run(sqlu"insert into users (login, password_hash, salt, role) values (${user.login}, ${user.passwordHash}, ${user.salt}, ${user.role})")
  def getById(id: Long): Future[Option[models.User]] = db.run(sql"select * from users where id = $id".as[models.User].headOption)
  def getAll: Future[List[models.User]]              = db.run(sql"select * from users".as[models.User].map(_.toList))
  def deleteById(id: Long): Future[Int]              = db.run(sqlu"delete from users where id = $id")

  def init: Future[Unit] = db.run(query.schema.create)
}

object Users {

  class Mapping(tag: Tag) extends Table[models.User](tag, "users") {
    val id           = column[Long]("id", O.Unique, O.AutoInc)
    val login        = column[String]("login")
    val passwordHash = column[String]("password_hash")
    val salt         = column[String]("salt")
    val role         = column[models.Role]("role")

    val * = (id, login, passwordHash, salt, role) <> (models.User.apply _ tupled, models.User.unapply)

    val pk = primaryKey("users_pk", id)
  }

  object Mapping {
    val query = TableQuery[Mapping]
  }

}
