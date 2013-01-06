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
      "occasion" -> text(minLength = 10,maxLength = 100)
    )
  }


  def showReceive = withSessionPerson { sessionPerson => implicit request =>
    val occasions = Occasion.findByPerson(sessionPerson)
    Logger.info("Occasions found %d".format(occasions.size))
    Ok(views.html.receive.receive(sessionPerson,occasions))
  }


  def startPresentRegistration = withSessionPerson { sessionPerson => implicit request =>
    val occasions = Occasion.findByPerson(sessionPerson)
    simpleRegisterForm.bindFromRequest.fold (
      errors => Ok(views.html.receive.recordpresent(sessionPerson,fullPresentForm,occasions)),
      presentTitle => {
        Ok(views.html.receive.recordpresent(sessionPerson,fullPresentForm.fill(presentTitle.getOrElse(""),None,"",""),occasions))
      }
    )
  }


  def registerPresent =  withSessionPerson { sessionPerson => implicit request =>
    val occasions = Occasion.findByPerson(sessionPerson)
    fullPresentForm.bindFromRequest.fold (
      errors => {
        Logger.warn("Present registration failed")
        BadRequest(views.html.receive.recordpresent(sessionPerson,errors,occasions))
      },
      presentForm => {

        Logger.info("Add present to registry")

        Occasion.findById(presentForm._4) match {
          case Some(occasion) => {
            new Present(presentForm._1.trim, presentForm._2, presentForm._3.trim, occasion).save
            Redirect(routes.OccasionController.showOccasion(occasion.occasionId.get)).flashing("messageSuccess" -> "Present recorded")
          }
          case None => {
            Logger.warn("Occasion not found: %s".format(presentForm._4))
            NotFound(views.html.receive.recordpresent(sessionPerson,fullPresentForm.fill(
                presentForm._1, presentForm._2, presentForm._3, presentForm._4),occasions)).flashing("messageError"->"Event not found")
          }
        }
      }
    )
  }


}
