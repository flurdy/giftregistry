package models

// import play.api.Play.current
import org.mindrot.jbcrypt.BCrypt
// import play.api.db.DB
// import anorm._
// import anorm.SqlParser._
// import play.Logger
// import java.math.BigInteger
// import java.security.SecureRandom
// import play.api.Play


case class Person(
	personId:Option[Long],
	username: String,
	fullname: String,
	email: String
){

	var passwordOption: Option[String] = None

	def this(username:String,fullname:String,email:String) = this(None,username,fullname,email)

	def encryptPassword(password:String) = {
		passwordOption = Person.encrypt(password)
		this
	}

	def save = Person.save(this)

}


object Person {

  def encrypt(password: String) = Some(BCrypt.hashpw(password,BCrypt.gensalt()))

  def save(person:Person) = {
  	// TODO
  		person
  }

  def authenticate(username:String,password:String) : Option[Person] = {
  	// TODO
  		if(username=="testuser"){
  			Some(new Person("testuser","",""))
		} else {
	  		None
		}
  }

  def findByUsername(username:String) : Option[Person] = {
  		// TODO
  		if(username=="testuser"){
  			Some(new Person("testuser","",""))
		} else {
	  		None
		}
  }

}
