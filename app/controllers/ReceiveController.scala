package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object ReceiveController extends Controller with Secured {

  def showReceive = withSessionPerson { sessionPerson => implicit request =>

    Ok(views.html.receive.receive(sessionPerson))

  }

}
