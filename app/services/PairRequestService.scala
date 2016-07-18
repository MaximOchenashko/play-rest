package services

import java.time.LocalDateTime
import java.util.UUID
import javax.inject.{Inject, Singleton}

import common.enums.PairRequestStatus
import common.enums.PairRequestStatus.{Accepted, New}
import db.slick.BaseRepositoryService
import db.slick.BaseRepositoryService.ListQueryResult
import db.slick.SlickServiceResults.SlickMaybeError
import db.slick.errors.SlickError
import db.slick.extensions.SlickQueryExtension.ListQueryParams
import models.NameIdModel
import models.Tables._
import models.UsersTable.UserID
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Maxim Ochenashko
  */
@Singleton
class PairRequestService @Inject()(val dbConfigProvider: DatabaseConfigProvider)
                                  (implicit ec: ExecutionContext) extends BaseRepositoryService {

  import PairRequestService._
  import profile.api._
  import scalaz._
  import Scalaz._
  import EitherT._

  def pairExists(patientId: UserID, doctorId: UserID): Future[SlickMaybeError] =
    executeExists(PairExistsQuery(patientId, doctorId).extract)

  def createPair(patientId: UserID, doctorId: UserID, message: Option[String]): Future[SlickMaybeError] =
    executeSave(MedicalPairRequests += MedicalPairRequestsRow(newUuid, now, now, patientId, doctorId, message, None, New.code))

  def listByPatient[X](patientId: UserID, params: ListQueryParams): Future[ListQueryResult[PairListItem]] =
    listWithUsersJoin(_.patientId, _.doctorId, patientId, params)

  def listByDoctor[X](doctorId: UserID, params: ListQueryParams): Future[ListQueryResult[PairListItem]] =
    listWithUsersJoin(_.doctorId, _.patientId, doctorId, params)

  def answerToRequest(uuid: UUID, doctorId: UserID, status: PairRequestStatus, rejectReason: Option[String]): Future[SlickMaybeError] =
    (for {
      patientId <- eitherT(executeSingleResult(SelectPatientDoctorQuery(uuid, doctorId).extract))
      updateQuery = PairUpdateQuery(uuid).update((status.code, rejectReason))
      _ <- eitherT[Future, SlickError, Unit](status match {
        case Accepted =>
          val newPair = MedicalPairsRow(newUuid, now, now, patientId, doctorId)
          for {
            _ <- db.run(DBIO.seq(MedicalPairs += newPair, updateQuery).transactionally)
          } yield unitInstance.zero.right
        case _ =>
          executeUpdate(updateQuery)
      })
    } yield ()).run

  private def listWithUsersJoin[X](property: MedicalPairRequests => Rep[UserID],
                                   joinTable: MedicalPairRequests => Rep[UserID],
                                   userId: UserID,
                                   params: ListQueryParams): Future[ListQueryResult[PairListItem]] = {
    val query = for {
      pair <- MedicalPairRequests
      owner <- Users if owner.uuid === property(pair) && owner.uuid === userId
      joinUser <- Users if joinUser.uuid === joinTable(pair)
    } yield (pair, joinUser)

    executeList(query, params, (pairListProjection _).tupled)
  }

  private def pairListProjection(p: MedicalPairRequests, u: Users) =
    (p.uuid,
      p.createDate,
      p.status,
      models.QueryProjections.user2nameIdModel(u),
      p.message,
      p.rejectMessage).<>[PairListItem, (UUID, LocalDateTime, Int, NameIdModel, Option[String], Option[String])](
      { case (uuid, cDate, status, user, msg, rejectMsg) =>
        PairListItem(uuid, cDate, PairRequestStatus.byCode(status).get, user, msg, rejectMsg)
      },
      { _ => throw new IllegalStateException("Update is not supported") }
    )
}

object PairRequestService {

  import models.Tables.profile.api._

  private val PairExistsQuery = Compiled { (patientId: Rep[UserID], doctorId: Rep[UserID]) =>
    for {
      r <- MedicalPairRequests
      if r.patientId === patientId && r.doctorId === doctorId && r.status.inSet(Seq(Accepted.code, New.code))
    } yield r
  }

  private val SelectPatientDoctorQuery = Compiled { (pairId: Rep[UUID], doctorId: Rep[UserID]) =>
    for {
      pair <- MedicalPairRequests
      if pair.uuid === pairId
      if pair.doctorId === doctorId
      if pair.status === PairRequestStatus.New.code
    } yield pair.patientId
  }

  private val PairUpdateQuery = Compiled { (pairId: Rep[UUID]) =>
    for {p <- MedicalPairRequests if p.uuid === pairId} yield (p.status, p.rejectMessage)
  }

  case class PairListItem(uuid: UUID, createDate: LocalDateTime, status: PairRequestStatus,
                          user: NameIdModel, message: Option[String], rejectReason: Option[String])

}

