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
        mongoUri.username.map { username =>
          mongoUri.password.map { password =>
            database.underlying.authenticate(username,password)
          }
        }
        database.apply(collectionName)
      }
    }
  }


}
