package common.enums.fitbit

import common.enums.base.{EnumHolder, EnumLike}

/**
  * @author Maxim Ochenashko
  */
sealed trait Scope extends EnumLike {
  def requestValue: String
}

object Scope extends EnumHolder[Scope] {
  override def values: Seq[Scope] = Seq(Activity, HeartRate, Location, Nutrition, Settings, Sleep, Social, Weight)

  case object Activity extends Scope {
    override def name: String = "Activity"
    override def requestValue: String = "activity"
    override def code: Int = 0
  }

  case object HeartRate extends Scope {
    override def name: String = "HeartRate"
    override def requestValue: String = "heartrate"
    override def code: Int = 1
  }

  case object Location extends Scope {
    override def name: String = "Location"
    override def requestValue: String = "location"
    override def code: Int = 2
  }

  case object Nutrition extends Scope {
    override def name: String = "Nutrition"
    override def requestValue: String = "nutrition"
    override def code: Int = 3
  }

  case object Profile extends Scope {
    override def name: String = "Profile"
    override def requestValue: String = "profile"
    override def code: Int = 4
  }

  case object Settings extends Scope {
    override def name: String = "Activity"
    override def requestValue: String = "activity"
    override def code: Int = 5
  }

  case object Sleep extends Scope {
    override def name: String = "Sleep"
    override def requestValue: String = "sleep"
    override def code: Int = 6
  }

  case object Social extends Scope {
    override def name: String = "Social"
    override def requestValue: String = "social"
    override def code: Int = 7
  }

  case object Weight extends Scope {
    override def name: String = "Weight"
    override def requestValue: String = "weight"
    override def code: Int = 8
  }

}
