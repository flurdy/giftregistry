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

	val ValidUsername = """^[a-zA-Z0-9-_]+$""".r

	val InvalidText = """[;#\/\\:]""".r

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
        !InvalidText.findFirstIn(email.trim).isDefined && ValidEmailAddress.findFirstIn(email.trim).isDefined
      }
    }) verifying("Username is not valid. Only alphanumeric allowed. No spaces", fields => fields match {
      case (username, fullname, email, password, confirmPassword) => {
        ValidUsername.findFirstIn(username.trim).isDefined
      }
    }) verifying("Username is already taken, please try another", fields => fields match {
      case (username, fullname, email, password, confirmPassword) => {
        !Person.findByUsername(username.trim).isDefined
      }
    })
   )

	val loginForm = Form(
		tuple(
	      "username" -> nonEmptyText(maxLength = 100),
	      "password" -> nonEmptyText(minLength = 4, maxLength = 100)
		) verifying("Username is not valid. Only alphanumeric allowed. No spaces", fields => fields match {
	      case (username, password ) => {
	        ValidUsername.findFirstIn(username.trim).isDefined
	      }
		}) verifying("Log in failed. Username does not exist or password is invalid", fields => fields match {
	      case (username, password) => {
	      	Person.authenticate(username, password).isDefined
	      }
		})
	)


	implicit def analyticsDetails: Option[String] = None


	def index = Action { implicit request =>
    findSessionUsername(request.session) match {
      case Some(username) => Ok(views.html.indexsession())
      case None => Ok(views.html.indexnosession())
    }
	}

  private def findSessionUsername(session:Session) = session.get(Security.username)


	def startRegistration = Action { implicit request =>
		simpleRegisterForm.bindFromRequest.fold (
		    formWithErrors => Redirect(routes.Application.showRegistration),
		    emailEntered => {
	    		Ok(views.html.registration(registerForm.fill(
	    			("","",emailEntered.getOrElse(""),"",""))))
		    }
		)
	}


	def showRegistration =  Action { implicit request =>
		Ok(views.html.registration(registerForm))
	}


	def register = Action { implicit request =>
		registerForm.bindFromRequest.fold (
		    formWithErrors => {
		    	BadRequest(views.html.registration(formWithErrors)).flashing(
		    		"messageError"->"Please correct your entries")
		    },
		    formEntered => {
		    		Logger.info("registering %s".format(formEntered._1))

		    		new Person(formEntered._1.trim,formEntered._2.trim,formEntered._3.trim,formEntered._4.trim).save

		        	Redirect(routes.Application.index).flashing(
		        	  "messageSuccess" -> "You have registered!",
		        	  "messageWarning" -> "Please click on the link in the email we have sent to you"
	        		)
		    }
		)
	}


	def showLogin =  Action { implicit request =>
		Ok(views.html.login(loginForm)).withNewSession
	}


	def login = Action { implicit request =>
		loginForm.bindFromRequest.fold (
		   formWithErrors => {
		   	BadRequest(views.html.login(formWithErrors))
		   },
		   formEntered => {
		    	Logger.info("logging in %s".format(formEntered._1))

		    	Person.authenticate(formEntered._1,formEntered._2) match {
		    		case Some(person) => {
		        		Redirect(routes.Application.index()).withSession(
		        			"username" -> formEntered._1).flashing(
		        			"message"->"You have logged in")
		    		}
		    		case None => BadRequest(views.html.login(loginForm.fill(formEntered._1,""))).flashing("messageError" -> "Could not find person")
		    	}

		    }
		)
	}



  def logout = Action {
    Redirect(routes.Application.index).withNewSession.flashing("message"->"You have been logged out")
  }

  implicit def sessionPerson(implicit session:Session) : Option[Person] = {
    findSessionUsername(session).flatMap ( username => Person.findByUsername(username) )
  }


}
