package services

import javax.inject.{Inject, Singleton}

import db.slick.BaseRepositoryService
import db.slick.SlickServiceResults.{SlickResult, SlickMaybeError}
import models.UsersTable.UserID
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Maxim Ochenashko
  */
@Singleton
class ProfileService @Inject()(val dbConfigProvider: DatabaseConfigProvider)
                              (implicit ec: ExecutionContext) extends BaseRepositoryService {

  import ProfileService._
  import driver.api._

  def update(userId: UserID, firstName: String, lastName: String): Future[SlickMaybeError] =
    executeUpdate(ProfileUpdateQuery(userId).update((firstName, lastName)))

  def byUuid(uuid: UserID): Future[SlickResult[ProfileInfo]] =
    executeSingleResult(ProfileByIdQuery(uuid).extract)

}

object ProfileService {

  import models.Tables._
  import models.Tables.profile.api._

  private val ProfileByIdQuery = Compiled { (uuid: Rep[UserID]) =>
    for {u <- Users if u.uuid === uuid} yield (u.firstName, u.lastName, u.email)<>(ProfileInfo.tupled, ProfileInfo.unapply)
  }

  private val ProfileUpdateQuery = Compiled { (uuid: Rep[UserID]) =>
    for {u <- Users if u.uuid === uuid} yield (u.firstName, u.lastName)
  }

  case class ProfileInfo(firstName: String, lastName: String, email: String)
}

