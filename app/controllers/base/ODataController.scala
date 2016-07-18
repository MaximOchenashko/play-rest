package controllers.base

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

import play.api.mvc.Request
import db.slick.extensions.SlickQueryExtension.{Filter, ListQueryParams, OrderBy, Pagination}

/**
  * @author Maxim Ochenashko
  */
trait ODataController {

  protected def queryParams(implicit request: Request[_]): ListQueryParams =
    ListQueryParams(filter, orderBy, pagination)

  protected def pagination(implicit request: Request[_]): Option[Pagination] = Some(
    Pagination(
      decodedQueryString("offset").map(_.toLong).getOrElse(0L),
      decodedQueryString("limit").map(_.toLong).getOrElse(30L)
    )
  )

  protected def filter(implicit request: Request[_]): Option[Filter] =
    decodedQueryString("$filter") map Filter.apply

  protected def orderBy(implicit request: Request[_]): Option[OrderBy] =
    decodedQueryString("$orderby") map OrderBy.apply

  protected def decodedQueryString(key: String)(implicit request: Request[_]) =
    request.getQueryString(key).map(urlDecode)

  protected def urlDecode(source: String) = URLDecoder.decode(source, StandardCharsets.UTF_8.name)

}