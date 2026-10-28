package com.kmpproject.core.data.storage

import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

// Would be nicer to have a better "merging" of feature-module databases.
// See https://github.com/cashapp/sqldelight/issues/1455
internal fun merge(schemes: Set<SqlSchema<QueryResult.Value<Unit>>>): SqlSchema<QueryResult.Value<Unit>> =
  object : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long = schemes
      .map { it.version }
      .reduce { first, second ->
        if (first != second) {
          error("All schemes versions must be the same. first = $first, second: $second")
        }
        second
      }

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      schemes.forEach { it.create(driver) }
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> {
      schemes.forEach { it.migrate(driver, oldVersion, newVersion, *callbacks) }
      return QueryResult.Unit
    }
  }

internal const val IN_MEMORY_DB_NAME = "memory.db"
