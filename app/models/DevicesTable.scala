package models


// NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
import slick.jdbc.{GetResult => GR}



trait DevicesTable {
  self: Tables =>

  import profile.api._
  

  

  /** Entity class storing rows of table Devices
   *  @param uuid Database column uuid SqlType(uuid), PrimaryKey
   *  @param createDate Database column create_date SqlType(timestamp)
   *  @param updateDate Database column update_date SqlType(timestamp)
   *  @param userId Database column user_id SqlType(uuid)
   *  @param name Database column name SqlType(varchar), Length(255,true)
   *  @param deviceType Database column device_type SqlType(int4) */
  case class DevicesRow(uuid: java.util.UUID, createDate: java.time.LocalDateTime, updateDate: java.time.LocalDateTime, userId: UsersTable.UserID, name: String, deviceType: Int)
  /** GetResult implicit for fetching DevicesRow objects using plain SQL queries */
  implicit def GetResultDevicesRow(implicit e0: GR[java.util.UUID], e1: GR[java.time.LocalDateTime], e2: GR[UsersTable.UserID], e3: GR[String], e4: GR[Int]): GR[DevicesRow] = GR{
    prs => import prs._
    DevicesRow.tupled((<<[java.util.UUID], <<[java.time.LocalDateTime], <<[java.time.LocalDateTime], <<[UsersTable.UserID], <<[String], <<[Int]))
  }
  /** Table description of table devices. Objects of this class serve as prototypes for rows in queries. */
  class Devices(_tableTag: Tag) extends Table[DevicesRow](_tableTag, Some("health-keeper"), "devices") {
    def * = (uuid, createDate, updateDate, userId, name, deviceType) <> (DevicesRow.tupled, DevicesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uuid), Rep.Some(createDate), Rep.Some(updateDate), Rep.Some(userId), Rep.Some(name), Rep.Some(deviceType)).shaped.<>({r=>import r._; _1.map(_=> DevicesRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uuid SqlType(uuid), PrimaryKey */
    val uuid: Rep[java.util.UUID] = column[java.util.UUID]("uuid", O.PrimaryKey)
    /** Database column create_date SqlType(timestamp) */
    val createDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("create_date")
    /** Database column update_date SqlType(timestamp) */
    val updateDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("update_date")
    /** Database column user_id SqlType(uuid) */
    val userId: Rep[UsersTable.UserID] = column[UsersTable.UserID]("user_id")
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column device_type SqlType(int4) */
    val deviceType: Rep[Int] = column[Int]("device_type")

    /** Foreign key referencing Users (database name devices_user_id_fkey) */
    lazy val usersFk = foreignKey("devices_user_id_fkey", userId, Users)(r => r.uuid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Devices */
  lazy val Devices = new TableQuery(tag => new Devices(tag))

}


    
