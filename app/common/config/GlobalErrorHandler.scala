package common.config

import play.api.Logger
import play.api.http.HttpErrorHandler
import play.api.libs.json.JsObject
import play.api.mvc.RequestHeader
import rest.ApiResponses

import scala.concurrent.Future

/**
  * @author Maxim Ochenashko
  */
class GlobalErrorHandler extends HttpErrorHandler with ApiResponses {

  def onServerError(request: RequestHeader, e: Throwable) = Future.successful {
    Logger.error(s"Error on request ${request.path}", e)
    internalServerError(Some(e.getMessage))
  }

  def onClientError(request: RequestHeader, status: Int, message: String) = Future.successful {
    apiResult[JsObject](status, errorMessage = Some(message).filter(_.nonEmpty))
  }
}
