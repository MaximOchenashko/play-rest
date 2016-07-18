package models

import scalaz._

// AUTO-GENERATED Slick data model
/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables extends DeviceMeasurementsTable with DevicesTable with MeasurementsSimilaritiesTable with MedicalCardsTable with MedicalPairRequestsTable with MedicalPairsTable with UsersTable with WeeklyMeasurementsTable with WeeklyResultsTable {
  val profile: db.slick.driver.PostgresDriverExtended
  import Tables._
  import profile.api._
  
  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(DeviceMeasurements.schema, Devices.schema, MeasurementsSimilarities.schema, MedicalCards.schema, MedicalPairRequests.schema, MedicalPairs.schema, Users.schema, WeeklyMeasurements.schema, WeeklyResults.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  
}

/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = db.slick.driver.PostgresDriverExtended
} with Tables {
  
}
      
