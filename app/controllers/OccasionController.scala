package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object OccasionController extends Controller with Secured {

  val simpleOccasionForm = Form {
    "title" -> text(minLength=2,maxLength = 100)
  }



  def createOccasion = withSessionPerson { sessionPerson => implicit request =>
    simpleOccasionForm.bindFromRequest.fold (
      errors => {
        val occasions = Occasion.findByPerson(sessionPerson)
        Ok(views.html.receive.receive(sessionPerson,occasions))
      },
      occasionTitle => {
        val occasion = new Occasion(occasionTitle.trim,sessionPerson).save
        Logger.info("Occation created %s".format(occasion.occasionId.get))
        Redirect(routes.ReceiveController.showReceive()).flashing("messageSuccess"->"Occasion added")
      }
    )
  }

  def showOccasion(occasionId:String) = withSessionPerson { sessionPerson => implicit request =>
    NotImplemented("Coming soon")
  }




}
