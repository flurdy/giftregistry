package models

import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import scala.Some

case class Present(
      presentId: Option[ObjectId] = None,
      title: String,
      description: Option[String],
      from: String
    ) {

    def this( title: String, description: Option[String], from: String) = this(None,title,description,from)

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
      "from" -> present.from)
    mongoPresentConnection += mongoObject
    present.copy(presentId = Some(newId))
  }


}