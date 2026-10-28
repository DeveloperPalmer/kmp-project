package com.kmpproject.core.data

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.kmpproject.core.data.di.InMemoryDb
import com.kmpproject.core.data.storage.IN_MEMORY_DB_NAME
import com.kmpproject.core.data.storage.merge
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface IosDataModule {
  // Even if the driver is created with in-memory db configuration, name is still required:
  // without one cache won't be shared between db connections, i.e. db will be created anew for every connection
  @Provides
  @InMemoryDb
  @SingleIn(AppScope::class)
  fun provideInMemorySqlDriver(schema: Set<SqlSchema<QueryResult.Value<Unit>>>): SqlDriver {
    return NativeSqliteDriver(
      schema = merge(schema),
      name = IN_MEMORY_DB_NAME,
      onConfiguration = { configuration -> configuration.copy(inMemory = true) },
    )
  }
}
