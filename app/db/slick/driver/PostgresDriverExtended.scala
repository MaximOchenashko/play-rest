package db.slick.driver

import com.github.tminglei.slickpg._
import play.api.libs.json._
import slick.driver.PostgresDriver

/**
  * @author Maxim Ochenashko
  */
trait PostgresDriverExtended extends PostgresDriver
  with PgArraySupport
  with PgDate2Support
  with PgPlayJsonSupport {

  override def pgjson = "jsonb"

  override val api = MyAPI

  object MyAPI extends API with ArrayImplicits with DateTimeImplicits with JsonImplicits {
    implicit val playJsonArrayTypeMapper = new AdvancedArrayJdbcType[JsValue](pgjson,
      (s) => utils.SimpleArrayUtils.fromString[JsValue](s => Json.parse(s))(s).orNull,
      (v) => utils.SimpleArrayUtils.mkString[JsValue](s => s.toString())(v)
    ).to(_.toList)
  }

}

object PostgresDriverExtended extends PostgresDriverExtended