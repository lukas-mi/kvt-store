package dk.tryk.kvt.store

import dk.tryk.kvt.Timestamp

trait KVTStore[K, V] {
  def put(key: K, ts: Timestamp, value: V): Unit

  def get(key: K, ts: Timestamp): Option[V]
}
