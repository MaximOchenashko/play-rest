package common.enums

import common.enums.base.{EnumHolder, EnumLike}

/**
  * @author Maxim Ochenashko
  */
sealed trait PairRequestStatus extends EnumLike

object PairRequestStatus extends EnumHolder[PairRequestStatus] {
  override def values: Seq[PairRequestStatus] = Seq(New, Accepted, Rejected)

  case object New extends PairRequestStatus {
    override def name: String = "New"
    override def code: Int = 0
  }

  case object Accepted extends PairRequestStatus {
    override def name: String = "Accepted"
    override def code: Int = 1
  }

  case object Rejected extends PairRequestStatus {
    override def name: String = "Rejected"
    override def code: Int = 1
  }
}
