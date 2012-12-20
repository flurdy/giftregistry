package models

import com.mongodb.casbah._
import play.api._
import play.api.Play.current

object MongoRepository {

  val mongoUri = MongoURI(Play.configuration.getString("mongodb.uri").get)

  def getMongoCollection(collectionName:String) : MongoCollection = {
    mongoUri.connectDB match {
      case Left(thrown) => throw thrown
      case Right(database) => {
        if (mongoUri.username != null && mongoUri.password != null) {
          database.underlying.authenticate(mongoUri.username.get,mongoUri.password.get)
        }
        database.apply(collectionName)
      }
    }
  }


}
