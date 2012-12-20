package models

// import play.api.Play.current
import org.mindrot.jbcrypt.BCrypt
import play.api._
import play.api.Play.current
import com.mongodb.casbah.MongoURI
import com.mongodb.casbah.Imports._


case class Person(
	personId:Option[String],
	username: String,
	fullname: String,
	email: String
){

	private var passwordOption: Option[String] = None

  def this(personId:String) = this(Some(personId),"","","")

  def this(username:String,fullname:String,email:String) = this(None,username,fullname,email)

  def this(username:String,fullname:String,email:String,password:String) = {
    this(None,username,fullname,email)
    passwordOption = Some(password)
  }

  def getEncryptedPassword = {
    passwordOption match {
      case Some(password) => Person.encrypt(password)
      case None => throw new NullPointerException("No password has been set")
    }
  }

	def save = Person.save(this)

}

object Person  {

  def encrypt(password: String) = Some(BCrypt.hashpw(password,BCrypt.gensalt()))

  def passwordMatch(enteredPassword:String,existingPassword:String) = BCrypt.checkpw(enteredPassword,existingPassword)

  val collection = MongoRepository.getMongoCollection("person")


  def save(person:Person) = {
    val newId = new ObjectId
    val mongoObject = MongoDBObject(
          "_id" -> newId,
          "username" -> person.username,
          "fullname" -> person.fullname,
          "email" -> person.email,
          "password" -> person.getEncryptedPassword)
    collection += mongoObject
    person.copy(personId = Some(newId.toString))
  }


  def authenticate(username:String,password:String) : Option[Person] = {
    val searchTerm = MongoDBObject("username" -> username)
    val fieldsNeeded = MongoDBObject("password" -> 1)
    collection.findOne(searchTerm,fieldsNeeded) map { personObject =>
      personObject.getAs[String]("password").map{ existingPassword =>
        if(passwordMatch(password,existingPassword)){
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
    collection.findOne(searchTerm,fieldsNeeded) map { personObject =>
      new Person(
        Some(personObject.getAs[ObjectId]("_id").get.toString),
        username,
        personObject.getAs[String]("fullname").getOrElse(""),
        personObject.getAs[String]("email")getOrElse("")
      )
    }
  }




}
