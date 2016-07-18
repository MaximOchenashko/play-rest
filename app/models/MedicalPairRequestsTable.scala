package models


// NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
import slick.jdbc.{GetResult => GR}



trait MedicalPairRequestsTable {
  self: Tables =>

  import profile.api._
  

  

  /** Entity class storing rows of table MedicalPairRequests
   *  @param uuid Database column uuid SqlType(uuid), PrimaryKey
   *  @param createDate Database column create_date SqlType(timestamp)
   *  @param updateDate Database column update_date SqlType(timestamp)
   *  @param patientId Database column patient_id SqlType(uuid)
   *  @param doctorId Database column doctor_id SqlType(uuid)
   *  @param message Database column message SqlType(varchar), Length(255,true), Default(None)
   *  @param rejectMessage Database column reject_message SqlType(varchar), Length(255,true), Default(None)
   *  @param status Database column status SqlType(int4) */
  case class MedicalPairRequestsRow(uuid: java.util.UUID, createDate: java.time.LocalDateTime, updateDate: java.time.LocalDateTime, patientId: UsersTable.UserID, doctorId: UsersTable.UserID, message: Option[String] = None, rejectMessage: Option[String] = None, status: Int)
  /** GetResult implicit for fetching MedicalPairRequestsRow objects using plain SQL queries */
  implicit def GetResultMedicalPairRequestsRow(implicit e0: GR[java.util.UUID], e1: GR[java.time.LocalDateTime], e2: GR[UsersTable.UserID], e3: GR[Option[String]], e4: GR[Int]): GR[MedicalPairRequestsRow] = GR{
    prs => import prs._
    MedicalPairRequestsRow.tupled((<<[java.util.UUID], <<[java.time.LocalDateTime], <<[java.time.LocalDateTime], <<[UsersTable.UserID], <<[UsersTable.UserID], <<?[String], <<?[String], <<[Int]))
  }
  /** Table description of table medical_pair_requests. Objects of this class serve as prototypes for rows in queries. */
  class MedicalPairRequests(_tableTag: Tag) extends Table[MedicalPairRequestsRow](_tableTag, Some("health-keeper"), "medical_pair_requests") {
    def * = (uuid, createDate, updateDate, patientId, doctorId, message, rejectMessage, status) <> (MedicalPairRequestsRow.tupled, MedicalPairRequestsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uuid), Rep.Some(createDate), Rep.Some(updateDate), Rep.Some(patientId), Rep.Some(doctorId), message, rejectMessage, Rep.Some(status)).shaped.<>({r=>import r._; _1.map(_=> MedicalPairRequestsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6, _7, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uuid SqlType(uuid), PrimaryKey */
    val uuid: Rep[java.util.UUID] = column[java.util.UUID]("uuid", O.PrimaryKey)
    /** Database column create_date SqlType(timestamp) */
    val createDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("create_date")
    /** Database column update_date SqlType(timestamp) */
    val updateDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("update_date")
    /** Database column patient_id SqlType(uuid) */
    val patientId: Rep[UsersTable.UserID] = column[UsersTable.UserID]("patient_id")
    /** Database column doctor_id SqlType(uuid) */
    val doctorId: Rep[UsersTable.UserID] = column[UsersTable.UserID]("doctor_id")
    /** Database column message SqlType(varchar), Length(255,true), Default(None) */
    val message: Rep[Option[String]] = column[Option[String]]("message", O.Length(255,varying=true), O.Default(None))
    /** Database column reject_message SqlType(varchar), Length(255,true), Default(None) */
    val rejectMessage: Rep[Option[String]] = column[Option[String]]("reject_message", O.Length(255,varying=true), O.Default(None))
    /** Database column status SqlType(int4) */
    val status: Rep[Int] = column[Int]("status")

    /** Foreign key referencing Users (database name medical_pair_requests_doctor_id_fkey) */
    lazy val usersFk1 = foreignKey("medical_pair_requests_doctor_id_fkey", doctorId, Users)(r => r.uuid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name medical_pair_requests_patient_id_fkey) */
    lazy val usersFk2 = foreignKey("medical_pair_requests_patient_id_fkey", patientId, Users)(r => r.uuid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table MedicalPairRequests */
  lazy val MedicalPairRequests = new TableQuery(tag => new MedicalPairRequests(tag))

}


    
