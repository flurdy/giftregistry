package controllers


import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object EventController extends Controller with Secured {

  val simpleEventForm = Form {
    "title" -> text(minLength=2,maxLength = 100)
  }



  def createEvent = withSessionPerson { sessionPerson => implicit request =>
    simpleEventForm.bindFromRequest.fold (
      errors => Ok(views.html.receive.receive(sessionPerson)),
      eventTitle => {
        new Event(eventTitle.trim).save
        Redirect(routes.ReceiveController.showReceive()).flashing("messageSuccess"->"Event added")
      }
    )
  }


}
