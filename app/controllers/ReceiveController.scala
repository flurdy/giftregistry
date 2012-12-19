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
      "from" -> text(minLength=2, maxLength = 100),
      "event" -> text(maxLength = 100)
    )
  }


  def showReceive = withSessionPerson { sessionPerson => implicit request =>
    val events = Event.findByPerson(sessionPerson)
    Ok(views.html.receive.receive(sessionPerson,events))
  }


  def startPresentRegistration = withSessionPerson { sessionPerson => implicit request =>
    simpleRegisterForm.bindFromRequest.fold (
      errors => Ok(views.html.receive.recordpresent(sessionPerson,fullPresentForm)),
      presentTitle => {
        Ok(views.html.receive.recordpresent(sessionPerson,fullPresentForm.fill(presentTitle.getOrElse(""),None,"","")))
      }
    )
  }


  def registerPresent =  withSessionPerson { sessionPerson => implicit request =>
    fullPresentForm.bindFromRequest.fold (
      errors => {
        Logger.warn("Present registration failed")
        BadRequest(views.html.receive.recordpresent(sessionPerson,errors))
      },
      presentForm => {

        Logger.info("Add present to registry")

        Event.findById(presentForm._4) match {
          case Some(event) => {
            new Present(presentForm._1.trim, presentForm._2, presentForm._3.trim, event).save
            Redirect(routes.ReceiveController.showReceive()).flashing("messageSuccess" -> "Present recorded")
          }
          case None => {
            Logger.warn("Event not found")
            NotFound(views.html.receive.recordpresent(sessionPerson,fullPresentForm.fill(
                presentForm._1, presentForm._2, presentForm._3, presentForm._4))).flashing("messageError"->"Event not found")
          }
        }
      }
    )
  }


}
