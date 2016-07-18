package controllers

import javax.inject.{Inject, Singleton}

import common.enums.UserRole.{Doctor, Patient}
import db.slick.BaseRepositoryService.ListQueryResult
import models.NameIdModel
import play.api.i18n.MessagesApi
import play.api.mvc.Result
import rest.controllers.RestController
import services.UserService
import services.security.AuthService

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Maxim Ochenashko
  */
@Singleton
class UsersController @Inject()(val authService: AuthService,
                                userService: UserService)
                               (implicit val messagesApi: MessagesApi, ec: ExecutionContext) extends RestController {

  import models.JsonFormats._

  def patients = authorized(Seq(Doctor)).async { implicit request =>
    processListRequest(userService.listPatients(request.authInfo.uuid, queryParams))
  }

  def myDoctors = authorized(Seq(Patient)).async { implicit request =>
    processListRequest(userService.listDoctorsByPatient(request.authInfo.uuid, queryParams))
  }

  def doctors = authorized().async { implicit request =>
    processListRequest(userService.listDoctors(queryParams))
  }

  private[this] def processListRequest(future: => Future[ListQueryResult[NameIdModel]]): Future[Result] =
    for {listResult <- future} yield ok(listResult)
}

