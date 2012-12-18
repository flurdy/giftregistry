package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object ReceiveController extends Controller with Secured {

  val simpleRegisterForm = Form {
    "title" -> optional(text(maxLength = 100))
  }

  val fullPresentForm = Form {
    tuple(
      "title" -> text(minLength=2, maxLength = 100),
      "description" -> optional(text(maxLength = 2000)),
      "from" -> text(minLength=2, maxLength = 100)
    )
  }

  def showReceive = withSessionPerson { sessionPerson => implicit request =>

    Ok(views.html.receive.receive(sessionPerson))

  }

  def startPresentRegistration = withSessionPerson { sessionPerson => implicit request =>
    simpleRegisterForm.bindFromRequest.fold (
      errors => {
        Ok(views.html.receive.recordpresent(sessionPerson,fullPresentForm))
      },
      presentTitle => {

        Ok(views.html.receive.recordpresent(sessionPerson,fullPresentForm.fill(presentTitle.getOrElse(""),None,"")))
      }
    )
  }

  def registerPresent = TODO

}
