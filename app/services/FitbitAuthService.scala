package services

import java.nio.charset.StandardCharsets
import javax.inject.{Inject, Singleton}

import org.apache.commons.codec.binary.Base64
import play.api.Configuration
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.Results
import services.FitbitAuthService._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * @author Maxim Ochenashko
  */
@Singleton
class FitbitAuthService @Inject()(configuration: Configuration, ws: WSClient) {

  lazy val fitbitClientId = configuration.getString("fitbit.client.id").get
  lazy val fitbitClientSecret = configuration.getString("fitbit.client.secret").get
  lazy val fitbitAuthUrl = configuration.getString("fitbit.auth.url").get
  lazy val fitbitTokenUrl = configuration.getString("fitbit.token.url").get
  lazy val authorizationKey = Base64.encodeBase64URLSafeString(s"$fitbitClientId:$fitbitClientSecret".getBytes(StandardCharsets.UTF_8))

  def authorizationUrl(scope: String, state: String) = fitbitAuthUrl.format(fitbitClientId, scope, state)

  def token(code: String): Future[FitbitTokenResponse] = {
    val tokenResponse = ws.url(fitbitTokenUrl)
      .withQueryString(
        "code" -> code,
        "client_id" -> fitbitClientId,
        "grant_type" -> "authorization_code")
      .withHeaders(
        HeaderNames.AUTHORIZATION -> s"Basic $authorizationKey",
        HeaderNames.CONTENT_TYPE -> MimeTypes.FORM)
      .post(Results.EmptyContent())

    tokenResponse flatMap { response =>
      response.json.validate[FitbitTokenResponse].fold(
        invalid => Future.failed[FitbitTokenResponse](new RuntimeException(invalid.head._2.head.message)),
        valid => Future.successful(valid)
      )
    }
  }

  def refreshToken(code: String, refreshToken: String): Future[WSResponse] = {
    ws.url(fitbitTokenUrl)
      .withQueryString(
        "grant_type" -> "refresh_token",
        "refresh_token" -> refreshToken)
      .withHeaders(
        HeaderNames.AUTHORIZATION -> s"Basic $authorizationKey",
        HeaderNames.CONTENT_TYPE -> MimeTypes.FORM)
      .post(Results.EmptyContent())
  }
}

object FitbitAuthService {

  case class FitbitTokenResponse(accessToken: String, expiresIn: Int, refreshToken: String, tokenType: String, userId: String)

  implicit val authorizationTokenReads: Reads[FitbitTokenResponse] = new Reads[FitbitTokenResponse] {
    override def reads(json: JsValue): JsResult[FitbitTokenResponse] = Try {
      val accessToken = (json \ "access_token").as[String]
      val expiresIn = (json \ "expires_in").as[Int]
      val refreshToken = (json \ "refresh_token").as[String]
      val tokenType = (json \ "token_type").as[String]
      val userId = (json \ "user_id").as[String]
      FitbitTokenResponse(accessToken, expiresIn, refreshToken, tokenType, userId)
    } match {
      case Success(tokenInfo) => JsSuccess(tokenInfo)
      case Failure(e) => JsError(e.getMessage)
    }
  }

}
