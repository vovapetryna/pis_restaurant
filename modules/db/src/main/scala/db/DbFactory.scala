package db

import com.typesafe.config.Config
import postgres.PGFactory

import scala.concurrent.{ExecutionContext, Future}

trait DbFactory {

  def users: dao.Users
  def orders: dao.Orders
  def orderRecords: dao.OrderRecords
  def menuRecords: dao.MenuRecords

  def init: Future[Unit]

}

object DbFactory {

  def get(implicit conf: Config, ec: ExecutionContext): DbFactory = new PGFactory(conf)

}
