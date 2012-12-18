package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.Play.current


trait Secured {
	
  def username(request: RequestHeader) = request.session.get(Security.username)

  def isAuthenticated(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthenticated) { username =>
      Action(request => f(username)(request))
    }
  }

  private def onUnauthenticated(request: RequestHeader) = {
    Results.Redirect(routes.Application.index)
  }




  def withSessionPerson(f: Person => Request[AnyContent] => Result) = isAuthenticated {
    username => implicit request =>
      Person.findByUsername(username).map { sessionPerson =>

        f(sessionPerson)(request)

      }.getOrElse(onUnauthenticated(request))
  }

  implicit def analyticsDetails: Option[String] = None


  implicit def potentialSessionPerson(implicit session:Session) : Option[Person] = {
    session.get(Security.username).flatMap ( username => Person.findByUsername(username) )
  }


}