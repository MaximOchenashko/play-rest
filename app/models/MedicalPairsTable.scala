package models


// NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
import slick.jdbc.{GetResult => GR}



trait MedicalPairsTable {
  self: Tables =>

  import profile.api._
  

  

  /** Entity class storing rows of table MedicalPairs
   *  @param uuid Database column uuid SqlType(uuid), PrimaryKey
   *  @param createDate Database column create_date SqlType(timestamp)
   *  @param updateDate Database column update_date SqlType(timestamp)
   *  @param patientId Database column patient_id SqlType(uuid)
   *  @param doctorId Database column doctor_id SqlType(uuid) */
  case class MedicalPairsRow(uuid: java.util.UUID, createDate: java.time.LocalDateTime, updateDate: java.time.LocalDateTime, patientId: UsersTable.UserID, doctorId: UsersTable.UserID)
  /** GetResult implicit for fetching MedicalPairsRow objects using plain SQL queries */
  implicit def GetResultMedicalPairsRow(implicit e0: GR[java.util.UUID], e1: GR[java.time.LocalDateTime], e2: GR[UsersTable.UserID]): GR[MedicalPairsRow] = GR{
    prs => import prs._
    MedicalPairsRow.tupled((<<[java.util.UUID], <<[java.time.LocalDateTime], <<[java.time.LocalDateTime], <<[UsersTable.UserID], <<[UsersTable.UserID]))
  }
  /** Table description of table medical_pairs. Objects of this class serve as prototypes for rows in queries. */
  class MedicalPairs(_tableTag: Tag) extends Table[MedicalPairsRow](_tableTag, Some("health-keeper"), "medical_pairs") {
    def * = (uuid, createDate, updateDate, patientId, doctorId) <> (MedicalPairsRow.tupled, MedicalPairsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uuid), Rep.Some(createDate), Rep.Some(updateDate), Rep.Some(patientId), Rep.Some(doctorId)).shaped.<>({r=>import r._; _1.map(_=> MedicalPairsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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

    /** Foreign key referencing Users (database name medical_pairs_doctor_id_fkey) */
    lazy val usersFk1 = foreignKey("medical_pairs_doctor_id_fkey", doctorId, Users)(r => r.uuid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name medical_pairs_patient_id_fkey) */
    lazy val usersFk2 = foreignKey("medical_pairs_patient_id_fkey", patientId, Users)(r => r.uuid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (patientId,doctorId) (database name uk_medical_pairs_unqie_pair) */
    val index1 = index("uk_medical_pairs_unqie_pair", (patientId, doctorId), unique=true)
  }
  /** Collection-like TableQuery object for table MedicalPairs */
  lazy val MedicalPairs = new TableQuery(tag => new MedicalPairs(tag))

}


    
