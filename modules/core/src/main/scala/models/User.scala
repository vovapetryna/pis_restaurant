package models

case class User(id: Long, login: String, passwordHash: String, salt: String, role: Role) {
  def isClient: Boolean = role == Role.Client
  def isAdmin: Boolean  = role == Role.Admin

  def session: shared.Session = shared.Session(id, role)
}

object User {
  def fromAuth(auth: shared.Auth, role: Role = Role.Client): User = {
    val salt = utils.salt.generate()
    val hash = utils.passwordHashing.hashPassword(auth.password, salt)
    User(-1L, auth.login, hash, salt, role)
  }
}
