package common.enums

import common.enums.base.{EnumHolder, EnumLike}

/**
  * @author Maxim Ochenashko
  */
sealed trait UserStatus extends EnumLike

object UserStatus extends EnumHolder[UserStatus] {
  override def values: Seq[UserStatus] = Seq(Active, NotVerified)

  case object Active extends UserStatus {
    override def name: String = "Active"
    override def code: Int = 0
  }

  case object NotVerified extends UserStatus {
    override def name: String = "NotVerified"
    override def code: Int = 1
  }
}
