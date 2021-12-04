package services

import models.MenuRecord
import postgres.Profile.api._

import scala.concurrent.{ExecutionContext, Future}

class MenuServiceImpl(implicit ec: ExecutionContext, db: Database) extends api.MenuService {
  override def getAll: Future[Seq[MenuRecord]] = db.run(postgres.MenuRecords.getAll)

  override def create(model: MenuRecord.Create): Future[Int] = db.run(postgres.MenuRecords.create(models.MenuRecord.fromCreate(model)))

  override def update(model: MenuRecord.Update): Future[Boolean] = {
    def delete(uE: models.MenuRecord.Update): DBIO[Boolean] =
      if (uE.delete) postgres.MenuRecords.deleteById(uE.id).map(_ == 1) else DBIO.successful(true)

    def update(uE: models.MenuRecord.Update): DBIO[Boolean] =
      if (!uE.delete) postgres.MenuRecords.update(models.MenuRecord.fromUpdate(uE)).map(_ == 1) else DBIO.successful(true)

    db.run(for {
      updated <- update(model)
      deleted <- delete(model)
    } yield updated && deleted)
  }
}
