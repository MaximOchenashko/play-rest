package models


// NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
import slick.jdbc.{GetResult => GR}



trait MeasurementsSimilaritiesTable {
  self: Tables =>

  import profile.api._
  

  

  /** Entity class storing rows of table MeasurementsSimilarities
   *  @param uuid Database column uuid SqlType(uuid), PrimaryKey
   *  @param firstWeeklyMeasurementId Database column first_weekly_measurement_id SqlType(uuid)
   *  @param secondWeeklyMeasurementId Database column second_weekly_measurement_id SqlType(uuid)
   *  @param correlationCoefficient Database column correlation_coefficient SqlType(numeric) */
  case class MeasurementsSimilaritiesRow(uuid: java.util.UUID, firstWeeklyMeasurementId: java.util.UUID, secondWeeklyMeasurementId: java.util.UUID, correlationCoefficient: scala.math.BigDecimal)
  /** GetResult implicit for fetching MeasurementsSimilaritiesRow objects using plain SQL queries */
  implicit def GetResultMeasurementsSimilaritiesRow(implicit e0: GR[java.util.UUID], e1: GR[scala.math.BigDecimal]): GR[MeasurementsSimilaritiesRow] = GR{
    prs => import prs._
    MeasurementsSimilaritiesRow.tupled((<<[java.util.UUID], <<[java.util.UUID], <<[java.util.UUID], <<[scala.math.BigDecimal]))
  }
  /** Table description of table measurements_similarities. Objects of this class serve as prototypes for rows in queries. */
  class MeasurementsSimilarities(_tableTag: Tag) extends Table[MeasurementsSimilaritiesRow](_tableTag, Some("health-keeper"), "measurements_similarities") {
    def * = (uuid, firstWeeklyMeasurementId, secondWeeklyMeasurementId, correlationCoefficient) <> (MeasurementsSimilaritiesRow.tupled, MeasurementsSimilaritiesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uuid), Rep.Some(firstWeeklyMeasurementId), Rep.Some(secondWeeklyMeasurementId), Rep.Some(correlationCoefficient)).shaped.<>({r=>import r._; _1.map(_=> MeasurementsSimilaritiesRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uuid SqlType(uuid), PrimaryKey */
    val uuid: Rep[java.util.UUID] = column[java.util.UUID]("uuid", O.PrimaryKey)
    /** Database column first_weekly_measurement_id SqlType(uuid) */
    val firstWeeklyMeasurementId: Rep[java.util.UUID] = column[java.util.UUID]("first_weekly_measurement_id")
    /** Database column second_weekly_measurement_id SqlType(uuid) */
    val secondWeeklyMeasurementId: Rep[java.util.UUID] = column[java.util.UUID]("second_weekly_measurement_id")
    /** Database column correlation_coefficient SqlType(numeric) */
    val correlationCoefficient: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("correlation_coefficient")

    /** Foreign key referencing WeeklyMeasurements (database name measurements_similarities_first_weekly_measurement_id_fkey) */
    lazy val weeklyMeasurementsFk1 = foreignKey("measurements_similarities_first_weekly_measurement_id_fkey", firstWeeklyMeasurementId, WeeklyMeasurements)(r => r.uuid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing WeeklyMeasurements (database name measurements_similarities_second_weekly_measurement_id_fkey) */
    lazy val weeklyMeasurementsFk2 = foreignKey("measurements_similarities_second_weekly_measurement_id_fkey", secondWeeklyMeasurementId, WeeklyMeasurements)(r => r.uuid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table MeasurementsSimilarities */
  lazy val MeasurementsSimilarities = new TableQuery(tag => new MeasurementsSimilarities(tag))

}


    
