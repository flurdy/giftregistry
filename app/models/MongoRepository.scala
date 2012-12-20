package models

import com.mongodb.casbah._
import play.api.Play
import play.api.Play.current

object MongoRepository {

  val mongoUri = MongoURI(Play.configuration.getString("mongodb.uri").get)

  def getMongoCollection(collectionName:String) : MongoCollection = mongoUri.connectDB fold ( throw _, _.apply(collectionName) )


}
