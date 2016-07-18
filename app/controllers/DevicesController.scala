package controllers

import javax.inject.{Inject, Singleton}

import common.enums.DeviceType
import common.enums.UserRole.Patient
import controllers.DevicesController.NewDevice
import play.api.i18n.MessagesApi
import play.api.libs.json.{Format, Json}
import play.api.mvc.Action
import rest.controllers.RestController
import services.DeviceService.DeviceListItem
import services.security.AuthService
import services.{DeviceService, MeasurementsResultsHandler}

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Maxim Ochenashko
  */
@Singleton
class DevicesController @Inject()(val authService: AuthService,
                                  measurementsResultsHandler: MeasurementsResultsHandler,
                                  deviceService: DeviceService)
                                 (implicit val messagesApi: MessagesApi, ec: ExecutionContext) extends RestController {

  import DevicesController._
  import scalaz._
  import EitherT._
  import Scalaz._

  def list = authorized(Seq(Patient)).async { implicit request =>
    for {
      listResult <- deviceService.list(request.authInfo.uuid, queryParams)
    } yield ok(listResult)
  }

  def create = authorized(Seq(Patient)).parseAsync[NewDevice] {
    case (request, NewDevice(name, deviceType)) =>
      eitherT(deviceService.create(request.authInfo.uuid, name, deviceType))
        .leftMap(e => badRequest(e.message))
        .rightAs(created())
        .merge
  }

  def genData = Action.async {
    measurementsResultsHandler.genRandomData()
    Future.successful(Ok)
  }

  def recount = Action.async {
    measurementsResultsHandler.makeWeeklyMeasurements()
    Future.successful(Ok)
  }

}

object DevicesController {

  case class NewDevice(name: String, deviceType: DeviceType)

  implicit val deviceTypeFormat: Format[DeviceType] = Format(DeviceType.apiJsonReads, DeviceType.jsonWrites)

  implicit val newDeviceFormat: Format[NewDevice] = Json.format[NewDevice]

  implicit val deviceListItem: Format[DeviceListItem] = Json.format[DeviceListItem]

}

