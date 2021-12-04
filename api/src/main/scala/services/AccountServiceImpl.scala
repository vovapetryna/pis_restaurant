package services

import postgres.Profile.api._

import scala.concurrent.{ExecutionContext, Future}

class AccountServiceImpl(implicit ec: ExecutionContext, db: Database) extends api.AccountService {
  override def create(model: shared.Auth): Future[Int] = db.run(postgres.Users.create(models.User.fromAuth(model)))

  override def authorize(model: shared.Auth): Future[models.User] =
    db.run(postgres.Users.getByLogin(model.login))
      .map {
        case Some(account) if utils.passwordHashing.verifyPassword(account.passwordHash, model.password, account.salt) => account
      }
}
