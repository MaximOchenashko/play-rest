package common.enums

import common.enums.base.{EnumHolder, EnumLike}

/**
  * @author Maxim Ochenashko
  */
sealed trait UserRole extends EnumLike

object UserRole extends EnumHolder[UserRole] {
  override def values: Seq[UserRole] = Seq(Patient, Doctor)

  case object Patient extends UserRole {
    override def name: String = "Patient"
    override def code: Int = 0
  }

  case object Doctor extends UserRole {
    override def name: String = "Doctor"
    override def code: Int = 1
  }
}
