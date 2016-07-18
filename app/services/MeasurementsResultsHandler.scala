package services

import java.time.LocalDateTime
import java.util.UUID
import javax.inject.{Inject, Singleton}

import db.slick.BaseRepositoryService
import models.Tables
import models.Tables.{DeviceMeasurements, DeviceMeasurementsRow, WeeklyMeasurements}
import org.apache.commons.math3.stat.inference.TTest
import play.api.db.slick.DatabaseConfigProvider

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

/**
  * @author Maxim Ochenashko
  */
@Singleton
class MeasurementsResultsHandler @Inject()(val dbConfigProvider: DatabaseConfigProvider)
                                          (implicit ec: ExecutionContext) extends BaseRepositoryService {

  import driver.api._

  private[this] val tTest = new TTest()

  def genRandomData() = {
    val start = 70
    val end = 90
    val weekStart = LocalDateTime.of(2016, 5, 1, 7, 0, 0, 0)
    val rows = for {
      day <- 0 to 13
      rnd = start + Random.nextInt((end - start) + 1)
      week = weekStart.plusDays(day)
    } yield DeviceMeasurementsRow(newUuid, now, now, UUID.fromString("0fa93386-7522-4efc-8690-3ecbae8ed416"), rnd, weekStart, 0, newUuid)
    db.run(DeviceMeasurements ++= rows)
  }

  def makeWeeklyMeasurements() = {
    val existingWeeklyResults = for {w <- WeeklyMeasurements} yield w.deviceMeasurementId

    val query = for {
      m <- DeviceMeasurements.filterNot(_.uuid in existingWeeklyResults)
    } yield m

    for {
      r <- db.run(query.sortBy(_.timestamp.asc).result)
      values = r.groupBy(_.deviceId).mapValues(groupByWeek)
      firstResults <- firstResultsByDevices(values.keySet)
      withFirstResults = values map { case (deviceId, v) => firstResults.get(deviceId).filter(_.nonEmpty) -> v }
      processResult <- process(withFirstResults)
    } yield ()
  }

  private[this] def process(v: Map[Option[Seq[Tables.DeviceMeasurementsRow]], mutable.Map[LocalDateTime, Seq[Tables.DeviceMeasurementsRow]]]) = {
    val futures = v map { case (firstWeek, other) =>
      val (firstWeekResults, otherValues) = firstWeek match {
        case Some(x) => x.map(_.value.doubleValue()) -> other
        case None => other.head._2.map(_.value.doubleValue()) -> other.drop(1)
      }
        //todo save first week
      val newRows = otherValues map { case (date, measurements) =>
        val values = measurements.map(_.value.doubleValue())
        val avg = values.sum / values.length
        val left = values.foldLeft(0d)((acc, v) => acc + math.pow(v - avg, 2))
        val standardDeviation = math.sqrt(left / (values.length - 1))
        val weeklyResultId = newUuid
        val weeklyResult = Tables.WeeklyResultsRow(weeklyResultId, now, avg, tTest.t(firstWeekResults.toArray, values.toArray), standardDeviation)
        weeklyResult -> (measurements map { v => Tables.WeeklyMeasurementsRow(newUuid, v.uuid, weeklyResultId, date) })
      }
      val queries = DBIO.seq(Tables.WeeklyResults ++= newRows.keys, Tables.WeeklyMeasurements ++= newRows.flatMap(_._2))
      db.run(queries)
    }
    Future.sequence(futures)
  }

  private[this] def firstResultsByDevices(devicesIds: Set[UUID]): Future[Map[UUID, Seq[Tables.DeviceMeasurementsRow]]] = {
    def query(deviceId: UUID) = for {
      m <- Tables.DeviceMeasurements.filter(_.deviceId === deviceId).sortBy(_.timestamp.asc).take(1)
      w <- Tables.WeeklyMeasurements if w.deviceMeasurementId === m.uuid
      mt <- Tables.DeviceMeasurements if mt.uuid === w.deviceMeasurementId
    } yield mt

    val futures = devicesIds map { deviceId =>
      logger.info(query(deviceId).result.statements.mkString(""))
      for {res <- db.run(query(deviceId).result)} yield deviceId -> res
    }
    for {seq <- Future.sequence(futures)} yield seq.toMap
  }

  private[this] def groupByWeek(values: Seq[DeviceMeasurementsRow]) = {
    val map: mutable.Map[LocalDateTime, Seq[DeviceMeasurementsRow]] = mutable.Map.empty
    values foreach { v =>
      val date = v.timestamp
      val (key, value) = map
        .find { case (startDate, results) =>
          val endDate = startDate.plusWeeks(1).minusHours(1)
          (date.isEqual(startDate) || date.isAfter(startDate)) && (date.isEqual(endDate) || date.isBefore(endDate))
        }
        .map { case (startDate, results) => startDate -> (results :+ v) }
        .getOrElse(date, Seq(v))
      map.update(key, value)
    }
    map filter { case (_, v) => v.length == 7 }
  }

}
