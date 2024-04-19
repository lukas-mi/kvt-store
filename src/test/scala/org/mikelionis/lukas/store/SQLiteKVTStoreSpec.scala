package org.mikelionis.lukas.store

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.flatspec.AnyFlatSpec

class SQLiteKVTStoreSpec extends AnyFlatSpec with KVTStoreSpecHelper with BeforeAndAfterEach with BeforeAndAfterAll {
  private val config = ConfigFactory.load()
  private val table = config.getString("jdbc.table")

  private val con = SQLiteKVTStore.getCon(config)
  private val store = new SQLiteKVTStore(con, table)

  override def beforeEach(): Unit = {
    super.beforeEach()

    SQLiteKVTStore.drop(con, table)
    SQLiteKVTStore.create(con, table)
  }

  override def afterAll(): Unit = {
    SQLiteKVTStore.drop(con, table)
    con.close()

    super.afterAll()
  }

  allTests.foreach(test => test(store))
}
