package models


// NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
import slick.jdbc.{GetResult => GR}



trait MedicalCardsTable {
  self: Tables =>

  import profile.api._
  

  

  /** Entity class storing rows of table MedicalCards
   *  @param uuid Database column uuid SqlType(uuid), PrimaryKey
   *  @param createDate Database column create_date SqlType(timestamp)
   *  @param updateDate Database column update_date SqlType(timestamp)
   *  @param userId Database column user_id SqlType(uuid)
   *  @param birthDate Database column birth_date SqlType(date)
   *  @param weight Database column weight SqlType(numeric)
   *  @param height Database column height SqlType(numeric) */
  case class MedicalCardsRow(uuid: java.util.UUID, createDate: java.time.LocalDateTime, updateDate: java.time.LocalDateTime, userId: UsersTable.UserID, birthDate: java.time.LocalDate, weight: scala.math.BigDecimal, height: scala.math.BigDecimal)
  /** GetResult implicit for fetching MedicalCardsRow objects using plain SQL queries */
  implicit def GetResultMedicalCardsRow(implicit e0: GR[java.util.UUID], e1: GR[java.time.LocalDateTime], e2: GR[UsersTable.UserID], e3: GR[java.time.LocalDate], e4: GR[scala.math.BigDecimal]): GR[MedicalCardsRow] = GR{
    prs => import prs._
    MedicalCardsRow.tupled((<<[java.util.UUID], <<[java.time.LocalDateTime], <<[java.time.LocalDateTime], <<[UsersTable.UserID], <<[java.time.LocalDate], <<[scala.math.BigDecimal], <<[scala.math.BigDecimal]))
  }
  /** Table description of table medical_cards. Objects of this class serve as prototypes for rows in queries. */
  class MedicalCards(_tableTag: Tag) extends Table[MedicalCardsRow](_tableTag, Some("health-keeper"), "medical_cards") {
    def * = (uuid, createDate, updateDate, userId, birthDate, weight, height) <> (MedicalCardsRow.tupled, MedicalCardsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uuid), Rep.Some(createDate), Rep.Some(updateDate), Rep.Some(userId), Rep.Some(birthDate), Rep.Some(weight), Rep.Some(height)).shaped.<>({r=>import r._; _1.map(_=> MedicalCardsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uuid SqlType(uuid), PrimaryKey */
    val uuid: Rep[java.util.UUID] = column[java.util.UUID]("uuid", O.PrimaryKey)
    /** Database column create_date SqlType(timestamp) */
    val createDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("create_date")
    /** Database column update_date SqlType(timestamp) */
    val updateDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("update_date")
    /** Database column user_id SqlType(uuid) */
    val userId: Rep[UsersTable.UserID] = column[UsersTable.UserID]("user_id")
    /** Database column birth_date SqlType(date) */
    val birthDate: Rep[java.time.LocalDate] = column[java.time.LocalDate]("birth_date")
    /** Database column weight SqlType(numeric) */
    val weight: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("weight")
    /** Database column height SqlType(numeric) */
    val height: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("height")

    /** Foreign key referencing Users (database name medical_cards_user_id_fkey) */
    lazy val usersFk = foreignKey("medical_cards_user_id_fkey", userId, Users)(r => r.uuid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (userId) (database name medical_cards_user_id_key) */
    val index1 = index("medical_cards_user_id_key", userId, unique=true)
  }
  /** Collection-like TableQuery object for table MedicalCards */
  lazy val MedicalCards = new TableQuery(tag => new MedicalCards(tag))

}


    
