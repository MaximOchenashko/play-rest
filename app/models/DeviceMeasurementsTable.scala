package models


// NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
import slick.jdbc.{GetResult => GR}



trait DeviceMeasurementsTable {
  self: Tables =>

  import profile.api._
  

  

  /** Entity class storing rows of table DeviceMeasurements
   *  @param uuid Database column uuid SqlType(uuid), PrimaryKey
   *  @param createDate Database column create_date SqlType(timestamp)
   *  @param updateDate Database column update_date SqlType(timestamp)
   *  @param deviceId Database column device_id SqlType(uuid)
   *  @param value Database column value SqlType(numeric)
   *  @param timestamp Database column timestamp SqlType(timestamp)
   *  @param unit Database column unit SqlType(int4)
   *  @param measurementId Database column measurement_id SqlType(uuid) */
  case class DeviceMeasurementsRow(uuid: java.util.UUID, createDate: java.time.LocalDateTime, updateDate: java.time.LocalDateTime, deviceId: java.util.UUID, value: scala.math.BigDecimal, timestamp: java.time.LocalDateTime, unit: Int, measurementId: java.util.UUID)
  /** GetResult implicit for fetching DeviceMeasurementsRow objects using plain SQL queries */
  implicit def GetResultDeviceMeasurementsRow(implicit e0: GR[java.util.UUID], e1: GR[java.time.LocalDateTime], e2: GR[scala.math.BigDecimal], e3: GR[Int]): GR[DeviceMeasurementsRow] = GR{
    prs => import prs._
    DeviceMeasurementsRow.tupled((<<[java.util.UUID], <<[java.time.LocalDateTime], <<[java.time.LocalDateTime], <<[java.util.UUID], <<[scala.math.BigDecimal], <<[java.time.LocalDateTime], <<[Int], <<[java.util.UUID]))
  }
  /** Table description of table device_measurements. Objects of this class serve as prototypes for rows in queries. */
  class DeviceMeasurements(_tableTag: Tag) extends Table[DeviceMeasurementsRow](_tableTag, Some("health-keeper"), "device_measurements") {
    def * = (uuid, createDate, updateDate, deviceId, value, timestamp, unit, measurementId) <> (DeviceMeasurementsRow.tupled, DeviceMeasurementsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uuid), Rep.Some(createDate), Rep.Some(updateDate), Rep.Some(deviceId), Rep.Some(value), Rep.Some(timestamp), Rep.Some(unit), Rep.Some(measurementId)).shaped.<>({r=>import r._; _1.map(_=> DeviceMeasurementsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uuid SqlType(uuid), PrimaryKey */
    val uuid: Rep[java.util.UUID] = column[java.util.UUID]("uuid", O.PrimaryKey)
    /** Database column create_date SqlType(timestamp) */
    val createDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("create_date")
    /** Database column update_date SqlType(timestamp) */
    val updateDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("update_date")
    /** Database column device_id SqlType(uuid) */
    val deviceId: Rep[java.util.UUID] = column[java.util.UUID]("device_id")
    /** Database column value SqlType(numeric) */
    val value: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("value")
    /** Database column timestamp SqlType(timestamp) */
    val timestamp: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("timestamp")
    /** Database column unit SqlType(int4) */
    val unit: Rep[Int] = column[Int]("unit")
    /** Database column measurement_id SqlType(uuid) */
    val measurementId: Rep[java.util.UUID] = column[java.util.UUID]("measurement_id")

    /** Foreign key referencing Devices (database name device_measurements_device_id_fkey) */
    lazy val devicesFk = foreignKey("device_measurements_device_id_fkey", deviceId, Devices)(r => r.uuid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table DeviceMeasurements */
  lazy val DeviceMeasurements = new TableQuery(tag => new DeviceMeasurements(tag))

}


    
