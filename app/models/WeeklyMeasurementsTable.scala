package models


// NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
import slick.jdbc.{GetResult => GR}



trait WeeklyMeasurementsTable {
  self: Tables =>

  import profile.api._
  

  

  /** Entity class storing rows of table WeeklyMeasurements
   *  @param uuid Database column uuid SqlType(uuid), PrimaryKey
   *  @param deviceMeasurementId Database column device_measurement_id SqlType(uuid)
   *  @param weeklyResultId Database column weekly_result_id SqlType(uuid)
   *  @param weekStartDate Database column week_start_date SqlType(timestamp) */
  case class WeeklyMeasurementsRow(uuid: java.util.UUID, deviceMeasurementId: java.util.UUID, weeklyResultId: java.util.UUID, weekStartDate: java.time.LocalDateTime)
  /** GetResult implicit for fetching WeeklyMeasurementsRow objects using plain SQL queries */
  implicit def GetResultWeeklyMeasurementsRow(implicit e0: GR[java.util.UUID], e1: GR[java.time.LocalDateTime]): GR[WeeklyMeasurementsRow] = GR{
    prs => import prs._
    WeeklyMeasurementsRow.tupled((<<[java.util.UUID], <<[java.util.UUID], <<[java.util.UUID], <<[java.time.LocalDateTime]))
  }
  /** Table description of table weekly_measurements. Objects of this class serve as prototypes for rows in queries. */
  class WeeklyMeasurements(_tableTag: Tag) extends Table[WeeklyMeasurementsRow](_tableTag, Some("health-keeper"), "weekly_measurements") {
    def * = (uuid, deviceMeasurementId, weeklyResultId, weekStartDate) <> (WeeklyMeasurementsRow.tupled, WeeklyMeasurementsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uuid), Rep.Some(deviceMeasurementId), Rep.Some(weeklyResultId), Rep.Some(weekStartDate)).shaped.<>({r=>import r._; _1.map(_=> WeeklyMeasurementsRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uuid SqlType(uuid), PrimaryKey */
    val uuid: Rep[java.util.UUID] = column[java.util.UUID]("uuid", O.PrimaryKey)
    /** Database column device_measurement_id SqlType(uuid) */
    val deviceMeasurementId: Rep[java.util.UUID] = column[java.util.UUID]("device_measurement_id")
    /** Database column weekly_result_id SqlType(uuid) */
    val weeklyResultId: Rep[java.util.UUID] = column[java.util.UUID]("weekly_result_id")
    /** Database column week_start_date SqlType(timestamp) */
    val weekStartDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("week_start_date")

    /** Foreign key referencing DeviceMeasurements (database name weekly_measurements_device_measurement_id_fkey) */
    lazy val deviceMeasurementsFk = foreignKey("weekly_measurements_device_measurement_id_fkey", deviceMeasurementId, DeviceMeasurements)(r => r.uuid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing WeeklyResults (database name weekly_measurements_weekly_result_id_fkey) */
    lazy val weeklyResultsFk = foreignKey("weekly_measurements_weekly_result_id_fkey", weeklyResultId, WeeklyResults)(r => r.uuid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table WeeklyMeasurements */
  lazy val WeeklyMeasurements = new TableQuery(tag => new WeeklyMeasurements(tag))

}


    
