package models

case class User(id: Long, login: String, passwordHash: String, salt: String, role: Role) {
  def isClient: Boolean = role == Role.Client
  def isAdmin: Boolean  = role == Role.Admin
}
