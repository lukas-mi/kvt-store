# kvt-store
Key Value Timestamp store POC

Prerequisites:
- [sbt](https://www.scala-sbt.org/) for building Scala code

Run application on `localhost:8080`:
- `sbt run`

Run tests:
- `sbt test`

Implementations:
- `InMemoryKVTStore` a in memory store based on a concurrent map where each key is associated with an ordered map structure with timestamp as the key (implemented as red-black tree)
  - `SQLiteKVTStore` a persistent store backed by SQLite embedded DB. Queries under the hood:
  - Table definition:
    ```sqlite
      CREATE TABLE IF NOT EXISTS kvt_store (
          `key` VARCHAR(255) NOT NULL,
          `value` VARCHAR(255) NOT NULL,
          `timestamp` TIMESTAMP NOT NULL,
          PRIMARY KEY (`key`, `timestamp`)
      );
    ```
  - For `put` operations:
    ```sqlite
      INSERT INTO kvt_store (`key`, `value`, `timestamp`)
          VALUES(?, ?, ?)
          ON CONFLICT(`key`, `timestamp`) DO UPDATE SET
              `value`=excluded.`value`;
    ```
  - For `get` operations:
    ```sqlite
      SELECT value
      FROM kvt_store
      WHERE key = ?
          AND timestamp <= ?
      ORDER BY timestamp DESC
      LIMIT 1
    ```

TODO:
  - Add tests for API
  - Alternative implementation for a persistent store
