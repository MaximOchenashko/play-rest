package services

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import db.slick.BaseRepositoryService
import db.slick.SlickServiceResults.{SlickMaybeError, SlickResult}
import models.Tables.{MedicalCards, MedicalCardsRow}
import models.UsersTable.UserID
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Maxim Ochenashko
  */
@Singleton
class MedicalCardService @Inject()(val dbConfigProvider: DatabaseConfigProvider)
                                  (implicit ec: ExecutionContext) extends BaseRepositoryService {

  import MedicalCardService._
  import driver.api._

  def update(userId: UserID, birthDate: LocalDate, weight: Double, height: Double): Future[SlickMaybeError] =
    db.run(UpdateQuery(userId).extract.result.headOption) flatMap {
      case Some(x) =>
        executeUpdate(UpdateQuery(userId).extract.update((birthDate, weight, height)))
      case None =>
        executeSave(MedicalCards += MedicalCardsRow(newUuid, now, now, userId, birthDate, weight, height))
    }

  def byUserId(userId: UserID): Future[SlickResult[HealthInfo]] =
    executeSingleResult(CardByIdQuery(userId).extract)

}

object MedicalCardService {

  import models.Tables._
  import models.Tables.profile.api._

  private val UpdateQuery = Compiled { (userId: Rep[UserID]) =>
    for {
      m <- MedicalCards if m.userId === userId
    } yield (m.birthDate, m.height, m.weight)
  }

  private val CardByIdQuery = Compiled { (userId: Rep[UserID]) =>
    for {
      m <- MedicalCards if m.userId === userId
    } yield (m.birthDate, m.weight, m.height).<>[HealthInfo, (LocalDate, BigDecimal, BigDecimal)](
      { case (birthDate, weight, height) => HealthInfo(birthDate, weight.doubleValue(), height.doubleValue()) },
      { _ => throw new IllegalStateException("Update is not supported") }
    )
  }

  case class HealthInfo(birthDate: LocalDate, weight: Double, height: Double)

}
