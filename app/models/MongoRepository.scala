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
        Logger.info("Is auth %b".format( database.isAuthenticated ) )
        database.authenticate(mongoUri.username.get,mongoUri.password.get.toString)
        Logger.info("Is auth %b".format( database.isAuthenticated ) )
        database.apply(collectionName)
      }
    }
  }


}
