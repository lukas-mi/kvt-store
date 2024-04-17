package dk.tryk.kvt.store

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

trait KVTStoreSpecHelper extends should.Matchers { this: AnyFlatSpec =>
  val test1: KVTStore[String, String] => Unit = { store =>
    it should "allow for values to be stored and retrieved" in {
      // several values under the same key
      val key1 = "key1"
      val ts1 = 100
      val value1 = "value100"
      val ts12 = 200
      val value12 = "value200"
      store.put(key1, ts1, value1)
      store.put(key1, ts12, value12)

      // different key
      val key2 = "key2"
      val ts2 = 222
      val value2 = "value222"
      store.put(key2, ts2, value2)

      store.get(key1, ts1) shouldBe Some(value1)
      store.get(key1, ts12) shouldBe Some(value12)
      store.get(key2, ts2) shouldBe Some(value2)
    }
  }

  val test2: KVTStore[String, String] => Unit = { store =>
    it should "not retrieve values that correspond to non-existing key" in {
      val key1 = "key1"
      val ts1 = 100
      val value1 = "value100"
      store.put(key1, ts1, value1)

      store.get("keyOther", ts1) shouldBe None
    }
  }

  val test3: KVTStore[String, String] => Unit = { store =>
    it should "retrieve values at immediately preceding timestamps when the provided do not exist" in {
      val key1 = "key1"
      val ts1 = 100
      val value1 = "value100"
      store.put(key1, ts1, value1)

      val ts2 = 200
      val value2 = "value200"
      store.put(key1, ts2, value2)

      val ts3 = 300
      val value3 = "value300"
      store.put(key1, ts3, value3)

      store.get(key1, 101) shouldBe Some(value1)
      store.get(key1, 199) shouldBe Some(value1)
      store.get(key1, 201) shouldBe Some(value2)
      store.get(key1, 299) shouldBe Some(value2)
      store.get(key1, 301) shouldBe Some(value3)
    }
  }

  val test4: KVTStore[String, String] => Unit = { store =>
    it should "not retrieve anything for a key if there is no equal or preceding timestamp to the provided" in {
      val key1 = "key1"
      val ts1 = 100
      val value1 = "value100"
      store.put(key1, ts1, value1)

      store.get(key1, 99) shouldBe None
    }
  }

  val test5: KVTStore[String, String] => Unit = { store =>
    it should "update value under existing key-timestamp" in {
      val key1 = "key1"
      val ts1 = 100
      val value1 = "value100"
      store.put(key1, ts1, value1)

      val newValue1 = "value200"
      store.put(key1, ts1, newValue1)

      store.get(key1, ts1) shouldBe Some(newValue1)
    }
  }

  val allTests: List[KVTStore[String, String] => Unit] = List(
    test1,
    test2,
    test3,
    test4,
    test5
  )

}
