package org.mikelionis.lukas.store

import org.mikelionis.lukas.Timestamp

trait KVTStore[K, V] {
  def put(key: K, ts: Timestamp, value: V): Unit

  def get(key: K, ts: Timestamp): Option[V]
}
