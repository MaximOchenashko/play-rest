package db.slick.codegen

import java.io.File

import com.typesafe.config.ConfigFactory
import db.slick.driver.PostgresDriverExtended
import slick.codegen.SourceCodeGenerator
import slick.driver.JdbcProfile
import slick.model.Model
import slick.profile.SqlProfile.ColumnOption

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.Try

/**
  * @author Maxim Ochenashko
  */
class NewCustomizedCodeGenerator(metamodel: Model, outputDir: String,
                                 pkg: String, driver: JdbcProfile,
                                 includedTables: Seq[String]) extends SourceCodeGenerator(metamodel) {

  private[this] val slickUtilsTrait =
    s"""package $pkg
        |
        |import slick.ast.Ordering.{Desc, Asc}
        |import slick.lifted.{AbstractTable, ColumnOrdered, Rep}
        |
        |import scala.util.matching.Regex
        |
        |trait SlickUtils[X <: AbstractTable[_]] {
        |
        |  type FilterFunction = String => X => Rep[Boolean]
        |
        |  type OrderByFunction = String => X => ColumnOrdered[_]
        |
        |  def filterFunctions: Seq[(Regex, FilterFunction)]
        |
        |  def orderByFunctions: Seq[(Regex, OrderByFunction)]
        |
        |  lazy val queryFilter: String => Option[X => Rep[Boolean]] =
        |    filter => collectRegex(filter, filterFunctions)
        |
        |  lazy val orderBy: String => Option[X => ColumnOrdered[_]] =
        |    orderBy => collectRegex(orderBy, orderByFunctions)
        |
        |  protected def ord(column: X => Rep[_]): String => X => ColumnOrdered[_] = direction => table =>
        |    ColumnOrdered(column(table), slick.ast.Ordering(if (direction == "asc") Asc else Desc))
        |
        |  private def collectRegex[R](checkString: String, seq: Seq[(Regex, String => R)]): Option[R] = {
        |    val itr = seq.iterator
        |    while (itr.hasNext) {
        |      val (regex, func) = itr.next()
        |      checkString match {
        |        case regex(x) =>
        |          return Some(func(x))
        |        case _ =>
        |      }
        |    }
        |    None
        |  }
        |
        |}""".stripMargin

  writeStringToFile(slickUtilsTrait, outputDir, pkg, "SlickUtils.scala")

  tables
    .filter { table => includedTables.isEmpty || includedTables.contains(table.model.name.table) }
    .foreach { table => tableExtension(table) }

  def tableExtension(table: Table): Unit = {
    val tableName = table.model.name.table.toCamelCase
    val filter = queryFilter(table.columnsByName)
    val orderBy = queryOrderBy(table.columnsByName)

    val className = s"${tableName}Utils"
    val body =
      s"""|package $pkg
          |
          |import ${driver.getClass.getName.stripSuffix("$")}.api._
          |
          |import scala.util.matching.Regex
          |
          |trait $className extends SlickUtils[Tables.$tableName] {
          |
          |  $filter
          |
          |  $orderBy
          |}
          |
          |object $className extends $className""".stripMargin

    writeStringToFile(body, outputDir, pkg, s"${tableName}Utils.scala")
  }

  def queryOrderBy(columns: Map[String, TableDef#Column]): String = {
    val regexSeq = columns.toSeq.map(r => orderByRegex(r._1.toCamelCase.uncapitalize))
    s"""|lazy val orderByFunctions: Seq[(Regex, OrderByFunction)] = Seq(
        |    ${regexSeq.mkString(",\r\n    ")}
        |  )
      """.stripMargin
  }

  def queryFilter(columns: Map[String, TableDef#Column]): String = {
    val validTypes = Seq("String", "Int", "Float", "Long")
    val regexSeq = columns.toSeq.collect { case (name, column) if validTypes.exists(column.actualType.contains) =>
      filterRegex(column.model.nullable, validTypes.find(column.actualType.contains).get, name.toCamelCase.uncapitalize)
    }.flatten
    s"""|lazy val filterFunctions: Seq[(Regex, FilterFunction)] = Seq(
        |    ${regexSeq.mkString(",\r\n    ")}
        |  )""".stripMargin
  }

  def filterRegex(nullable: Boolean, columnType: String, columnName: String): Seq[String] =
    columnType match {
      case "String" => Seq(
        s""""contains\\\\($columnName,'([\\\\w\\\\d].*)'\\\\)".r -> ${filterQuery(nullable, columnType, columnName)}"""
      )
      case _ => Seq.empty
    }

  def filterQuery(nullable: Boolean, columnType: String, columnName: String): String =
    columnType match {
      case "String" =>
        nullable match {
          case true =>
            s"""(s => _.$columnName.filter(_.toLowerCase like s"%$${s.toLowerCase}%").isDefined)"""
          case false =>
            s"""(s => _.$columnName.toLowerCase like s"%$${s.toLowerCase}%")"""
        }
      case _ => ""
    }

  def orderByRegex(columnName: String): String = s""""$columnName (asc|desc)".r -> ord(_.$columnName)"""

  override def Table = new Table(_) {
    table =>
    override def Column = new Column(_) {
      column =>
      // customize db type -> scala type mapping, pls adjust it according to your environment
      override def rawType: String = model.tpe match {
        //case "java.sql.Date" => "java.time.LocalDate"
        //case "java.sql.Time" => "java.time.LocalTime"
        //case "java.sql.Timestamp" => "java.time.LocalDateTime"
        // currently, all types that's not built-in support were mapped to `String`
        case "String" => model.options.find(_.isInstanceOf[ColumnOption.SqlType])
          .map(_.asInstanceOf[ColumnOption.SqlType].typeName).map({
          case "jsonb" => "play.api.libs.json.JsValue"
          case _ => "String"
        }).getOrElse("String")
        case _ => super.rawType
      }
    }
  }

  // ensure to use our customized postgres driver at `import profile.simple._`
  override def packageCode(profile: String, pkg: String, container: String, parentType: Option[String]): String = {
    s"""|package $pkg
        |// AUTO-GENERATED Slick data model
        |/** Stand-alone Slick data model for immediate use */
        |object $container extends {
        |  val profile = $profile
        |} with $container
        |/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
        |trait $container${parentType.map(t => s" extends $t").getOrElse("")} {
        |  val profile: $profile
        |  import profile.api._
        |  ${indent(code)}
        |}""".stripMargin
  }

}

object NewCustomizedCodeGenerator {

  def main(args: Array[String]) = {
    val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()
    val url = conf.getString("slick.dbs.default.db.url")
    val jdbcDriver = conf.getString("slick.dbs.default.db.driver")
    val slickDriver = conf.getString("slick.dbs.default.driver").dropRight(1)
    val pkg = "models"
    val user = conf.getString("slick.dbs.default.db.user")
    val outputDir = s"""${new File("").getAbsolutePath}\\app"""
    val password = conf.getString("slick.dbs.default.db.password")

    import scala.collection.JavaConversions._
    val includedTables: Seq[String] = Try(conf.getStringList("slick.dbs.default.codegen.includedTables").toSeq).getOrElse(Seq.empty)
    run(slickDriver, jdbcDriver, url, outputDir, pkg, Some(user), Some(password), includedTables)
  }

  def run(slickDriver: String, jdbcDriver: String, url: String, outputDir: String,
          pkg: String, user: Option[String], password: Option[String], includedTables: Seq[String]): Unit = {
    val driver: JdbcProfile = PostgresDriverExtended
    val dbFactory = driver.api.Database
    val db = dbFactory.forURL(url, driver = jdbcDriver, user = user.orNull, password = password.orNull, keepAliveConnection = true)
    try {
      val m = Await.result(db.run(driver.createModel(None, ignoreInvalidDefaults = false)(ExecutionContext.global).withPinnedSession), Duration.Inf)
      new NewCustomizedCodeGenerator(m, outputDir, pkg, driver, includedTables).writeToFile(slickDriver, outputDir, pkg)
    } finally db.close
  }
}



