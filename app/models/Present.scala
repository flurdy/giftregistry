package models

import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import scala.Some
import scala.Some

case class Present(
      presentId: Option[ObjectId] = None,
      title: String,
      description: Option[String],
      from: String,
      event: Event
    ) {

    def this( title: String, description: Option[String], from: String, event: Event) = this(None,title,description,from,event)

    def save = Present.save(this)

}




object Present {

  val mongoPresentConnection = MongoConnection()("giftdb")("present")


  def save(present:Present) = {
    val newId = new ObjectId
    val mongoObject = MongoDBObject(
      "_id" -> newId,
      "title" -> present.title,
      "description" -> present.description.getOrElse(""),
      "from" -> present.from,
      "eventId" -> present.event.eventId )
    mongoPresentConnection += mongoObject
    present.copy(presentId = Some(newId))
  }

}


case class Event(
    eventId: Option[ObjectId],
    title: String
){
  def this(title:String) = this(None,title)
  def save = Event.save(this)
}

object Event {

  val mongoEventConnection = MongoConnection()("giftdb")("event")

  def findById(eventId:String) = {
    val searchTerm = MongoDBObject("eventId" -> eventId)
    val fieldsNeeded = MongoDBObject("title" -> 1)
    mongoEventConnection.findOne(searchTerm,fieldsNeeded) map { eventObject =>
      Event(
        eventObject.getAs[ObjectId]("_id"),
        eventObject.getAs[String]("title").getOrElse("")
      )
    }

  }


  def save(event:Event) = {
    val newId = new ObjectId
    val mongoObject = MongoDBObject(
      "_id" -> newId,
      "title" -> event.title)
    mongoEventConnection += mongoObject
    event.copy(eventId = Some(newId))
  }


  def findByPerson(sessionPerson:Person) : Seq[Event] = {
      Seq.empty
  }


}

