package rest.controllers

import controllers.base.ODataController
import models.Tables.UsersRow
import play.api.i18n.I18nSupport
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Reads
import play.api.mvc.{ActionBuilder, Controller, MultipartFormData, Request, Result}
import play.mvc.Http
import rest.security.SecurityActions
import rest.{ApiResponses, JsonParser}

import scala.concurrent.Future
import scala.language.higherKinds

/**
  * @author Maxim Ochenashko
  */
trait RestController extends Controller with ApiResponses with SecurityActions with ODataController with JsonParser with I18nSupport {

  implicit class ActionExtension[R[_] <: Request[_]](actionBuilder: ActionBuilder[R]) {
    def parseAsync[X](block: (R[X], X) => Future[Result])(implicit reader: Reads[X]) =
      actionBuilder.async(jsonParse[X]) { request => block(request, request.asInstanceOf[Request[X]].body) }
  }

  protected def files(implicit request: Request[MultipartFormData[TemporaryFile]]) = request.body.files

  protected def tokenKey(implicit request: Request[_]) = request.headers.get(Http.HeaderNames.AUTHORIZATION)

  protected def decodedQueryStrings(key: String)(implicit request: Request[_]) =
    request.queryString.get(key).map(_.map(urlDecode))

  implicit class UsersRowEx(row: UsersRow) {
    def fullName = row.firstName + " " + row.lastName
  }

}
