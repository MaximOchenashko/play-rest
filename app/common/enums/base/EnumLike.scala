package common.enums.base

import play.api.libs.json.{JsError, JsNumber, JsResult, JsSuccess, JsValue, Reads, Writes}

import scala.util.Try

/**
  * @author Maxim Ochenashko
  */
trait EnumLike {
  def name: String
  def code: Int
}

trait EnumHolder[T <: EnumLike] {
  def values: Seq[T]
  def byCode(code: Int) = values.find(_.code == code)
  def byName(name: String) = values.find(_.name == name)

  implicit val apiJsonReads: Reads[T] = new Reads[T] {
    override def reads(json: JsValue): JsResult[T] = json.validate[String]
      .flatMap(r => Try(r.toInt).toOption.fold[JsResult[Int]](JsError("error.expected.jsnumber"))(JsSuccess(_)))
      .flatMap { value =>
        byCode(value).fold[JsResult[T]](JsError("error.invalidEnumCode"))(JsSuccess(_))
      }
  }

  implicit val jsonReads: Reads[T] = new Reads[T] {
    override def reads(json: JsValue): JsResult[T] = json.validate[Int] flatMap { value =>
      byCode(value).fold[JsResult[T]](JsError("error.invalidEnumCode"))(JsSuccess(_))
    }
  }

  implicit val jsonWrites: Writes[T] = new Writes[T] {
    override def writes(o: T): JsValue = JsNumber(o.code)
  }
}
