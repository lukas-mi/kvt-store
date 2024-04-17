package dk.tryk.kvt.store

import dk.tryk.kvt.Timestamp

import java.util.concurrent.ConcurrentHashMap
import scala.collection.{concurrent, mutable}
import scala.jdk.CollectionConverters._

class InMemoryKVTStore[K, V] extends KVTStore[K, V] {
  private val store: concurrent.Map[K, mutable.TreeMap[Timestamp, V]] =
    new ConcurrentHashMap[K, mutable.TreeMap[Timestamp, V]]().asScala

  def put(key: K, ts: Timestamp, value: V): Unit = {
    store.updateWith(key) {
      case Some(tree) =>
        tree.addOne(ts, value)
        Some(tree)
      case None =>
        Some(mutable.TreeMap[Timestamp, V]((ts, value)))
    }
  }
  def get(key: K, ts: Timestamp): Option[V] = {
    for {
      tree <- store.get(key)
      (_, value) <- tree.maxBefore(ts + 1)
    } yield value
  }

}
