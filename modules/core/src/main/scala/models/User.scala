package models

case class User(id: Long, login: String, passwordHash: String, salt: String, role: Role) {
  def isClient: Boolean = role == Role.Client
  def isAdmin: Boolean  = role == Role.Admin
}

object User {
  def fromAuth(auth: shared.Auth): User = {
    val salt = utils.salt.generate()
    val hash = utils.passwordHashing.hashPassword(auth.password, salt)
    User(-1L, auth.login, hash, salt, Role.Client)
  }
}
