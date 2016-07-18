package ext.futures

import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/**
  * @author Maxim Ochenashko
  */
object FutureExt {

  implicit class LoggableFuture[X](val underlying: Future[X]) extends AnyVal {

    def logInfo(logger: => Logger)(implicit ec: ExecutionContext): Future[X] =
      handleFailure { case (message, cause) => logger.error(message, cause) }

    def logError(logger: => Logger)(implicit ec: ExecutionContext): Future[X] =
      handleFailure { case (message, cause) => logger.error(message, cause) }

    def logWarn(logger: => Logger)(implicit ec: ExecutionContext): Future[X] =
      handleFailure { case (message, cause) => logger.warn(message, cause) }

    def logDebug(logger: => Logger)(implicit ec: ExecutionContext): Future[X] =
      handleFailure { case (message, cause) => logger.debug(message, cause) }

    def handleFailure(appender: (String, Throwable) => Unit, message: => String = "Future execution error")
                     (implicit ec: ExecutionContext): Future[X] = {
      underlying onFailure { case NonFatal(e) => appender(message, e)}
      underlying
    }

  }

}
