package com.kmpproject.core.data.di

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.kmpproject.core.data.cities.CitiesDatabase
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface DatabaseModule {
  @Provides
  @SingleIn(AppScope::class)
  fun provideCitiesDatabase(
    @InMemoryDb sqlDriver: SqlDriver
  ): CitiesDatabase {
    return CitiesDatabase(driver = sqlDriver)
  }

  @Provides
  @SingleIn(AppScope::class)
  @IntoSet
  fun provideCitiesDatabaseSchema(): SqlSchema<QueryResult.Value<Unit>> {
    return CitiesDatabase.Schema
  }
}
