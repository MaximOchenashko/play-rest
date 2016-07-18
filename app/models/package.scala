import java.util.UUID

import models.UsersTable.UserID
import play.api.libs.json.{Reads, Writes, JsSuccess, JsResult, JsValue, Json, Format}

import scalaz.Tag

/**
  * @author Maxim Ochenashko
  */
package object models {

  case class UUIDModel(uuid: UUID)

  case class NameIdModel(uuid: UUID, name: String)

  object QueryProjections {

    import Tables._
    import Tables.profile.api._

    def user2nameIdModel(u: Tables.Users) =
      (u.uuid, u.firstName, u.lastName).<>[NameIdModel, (UserID, String, String)](
        { case (uuid, fName, lName) => NameIdModel(Tag.unwrap(uuid), fName + " " + lName) },
        { _ => throw new IllegalStateException("Update is not supported") }
      )

  }

  object JsonFormats {

    import scalaz._
    import scala.language.implicitConversions

    implicit val namedIdModelFormat: Format[NameIdModel] = Json.format[NameIdModel]

    implicit def taggedTypeFormat[Source, Tag](implicit w: Writes[Source], r: Reads[Source]): Format[Source @@ Tag] =
      new Format[Source @@ Tag] {

        override def reads(json: JsValue): JsResult[Source @@ Tag] =
          JsSuccess(Tag[Source, Tag](json.as[Source]))

        override def writes(o: Source @@ Tag): JsValue =
          Json.toJson(Tag.unwrap(o))
      }
  }

}
