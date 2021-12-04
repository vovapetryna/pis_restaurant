package api

import scala.concurrent.Future

trait AccountService {
  def create(model: shared.Auth): Future[Int]

  def authorize(model: shared.Auth): Future[models.User]
}
