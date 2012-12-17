package models

// import play.api.Play.current
import org.mindrot.jbcrypt.BCrypt
import play.api._
// import play.api.db.DB
// import anorm._
// import anorm.SqlParser._
// import play.Logger
// import java.math.BigInteger
// import java.security.SecureRandom
// import play.api.Play
import com.mongodb.casbah.Imports._


case class Person(
	personId:Option[ObjectId],
	username: String,
	fullname: String,
	email: String
){

	private var passwordOption: Option[String] = None

  def this(username:String,fullname:String,email:String) = this(None,username,fullname,email)

  def this(username:String,fullname:String,email:String,password:String) = {
    this(None,username,fullname,email)
    encryptPassword(password)
  }

	private def encryptPassword(password:String) = {
		passwordOption = Person.encrypt(password)
		this
	}

  def getEncryptedPassword = passwordOption

	def save = Person.save(this)

}


object Person {

  val mongoPersonConnection = MongoConnection()("giftdb")("person")

  def encrypt(password: String) = Some(BCrypt.hashpw(password,BCrypt.gensalt()))

  def save(person:Person) = {
    val newId = new ObjectId
    val mongoObject = MongoDBObject(
          "_id" -> newId,
          "username" -> person.username,
          "fullname" -> person.fullname,
          "email" -> person.email,
          "password" -> person.getEncryptedPassword.get)
    mongoPersonConnection += mongoObject
    person.copy(personId = Some(newId))
  }


  def authenticate(username:String,password:String) : Option[Person] = {
    val searchTerm = MongoDBObject("username" -> username)
    val fieldsNeeded = MongoDBObject("password" -> 1)
    mongoPersonConnection.findOne(searchTerm,fieldsNeeded) map { personObject =>
      personObject.getAs[String]("password").map{ existingPassword =>
        if(BCrypt.checkpw(password,existingPassword)){
          return findByUsername(username)
        } else {
          Logger.info("Password mismatch")
        }
      }.getOrElse(
        Logger.info("Password not found"))
    }
    Logger.info("Not found a mongo person")
    None
  }


  def findByUsername(username:String) : Option[Person] = {
    val searchTerm = MongoDBObject("username" -> username)
    val fieldsNeeded = MongoDBObject("fullname" -> 1,"email" -> 1)
    mongoPersonConnection.findOne(searchTerm,fieldsNeeded) map { personObject =>
      Logger.info("Found a mongo person!")
      new Person(
        personObject.getAs[ObjectId]("_id"),
        username,
        personObject.getAs[String]("fullname").getOrElse(""),
        personObject.getAs[String]("email")getOrElse("")
      )
    }
  }








}
