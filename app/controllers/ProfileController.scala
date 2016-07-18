package controllers

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}

import db.slick.errors.{NotFound => SlickNotFound}
import common.enums.UserRole
import play.api.i18n.MessagesApi
import play.api.libs.json.{Writes, Format, JsResult, JsSuccess, JsValue, Json, Reads}
import rest.controllers.RestController
import services.MedicalCardService.HealthInfo
import services.ProfileService.ProfileInfo
import services.security.AuthService
import services.{MedicalCardService, ProfileService}

import scala.concurrent.ExecutionContext

/**
  * @author Maxim Ochenashko
  */
@Singleton
class ProfileController @Inject()(val authService: AuthService,
                                  profileService: ProfileService,
                                  medicalCardService: MedicalCardService)
                                 (implicit val messagesApi: MessagesApi, ec: ExecutionContext) extends RestController {

  import ProfileController._
  import scalaz._
  import Scalaz._
  import EitherT._

  def profile = authorized().async { request =>
    eitherT(profileService.byUuid(request.authInfo.uuid))
      .leftMap {
        case SlickNotFound => notFound()
        case x => badRequest(x.message)
      }
      .map { profile => ok(profile.some) }
      .merge
  }

  def editProfile = authorized().parseAsync[EditProfile] { case (request, EditProfile(fName, lName)) =>
    eitherT(profileService.update(request.authInfo.uuid, fName, lName))
      .leftMap(e => badRequest(e.message))
      .rightAs(ok())
      .merge
  }

  def medicalCard = authorized(Seq(UserRole.Patient)).async { request =>
    eitherT(medicalCardService.byUserId(request.authInfo.uuid))
      .leftMap {
        case SlickNotFound => notFound()
        case x => badRequest(x.message)
      }
      .map(v => ok(v.some))
      .merge
  }

  def editMedicalCard = authorized(Seq(UserRole.Patient)).parseAsync[HealthInfo] {
    case (request, HealthInfo(birthDate, weight, height)) =>
      eitherT(medicalCardService.update(request.authInfo.uuid, birthDate, weight, height))
        .leftMap(e => badRequest(e.message))
        .rightAs(ok())
        .merge
  }
}

object ProfileController {

  case class EditProfile(firstName: String, lastName: String)

  private[this] val formatter = DateTimeFormatter.ofPattern("mm/dd/yyyy")

  import models.JsonFormats._
  implicit val profileInfoFormat: Format[ProfileInfo] = Json.format[ProfileInfo]
  implicit val editProfileFormat: Format[EditProfile] = Json.format[EditProfile]
  implicit val healthInfoResponseFormat: Writes[HealthInfo] = Json.writes[HealthInfo]

  implicit val healthInfo: Reads[HealthInfo] = new Reads[HealthInfo] {
    override def reads(json: JsValue): JsResult[HealthInfo] = {
      val birth = (json \ "birthDate").as[String]
      val weight = (json \ "weight").as[Double]
      val height = (json \ "height").as[Double]
      JsSuccess(HealthInfo(LocalDate.parse(birth, formatter), weight, height))
    }
  }
}