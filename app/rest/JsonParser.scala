package rest

import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc.BodyParser
import play.api.mvc.BodyParsers.parse

import scala.concurrent.Future

/**
  * @author Maxim Ochenashko
  */
trait JsonParser {
  self: ApiResponses with I18nSupport =>

  def jsonParse[A](implicit reader: Reads[A])= BodyParser("json reader") { request =>
    import play.api.libs.iteratee.Execution.Implicits.trampoline
    parse.json(request) mapFuture {
      case Left(simpleResult) =>
        Future.successful(Left(simpleResult))
      case Right(jsValue) =>
        jsValue.validate(reader) map { a =>
          Future.successful(Right(a))
        } recoverTotal { jsError =>
          Future.successful(Left(badRequest(errorsAsJson(jsError))))
        }
    }
  }

  private def errorsAsJson(jsError: JsError) = Json.toJson(
    jsError.errors.groupBy(_._1) map { case (path, errors) =>
      //remove 'obj.' prefix
      path.toJsonString.drop(4) -> errors.flatMap(_._2).map(e => Messages(e.message, e.args.map(a => translateMsgArg(a)): _*))
    }
  )

  private def translateMsgArg(msgArg: Any) = msgArg match {
    case key: String => Messages(key)
    case keys: Seq[_] => keys collect { case key: String => Messages(key) }
    case _ => msgArg
  }

}
