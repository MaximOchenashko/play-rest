package common.config

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
  * @author Maxim Ochenashko
  */
class GuiceConfiguration extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {

  }

}
