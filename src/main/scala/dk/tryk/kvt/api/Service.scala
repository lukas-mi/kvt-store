package dk.tryk.kvt.api

import dk.tryk.kvt.store.KVTStore

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object Service {
  def makeRoute(store: KVTStore[String, String]): Route = {
    path("") {
      concat(
        get {
          entity(as[GetObj]) { o =>
            complete(store.get(o.key, o.timestamp))
          }
        },
        put {
          entity(as[PutObj]) { o =>
            store.put(o.key, o.timestamp, o.value)
            complete(None)
          }
        }
      )
    }
  }
}
