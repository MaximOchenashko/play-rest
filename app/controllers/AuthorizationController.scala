package controllers

import javax.inject.{Inject, Singleton}

import common.enums.UserRole
import controllers.AuthorizationController._
import play.api.data.validation.ValidationError
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{Result, Action}
import rest.controllers.RestController
import services.security.AuthService
import services.security.AuthService.AuthInfo

import scala.concurrent.{Future, ExecutionContext}
import scalaz._
import Scalaz._
import EitherT._

/**
  * @author Maxim Ochenashko
  */
@Singleton
class AuthorizationController @Inject()(val authService: AuthService)
                                       (implicit val messagesApi: MessagesApi, ec: ExecutionContext) extends RestController {

  def signIn = Action.async(jsonParse[Credentials]) { request =>
    val credentials = request.body
    eitherT(authService.authenticate(credentials.email.trim, credentials.password.trim))
      .leftAs(badRequest(Messages("signIn.errors.userNotFound").some))
      .flatMap { user =>
        val authInfo = AuthInfo(user.uuid, user.email, user.firstName, user.lastName, UserRole.byCode(user.role).get)
        EitherT.right[Future, Result, Result](for {
          tokenKey <- authService.authorize(authInfo, credentials.rememberMe)
          response = AuthResponse(tokenKey, user.fullName, user.role)
        } yield ok(Some(response)).withHeaders(AUTHORIZATION -> tokenKey))
      }
      .merge
  }

  def signUp = Action.async(jsonParse[SignUp]) { request =>
    val dto = request.body
    val saveResult = for {
      _ <- eitherT(authService.userExists(dto.email))
      _ <- eitherT(authService.createUser(dto.firstName, dto.lastName, dto.email, dto.password, dto.role))
    } yield created()

    saveResult.leftMap(e => badRequest(e.message)).merge
  }

  def logout = authorized() { implicit request =>
    authService.logout(tokenKey.get)
    ok()
  }

}

object AuthorizationController {

  case class Credentials(email: String, password: String, rememberMe: Boolean)

  case class SignUp(firstName: String, lastName: String, email: String, password: String, role: UserRole)

  case class AuthResponse(tokenKey: String, name: String, role: Int)

  val PasswordPattern = "^((?=\\S*?[A-Z])(?=\\S*?[a-z])(?=\\S*?[0-9]).{5,20})\\S$".r
  val NamePattern = "^[\\w\\d\\s-']{3,25}$".r

  implicit val credentialsReads: Reads[Credentials] = Json.reads[Credentials]

  implicit val authResponseWrites: Writes[AuthResponse] = Json.writes[AuthResponse]

  implicit val signUpReads: Reads[SignUp] = new Reads[SignUp] {

    import play.api.libs.functional.syntax.toFunctionalBuilderOps

    override def reads(json: JsValue): JsResult[SignUp] = {
      val confirmPasswordValidator: Reads[String] =
        constraints.filter[String](ValidationError("signUp.errors.invalidConfirmPassword")) { confirm =>
          (json \ "password").asOpt[String].contains(confirm)
        }

      val readFunction = ((JsPath \ "firstName").read[String](pattern(NamePattern, "signUp.errors.invalidFirstName")) and
        (JsPath \ "lastName").read[String](pattern(NamePattern, "signUp.errors.invalidLastName")) and
        (JsPath \ "email").read[String](email) and
        (JsPath \ "role").read[UserRole](UserRole.apiJsonReads) and
        (JsPath \ "password").read[String](pattern(PasswordPattern, "signUp.errors.invalidPassword")) and
        (JsPath \ "confirmPassword").read[String](confirmPasswordValidator)) { (fName, lName, email, role, pwd, cpwd) =>
        SignUp(fName, lName, email, pwd, role)
      }

      json.validate(readFunction)
    }
  }


}
