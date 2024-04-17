import dk.tryk.kvt.api._
import dk.tryk.kvt.store.SQLiteKVTStore

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "kvt-store")
    implicit val exContext: ExecutionContext = system.executionContext

    val config = ConfigFactory.load()

    val table = config.getString("jdbc.table")
    val con = SQLiteKVTStore.getCon(config)
    val store = new SQLiteKVTStore(con, table)
    SQLiteKVTStore.create(con, table)

    val host = config.getString("api.host")
    val port = config.getInt("api.port")
    val bindingF = Http().newServerAt(host, port).bind(Service.makeRoute(store))

    println(s"KVT-store service started at $host:$port. \nPress any key to stop...")
    StdIn.readLine()

    bindingF
      .flatMap(_.unbind())
      .onComplete(_ => {
        println("Shutting down KVT-store service...")
        system.terminate()
        con.close()
      })
  }
}