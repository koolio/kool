package test.kool.mongodb

import com.mongodb.Mongo

import org.junit.After as after
/**
*/
public abstract class MongoTestSupport {
    val mongo = Mongo("127.0.0.1")
    val testDbName = "koolioIntTests"
    val db = mongo.getDB(testDbName)!!

    after fun close() {
        mongo.close()
        println("closed $mongo")
    }

}