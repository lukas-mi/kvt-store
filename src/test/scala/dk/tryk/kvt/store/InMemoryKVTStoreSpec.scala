package dk.tryk.kvt.store

import org.scalatest.flatspec.AnyFlatSpec

class InMemoryKVTStoreSpec extends AnyFlatSpec with KVTStoreSpecHelper {
  private val store = new InMemoryKVTStore[String, String]()

  allTests.foreach(test => test(store))
}
