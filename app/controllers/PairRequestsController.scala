package controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import common.enums.PairRequestStatus
import common.enums.UserRole.{Doctor, Patient}
import controllers.PairRequestsController._
import models.UsersTable.UserID
import play.api.i18n.MessagesApi
import play.api.libs.json.{Json, Reads, Writes}
import rest.controllers.RestController
import services.PairRequestService
import services.PairRequestService.PairListItem
import services.security.AuthService

import scala.concurrent.ExecutionContext

/**
  * @author Maxim Ochenashko
  */
@Singleton
class PairRequestsController @Inject()(val authService: AuthService,
                                       pairRequestService: PairRequestService)
                                      (implicit val messagesApi: MessagesApi, ec: ExecutionContext) extends RestController {

  import scalaz._
  import EitherT._
  import Scalaz._

  def create = authorized(Seq(Patient)).parseAsync[PairRequest] {
    case (request, PairRequest(doctorId, message)) =>
      val patientId = request.authInfo.uuid
      val docId = UserID(doctorId)
      val result = for {
        _ <- eitherT(pairRequestService.pairExists(patientId, docId))
        _ <- eitherT(pairRequestService.createPair(patientId, docId, message))
      } yield created()

      result.leftMap(e => badRequest(e.message)).merge
  }

  def list = authorized().async { implicit request =>
    (request.authInfo.role match {
      case Patient =>
        pairRequestService.listByPatient[PairListItem] _
      case Doctor =>
        pairRequestService.listByDoctor[PairListItem] _
    }) (request.authInfo.uuid, queryParams).map(ok(_))
  }

  def answerToRequest = authorized(Seq(Doctor)).parseAsync[RequestAnswer] {
    case (request, RequestAnswer(uuid, status, rejectReason)) =>
      eitherT(pairRequestService.answerToRequest(uuid, request.authInfo.uuid, status, rejectReason))
        .leftMap(e => badRequest(e.message))
        .rightAs(ok())
        .merge
  }

}

object PairRequestsController {

  case class PairRequest(doctorId: UUID, message: Option[String])

  case class RequestAnswer(uuid: UUID, status: PairRequestStatus, rejectReason: Option[String])

  import models.JsonFormats._
  implicit val pairRequestReads: Reads[PairRequest] = Json.reads[PairRequest]
  implicit val pairListItemWrites: Writes[PairListItem] = Json.writes[PairListItem]
  implicit val pairRequestStatusReads: Reads[PairRequestStatus] = PairRequestStatus.apiJsonReads
  implicit val acceptRequestReads: Reads[RequestAnswer] = Json.reads[RequestAnswer]
}
