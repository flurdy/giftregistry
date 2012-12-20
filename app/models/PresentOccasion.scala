package models

import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import play.api._
import play.api.Play.current
import scala.Some
import com.mongodb.casbah.MongoURI

case class Present(
      presentId: Option[String] = None,
      title: String,
      description: Option[String],
      from: String,
      occasion: Occasion
    ) {

    def this( title: String, description: Option[String], from: String, occasion: Occasion) = this(None,title,description,from,occasion)

    def save = Present.save(this)

}




object Present {

  val collection =  MongoRepository.getMongoCollection("present")

  def save(present:Present) = {
    val newId = new ObjectId
    val mongoObject = MongoDBObject(
      "_id" -> newId,
      "title" -> present.title,
      "description" -> present.description.getOrElse(""),
      "from" -> present.from,
      "occasionId" -> present.occasion.occasionId )
      // "personId" -> new ObjectId(personId) )
    collection += mongoObject
    present.copy(presentId = Some(newId.toString))
  }

}




case class Occasion(
    occasionId: Option[String],
    title: String,
    person : Person
){
  def this(title:String,person:Person) = this(None,title,person)
  def save = Occasion.save(this)
}



object Occasion {

  val collection = MongoRepository.getMongoCollection("occasion")

  def findById(occasionId:String) = {
    val searchTerm = MongoDBObject("occasionId" -> new ObjectId(occasionId))
    val fieldsNeeded = MongoDBObject("title" -> 1)
    collection.findOne(searchTerm,fieldsNeeded) map { occasionObject =>
      Occasion(
        Some(occasionObject.getAs[ObjectId]("_id").get.toString),
        occasionObject.getAs[String]("title").getOrElse(""),
        new Person(occasionObject.getAs[ObjectId]("personId").toString)
      )
    }

  }


  def save(occasion:Occasion) = {
    occasion.person.personId match {
      case Some(personId) => {

        val newId = new ObjectId
        val mongoObject = MongoDBObject(
          "_id" -> newId,
          "title" -> occasion.title,
          "personId" -> new ObjectId(personId))
        collection += mongoObject
        occasion.copy(occasionId = Some(newId.toString))

      }
      case None => throw new NullPointerException("Person id was null")
    }
  }


  def findByPerson(person:Person) : Seq[Occasion] = {
    person.personId match {
      case Some(personId) => {
        val searchTerm = MongoDBObject("personId" -> new ObjectId(personId))
        val occasions = collection.find(searchTerm) map { occasionObject =>
         Occasion(
            Some(occasionObject.getAs[ObjectId]("_id").get.toString),
            occasionObject.getAs[String]("title").getOrElse(""),
            new Person(occasionObject.getAs[ObjectId]("personId").get.toString)
         )
        }
        occasions.toSeq
      }
      case None => throw new NullPointerException("Person id was null")
    }
  }



}

