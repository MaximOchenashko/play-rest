package rest.security

import akka.actor.{ActorRef, ActorRefFactory, Props}
import akka.stream.Materializer
import common.enums.UserRole
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc.{ActionBuilder, ActionFilter, ActionTransformer, Controller, Request, WebSocket}
import rest.ApiResponses
import rest.security.SecurityActions.AuthorizedRequest
import services.security.AuthService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @author Maxim Ochenashko
  */
trait SecurityActions {
  self: Controller with ApiResponses =>

  def authService: AuthService

  protected def authorized(allowedRoles: Seq[UserRole] = UserRole.values) = Authenticated andThen authCheck(allowedRoles)

  protected def authorizedWS[In, Out](token: String)(props: ActorRef => Props)
                                     (implicit factory: ActorRefFactory, mat: Materializer, transformer: MessageFlowTransformer[In, Out]) =
    WebSocket.acceptOrResult[In, Out] { request =>
      authService.authenticate(token) map {
        case Some(x) => Right(ActorFlow.actorRef[In, Out](props))
        case None => Left(unauthorized())
      }
    }

  private object Authenticated extends ActionBuilder[AuthorizedRequest] with ActionTransformer[Request, AuthorizedRequest] {
    override protected def transform[A](request: Request[A]) = request.headers.get(AUTHORIZATION) match {
      case Some(key) => authService.authenticate(key) map { info => AuthorizedRequest(info.orNull, request) }
      case None => Future.successful(AuthorizedRequest(null, request))
    }
  }

  private def authCheck(allowedRoles: Seq[UserRole]): ActionFilter[AuthorizedRequest] =
    new ActionFilter[AuthorizedRequest] {

      import play.api.mvc.Result

      override protected def filter[A](request: AuthorizedRequest[A]) = Future.successful {
        Option(request.authInfo)
          .filter(a => allowedRoles.contains(a.role))
          .fold[Option[Result]](Some(unauthorized()))(x => Option.empty[Result])
      }
    }

}

object SecurityActions {

  import play.api.mvc.WrappedRequest
  import services.security.AuthService.AuthInfo

  case class AuthorizedRequest[X](authInfo: AuthInfo, request: Request[X]) extends WrappedRequest[X](request)

}