package org.mikelionis.lukas

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

package object api {
  final case class PutObj(key: String, value: String, timestamp: Timestamp)
  final case class GetObj(key: String, timestamp: Long)

  implicit val itemFormat: RootJsonFormat[PutObj] = jsonFormat3(PutObj.apply)
  implicit val orderFormat: RootJsonFormat[GetObj] = jsonFormat2(GetObj.apply)
}
