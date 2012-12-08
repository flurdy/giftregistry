package controllers

import play.api.Play.current
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
// import notifiers._
// import java.math.BigInteger
// import java.security.SecureRandom
// import dispatch._

object Application extends Controller with Secured {

	val ValidEmailAddress = """^[^@]+@[^@]+$""".r

	val simpleRegisterForm = Form(		
		"email" -> optional(text(maxLength = 100))
	)

	val registerForm = Form(		
		tuple(
      "username" -> nonEmptyText(maxLength = 100),
      "fullname" -> nonEmptyText(maxLength = 100),
      "email" -> nonEmptyText(maxLength = 100),
      "password" -> nonEmptyText(minLength = 4, maxLength = 100),
      "confirm" -> nonEmptyText(minLength = 4, maxLength = 100)
    ) verifying("Passwords do not match", fields => fields match {
      case (username, fullname, email, password, confirmPassword) => {
        password.trim == confirmPassword.trim
     }
    }) verifying("Email address is not valid", fields => fields match {
      case (username, fullname, email, password, confirmPassword) => {
        ValidEmailAddress.findFirstIn(email.trim).isDefined
      }
    })
   )

	implicit def analyticsDetails: Option[String] = None

	def index = Action { implicit request =>
		Ok(views.html.index())
	}

	def startRegistration = Action { implicit request =>
		simpleRegisterForm.bindFromRequest.fold (
		    formWithErrors => Redirect(routes.Application.showRegistration),
		    emailEntered => {
		        	Redirect(routes.Application.showRegistration()).flashing(
		        	  "emailEntered" -> emailEntered.toString
		        	)
		    }
		)		
	}

	def showRegistration =  Action { implicit request =>
		Ok(views.html.registration(registerForm))
	}

	def register = Action { implicit request =>
		registerForm.bindFromRequest.fold (
		    formWithErrors => {
		    	BadRequest(views.html.registration(formWithErrors)).flashing("messageError"->"Please correct your entries")
		    },
		    formEntered => {
		    		Logger.info("registering %s".format(formEntered._1))

		    		// TODO 

		        	Redirect(routes.Application.index).flashing(
		        	  "messageSuccess" -> """
		        	  	You have registered! 
		        	  	Please click on the link in the email we have sent to you 
		        	  """
	        		)		     
		    }
		)		
	}

	def login = TODO

}