package org.mikelionis.lukas.store

import org.mikelionis.lukas.Timestamp

import com.typesafe.config.Config

import java.sql.{Connection, DriverManager}
import scala.util.Using

object SQLiteKVTStore {
  def getCon(config: Config): Connection = {
    DriverManager.getConnection(
      config.getString("jdbc.url"),
      config.getString("jdbc.user"),
      config.getString("jdbc.password")
    )
  }

  def create(con: Connection, table: String): Boolean = {
    Using.resource(con.createStatement()) { stmt =>
      stmt.execute(createQuery(table))
    }
  }

  def drop(con: Connection, table: String): Unit = {
    Using.resource(con.createStatement()) { stmt =>
      stmt.execute(dropQuery(table))
    }
  }

  def dropQuery(tableName: String): String =
    s"DROP TABLE IF EXISTS $tableName"

  def createQuery(tableName: String): String =
    f"""
       |CREATE TABLE IF NOT EXISTS $tableName (
       |    `key` VARCHAR(255) NOT NULL,
       |    `value` VARCHAR(255) NOT NULL,
       |    `timestamp` TIMESTAMP NOT NULL,
       |    PRIMARY KEY (`key`, `timestamp`)
       |);
       |""".stripMargin

  def upsertQuery(table: String): String =
    f"""
       |INSERT INTO $table(`key`, `value`, `timestamp`)
       |  VALUES(?, ?, ?)
       |  ON CONFLICT(`key`, `timestamp`) DO UPDATE SET
       |    `value`=excluded.`value`;
       |""".stripMargin

  def selectQuery(table: String): String =
    f"""SELECT value
       |FROM $table
       |WHERE key = ?
       |  AND timestamp <= ?
       |ORDER BY timestamp DESC
       |LIMIT 1
       |""".stripMargin

}

class SQLiteKVTStore(con: Connection, table: String) extends KVTStore[String, String] {
  import SQLiteKVTStore._

  override def put(key: String, ts: Timestamp, value: String): Unit = {
    Using.resource(con.prepareStatement(upsertQuery(table))) { prepStmt =>
      prepStmt.setString(1, key)
      prepStmt.setString(2, value)
      prepStmt.setLong(3, ts)

      prepStmt.executeUpdate()
    }
  }

  override def get(key: String, ts: Timestamp): Option[String] = {
    Using.resource(con.prepareStatement(selectQuery(table))) { prepStmt =>
      prepStmt.setString(1, key)
      prepStmt.setLong(2, ts)

      Using.resource(prepStmt.executeQuery()) { rs =>
        if (rs.next()) Some(rs.getString("value")) else None
      }
    }
  }
}
