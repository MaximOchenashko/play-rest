package services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import common.enums.DeviceType
import db.slick.BaseRepositoryService
import db.slick.BaseRepositoryService.ListQueryResult
import db.slick.SlickServiceResults.SlickMaybeError
import db.slick.extensions.SlickQueryExtension.ListQueryParams
import models.Tables._
import models.UsersTable.UserID
import play.api.db.slick.DatabaseConfigProvider
import services.DeviceService.DeviceListItem

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Maxim Ochenashko
  */
@Singleton
class DeviceService @Inject()(val dbConfigProvider: DatabaseConfigProvider)
                             (implicit ec: ExecutionContext) extends BaseRepositoryService {

  import profile.api._

  def list(userId: UserID, params: ListQueryParams): Future[ListQueryResult[DeviceListItem]] = {
    val query = for {
      device <- Devices if device.userId === userId
    } yield device

    executeList(query, params, deviceListQueryProjection)
  }

  def create[X](userId: UserID, name: String, deviceType: DeviceType): Future[SlickMaybeError] =
    executeSave(Devices += DevicesRow(newUuid, now, now, userId, name, deviceType.code))

  private[this] def deviceListQueryProjection(d: Devices) =
    (d.uuid, d.name, d.deviceType).<>[DeviceListItem, (UUID, String, Int)](
      { case (uuid, name, deviceType) => DeviceListItem(uuid, name, DeviceType.byCode(deviceType).get)},
      { _ => throw new IllegalStateException("Update is not supported")}
    )
}

object DeviceService {

  case class NewDevice(name: String, deviceType: DeviceType)

  case class DeviceListItem(uuid: UUID, name: String, deviceType: DeviceType)

}

