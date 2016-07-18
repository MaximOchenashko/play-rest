package controllers.fitbit

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import javax.inject.{Inject, Singleton}

import common.enums.fitbit.Scope
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.Action
import rest.controllers.RestController
import services.FitbitAuthService
import services.FitbitAuthService.FitbitTokenResponse
import services.security.AuthService
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @author Maxim Ochenashko
  */
@Singleton
class FitbitAuthController @Inject()(fitbitAuth: FitbitAuthService,
                                     ws: WSClient,
                                     val authService: AuthService,
                                     dbConfigProvider: DatabaseConfigProvider,
                                     implicit val messagesApi: MessagesApi) extends RestController {

  /*implicit val writes = Json.writes[FitbitTokenResponse]

  def requestAuth = authorized().async { request =>
    Future.successful {
      val scope = Scope.values.map(_.requestValue).mkString(" ")
      val state = UUID.randomUUID.toString
      val url = fitbitAuth.authorizationUrl(scope, state)
      ok(Some(url)).withSession("oauth-state" -> state, "userId" -> request.authInfo.uuid.toString)
    }
  }

  def callback(codeOpt: Option[String] = None, stateOpt: Option[String]) = Action.async { implicit request =>
    (for {
      code <- codeOpt
      state <- stateOpt
      oauthState <- request.session.get("oauth-state")
      userUuid <- request.session.get("userId")
    } yield {
      Logger.info("callback")
      if (state == oauthState) {
        fitbitAuth.token(code) flatMap {
          case FitbitTokenResponse(accessToken, expiresIn, refreshToken, tokenType, userId) =>
            val user = UUID.fromString(userUuid)
            val db = dbConfigProvider.get[JdbcProfile].db
            val userQuery = for {f <- FitbitIntegrations if f.userId === user && f.fitbitUserId === userId} yield f
            db.run(userQuery.result).map(_.headOption) flatMap {
              case Some(x) => Future.successful(conflict())
              case None =>
                val insertQuery = FitbitIntegrationsRow(
                  UUID.randomUUID(),
                  Timestamp.from(Instant.now),
                  Timestamp.from(Instant.now),
                  user,
                  UUID.randomUUID(),
                  userId,
                  code,
                  accessToken,
                  refreshToken
                )
                db.run(FitbitIntegrations += insertQuery) map {
                  case 1 => created()
                  case _ => badRequest()
                }
            }
        } recover {
          case ex: RuntimeException => unauthorized(Some(ex.getMessage))
        }
      } else {
        Future.successful(badRequest(Some("Invalid fitbit login")))
      }
    }).getOrElse(Future.successful(badRequest(Some("No parameters supplied"))))
  }*/

}
