package models

import com.mongodb.casbah._
import play.api.Play
import play.api.Play.current
import akka.event.slf4j.Logger

object MongoRepository {

  val mongoUri = MongoURI(Play.configuration.getString("mongodb.uri").get)

  def getMongoCollection(collectionName:String) : MongoCollection = {
    Logger.info("uri %s".format(Play.configuration.getString("mongodb.uri").get))
    mongoUri.connectDB fold ( throw _, _.apply(collectionName) )
  }


}
