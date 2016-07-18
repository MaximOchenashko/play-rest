package models


// NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
import slick.jdbc.{GetResult => GR}



trait WeeklyResultsTable {
  self: Tables =>

  import profile.api._
  

  

  /** Entity class storing rows of table WeeklyResults
   *  @param uuid Database column uuid SqlType(uuid), PrimaryKey
   *  @param createDate Database column create_date SqlType(timestamp)
   *  @param averageResult Database column average_result SqlType(numeric)
   *  @param tStudent Database column t_student SqlType(numeric)
   *  @param deviation Database column deviation SqlType(numeric) */
  case class WeeklyResultsRow(uuid: java.util.UUID, createDate: java.time.LocalDateTime, averageResult: scala.math.BigDecimal, tStudent: scala.math.BigDecimal, deviation: scala.math.BigDecimal)
  /** GetResult implicit for fetching WeeklyResultsRow objects using plain SQL queries */
  implicit def GetResultWeeklyResultsRow(implicit e0: GR[java.util.UUID], e1: GR[java.time.LocalDateTime], e2: GR[scala.math.BigDecimal]): GR[WeeklyResultsRow] = GR{
    prs => import prs._
    WeeklyResultsRow.tupled((<<[java.util.UUID], <<[java.time.LocalDateTime], <<[scala.math.BigDecimal], <<[scala.math.BigDecimal], <<[scala.math.BigDecimal]))
  }
  /** Table description of table weekly_results. Objects of this class serve as prototypes for rows in queries. */
  class WeeklyResults(_tableTag: Tag) extends Table[WeeklyResultsRow](_tableTag, Some("health-keeper"), "weekly_results") {
    def * = (uuid, createDate, averageResult, tStudent, deviation) <> (WeeklyResultsRow.tupled, WeeklyResultsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uuid), Rep.Some(createDate), Rep.Some(averageResult), Rep.Some(tStudent), Rep.Some(deviation)).shaped.<>({r=>import r._; _1.map(_=> WeeklyResultsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uuid SqlType(uuid), PrimaryKey */
    val uuid: Rep[java.util.UUID] = column[java.util.UUID]("uuid", O.PrimaryKey)
    /** Database column create_date SqlType(timestamp) */
    val createDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("create_date")
    /** Database column average_result SqlType(numeric) */
    val averageResult: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("average_result")
    /** Database column t_student SqlType(numeric) */
    val tStudent: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("t_student")
    /** Database column deviation SqlType(numeric) */
    val deviation: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("deviation")
  }
  /** Collection-like TableQuery object for table WeeklyResults */
  lazy val WeeklyResults = new TableQuery(tag => new WeeklyResults(tag))

}


    
