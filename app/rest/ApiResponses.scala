package rest

import java.io.File

import models.{NameIdModel, UUIDModel}
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.{Format, JsObject, Json, Writes}
import play.api.mvc.{Result, Results}
import rest.ApiResponses.{ListWrapper, MetadataWrapper}
import db.slick.BaseRepositoryService.ListQueryResult

import scala.util.control.NonFatal

/**
  * @author Maxim Ochenashko
  */
trait ApiResponses extends Status {

  //100

  //200
  def ok[X](listQueryResult: ListQueryResult[X])(implicit fmt: Writes[X]): Result =
    ok(listQueryResult.values, listQueryResult.offset, listQueryResult.limit, listQueryResult.total)

  def ok[X](list: Seq[X], offset: Long, limit: Long, total: Long)(implicit fmt: Writes[X]): Result =
    ok(Some(ListWrapper[X](list, offset, limit, total)))

  def ok[X](list: Seq[X])(implicit fmt: Writes[X]): Result = ok(Some(ListWrapper[X](list)))

  def ok[X](content: Option[X])(implicit fmt: Writes[X]): Result = apiResult(OK, content)

  def ok(): Result = ok[String](Option.empty[String])(Writes.StringWrites)

  def fileResult(file: File, filename: String): Result =
    Results.Status(OK).sendFile(file, inline = false, _ => filename)

  def fileResult(data: Array[Byte], dispositionHeader: String) =
    Results.Ok(data).as(MimeTypes.BINARY).withHeaders(HeaderNames.CONTENT_DISPOSITION -> dispositionHeader)

  def created(errorMessage: Option[String] = None) = apiResult[JsObject](CREATED, errorMessage = errorMessage)

  def noContent(errorMessage: Option[String] = None) = apiResult[JsObject](NO_CONTENT, errorMessage = errorMessage)

  //300

  //400
  def badRequest[X](content: X)(implicit fmt: Writes[X]): Result = apiResult[X](Status.BAD_REQUEST, Some(content))

  def badRequest(errorMessage: Option[String] = None): Result = apiResult[JsObject](Status.BAD_REQUEST, errorMessage = errorMessage)

  def unauthorized(errorMessage: Option[String] = None) = apiResult[JsObject](UNAUTHORIZED, errorMessage = errorMessage)

  def forbidden(errorMessage: Option[String] = None) = apiResult[JsObject](FORBIDDEN, errorMessage = errorMessage)

  def notFound(errorMessage: Option[String] = None) = apiResult[JsObject](NOT_FOUND, errorMessage = errorMessage)

  def conflict(errorMessage: Option[String] = None) = apiResult[JsObject](CONFLICT, errorMessage = errorMessage)

  //500
  def internalServerError(errorMessage: Option[String] = None) = apiResult[JsObject](INTERNAL_SERVER_ERROR, errorMessage = errorMessage)


  //results
  def apiResult[X](code: Int, content: Option[X] = None, errorMessage: Option[String] = None)(implicit fmt: Writes[X]): Result = {
    Results.Status(code)(Json.toJson(MetadataWrapper(content, errorMessage))).as(MimeTypes.JSON)
  }

}

object ApiResponses {

  import play.api.libs.functional.syntax._
  import play.api.libs.json.{JsArray, JsError, JsNumber, JsObject, JsPath, JsResult, JsSuccess, JsValue, Reads}

  case class MetadataWrapper[X](content: Option[X] = None, errorMessage: Option[String] = None)

  case class ListWrapper[X](items: Seq[X], offset: Long, limit: Long, total: Long)

  object ListWrapper {
    def apply[X](items: Seq[X]): ListWrapper[X] = ListWrapper(items, 0L, items.size, items.size)
  }

  implicit val uuidModelFormat: Format[UUIDModel] = Json.format[UUIDModel]

  implicit val namedIdModelFormat: Format[NameIdModel] = Json.format[NameIdModel]

  implicit def metadataWrapperWrites[X](implicit fmt: Writes[X]): Writes[MetadataWrapper[X]] = (
    (JsPath \ "content").writeNullable[X] and (JsPath \ "errorMessage").writeNullable[String]
    ) (unlift(MetadataWrapper.unapply[X]))

  implicit def listWrapperReads[X](implicit fmt: Reads[X]): Reads[ListWrapper[X]] = new Reads[ListWrapper[X]] {
    override def reads(json: JsValue): JsResult[ListWrapper[X]] = json match {
      case obj: JsObject => try {
        val items = (json \ "items").as[JsArray].value.map(_.as(fmt))
        val offset = (json \ "offset").as[Long]
        val limit = (json \ "limit").as[Long]
        val total = (json \ "total").as[Long]
        JsSuccess(ListWrapper[X](items, offset, limit, total))
      } catch {
        case NonFatal(e) => JsError(e.getMessage)
      }
      case _ => JsError("JsObject expected")
    }
  }

  implicit def listWrapperWrites[X](implicit fmt: Writes[X]): Writes[ListWrapper[X]] = new Writes[ListWrapper[X]] {
    override def writes(o: ListWrapper[X]): JsValue = Json.obj(
      "items" -> o.items.map(Json.toJson(_)(fmt)),
      "offset" -> JsNumber(o.offset),
      "limit" -> JsNumber(o.limit),
      "total" -> JsNumber(o.total)
    )
  }

}
