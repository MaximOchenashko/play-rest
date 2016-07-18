package db.slick.codegen

import db.slick.codegen.TaggedExt.TaggedMetaModel
import slick.codegen.SourceCodeGenerator

/**
  * @author Maxim Ochenashko
  */
trait TaggedExt {
  self: SourceCodeGenerator =>

  def generateSharedTypes(types: Map[String, String]): Seq[TaggedMetaModel] =
    types
      .map { case (typeName, columnType) => TaggedMetaModel("", typeName, columnType) }
      .toSeq

}

object TaggedExt {

  case class TaggedMetaModel(prefix: String, columnNameRaw: String, columnType: String) {

    //uuid
    val columnName = if (columnNameRaw == "uuid") "ID" else columnNameRaw

    //GatewayID
    val typeName = prefix + columnName.capitalize

    //GatewayIDTag
    val `trait` = typeName + "Tag"

    //GatewayID = java.util.UUID @@ GatewayIDTag
    val `type` = s"""$typeName = $columnType @@ ${`trait`}"""

    //GatewayID(value: java.util.UUID): GatewayID = Tag[java.util.UUID, GatewayIDTag](value)
    val `def` = s"""$typeName(value: $columnType): $typeName = Tag[$columnType, ${`trait`}](value)"""

    val template =
      s"""
        |trait ${`trait`}
        |type ${`type`}
        |def ${`def`}
      """.stripMargin

    val columnMapper = s"implicit lazy val ${typeName}Mapper: BaseColumnType[$typeName] = MappedColumnType.base[$typeName, $columnType](Tag.unwrap, $typeName)"
  }

}
