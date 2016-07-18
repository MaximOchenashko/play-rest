package services

import javax.inject.{Inject, Singleton}

import common.enums.UserRole
import db.slick.BaseRepositoryService
import db.slick.BaseRepositoryService.ListQueryResult
import db.slick.extensions.SlickQueryExtension.ListQueryParams
import models.NameIdModel
import models.UsersTable.UserID
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Maxim Ochenashko
  */
@Singleton
class UserService @Inject()(val dbConfigProvider: DatabaseConfigProvider)
                           (implicit ec: ExecutionContext) extends BaseRepositoryService {

  import UserService._

  def listPatients(doctorId: UserID, params: ListQueryParams): Future[ListQueryResult[NameIdModel]] =
    executeList(PatientsQuery(doctorId).extract, params, models.QueryProjections.user2nameIdModel)

  def listDoctorsByPatient(patientId: UserID, params: ListQueryParams): Future[ListQueryResult[NameIdModel]] =
    executeList(DoctorsQuery(patientId).extract, params, models.QueryProjections.user2nameIdModel)

  def listDoctors(params: ListQueryParams): Future[ListQueryResult[NameIdModel]] =
    executeList(AllDoctorsQuery.extract, params, models.QueryProjections.user2nameIdModel)

}

object UserService {

  import models.Tables._
  import models.Tables.profile.api._

  private val PatientsQuery = Compiled { (doctorId: Rep[UserID]) =>
    for {
      user <- Users
      pair <- MedicalPairs
      if pair.patientId === user.uuid
      if pair.doctorId === doctorId
    } yield user
  }

  private val DoctorsQuery = Compiled { (patientId: Rep[UserID]) =>
    for {
      user <- Users
      pair <- MedicalPairs
      if pair.doctorId === user.uuid
      if pair.patientId === patientId
    } yield user
  }

  private val AllDoctorsQuery = Compiled {
    for {user <- Users if user.role === UserRole.Doctor.code} yield user
  }

}

