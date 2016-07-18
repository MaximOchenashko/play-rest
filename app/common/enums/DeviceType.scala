package common.enums

import common.enums.base.{EnumHolder, EnumLike}

/**
  * @author Maxim Ochenashko
  */
sealed trait DeviceType extends EnumLike

object DeviceType extends EnumHolder[DeviceType] {
  override def values: Seq[DeviceType] = Seq(Fitbit, Jawbone)

  case object Fitbit extends DeviceType {
    override def name: String = "Fitbit"
    override def code: Int = 0
  }

  case object Jawbone extends DeviceType {
    override def name: String = "Jawbone"
    override def code: Int = 1
  }
}
