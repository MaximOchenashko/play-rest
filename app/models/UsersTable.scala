package models


// NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
import slick.jdbc.{GetResult => GR}

import scalaz._

trait UsersTable {
  self: Tables =>

  import profile.api._
  import UsersTable._

  implicit lazy val UserIDMapper: BaseColumnType[UserID] = MappedColumnType.base[UserID, java.util.UUID](Tag.unwrap, UserID)

  /** Entity class storing rows of table Users
   *  @param uuid Database column uuid SqlType(uuid), PrimaryKey
   *  @param createDate Database column create_date SqlType(timestamp)
   *  @param updateDate Database column update_date SqlType(timestamp)
   *  @param firstName Database column first_name SqlType(varchar), Length(255,true)
   *  @param lastName Database column last_name SqlType(varchar), Length(255,true)
   *  @param email Database column email SqlType(varchar), Length(255,true)
   *  @param status Database column status SqlType(int4)
   *  @param deleted Database column deleted SqlType(bool)
   *  @param salt Database column salt SqlType(varchar), Length(255,true)
   *  @param password Database column password SqlType(varchar), Length(255,true)
   *  @param role Database column role SqlType(int4) */
  case class UsersRow(uuid: UserID, createDate: java.time.LocalDateTime, updateDate: java.time.LocalDateTime, firstName: String, lastName: String, email: String, status: Int, deleted: Boolean, salt: String, password: String, role: Int)
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[UserID], e1: GR[java.time.LocalDateTime], e2: GR[String], e3: GR[Int], e4: GR[Boolean]): GR[UsersRow] = GR{
    prs => import prs._
    UsersRow.tupled((<<[UserID], <<[java.time.LocalDateTime], <<[java.time.LocalDateTime], <<[String], <<[String], <<[String], <<[Int], <<[Boolean], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends Table[UsersRow](_tableTag, Some("health-keeper"), "users") {
    def * = (uuid, createDate, updateDate, firstName, lastName, email, status, deleted, salt, password, role) <> (UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(uuid), Rep.Some(createDate), Rep.Some(updateDate), Rep.Some(firstName), Rep.Some(lastName), Rep.Some(email), Rep.Some(status), Rep.Some(deleted), Rep.Some(salt), Rep.Some(password), Rep.Some(role)).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column uuid SqlType(uuid), PrimaryKey */
    val uuid: Rep[UserID] = column[UserID]("uuid", O.PrimaryKey)
    /** Database column create_date SqlType(timestamp) */
    val createDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("create_date")
    /** Database column update_date SqlType(timestamp) */
    val updateDate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("update_date")
    /** Database column first_name SqlType(varchar), Length(255,true) */
    val firstName: Rep[String] = column[String]("first_name", O.Length(255,varying=true))
    /** Database column last_name SqlType(varchar), Length(255,true) */
    val lastName: Rep[String] = column[String]("last_name", O.Length(255,varying=true))
    /** Database column email SqlType(varchar), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
    /** Database column status SqlType(int4) */
    val status: Rep[Int] = column[Int]("status")
    /** Database column deleted SqlType(bool) */
    val deleted: Rep[Boolean] = column[Boolean]("deleted")
    /** Database column salt SqlType(varchar), Length(255,true) */
    val salt: Rep[String] = column[String]("salt", O.Length(255,varying=true))
    /** Database column password SqlType(varchar), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
    /** Database column role SqlType(int4) */
    val role: Rep[Int] = column[Int]("role")

    /** Uniqueness Index over (email) (database name uk_users_email) */
    val index1 = index("uk_users_email", email, unique=true)
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))

}

object UsersTable {

  trait UserIDTag
  type UserID = java.util.UUID @@ UserIDTag
  def UserID(value: java.util.UUID): UserID = Tag[java.util.UUID, UserIDTag](value)
        
}
    
