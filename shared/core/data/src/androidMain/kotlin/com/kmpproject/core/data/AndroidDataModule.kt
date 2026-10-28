package com.kmpproject.core.data

import android.content.Context
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.kmpproject.core.data.di.InMemoryDb
import com.kmpproject.core.data.storage.merge
import com.kmpproject.core.domain.ApplicationContext
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface AndroidDataModule {
  @Provides
  @InMemoryDb
  @SingleIn(AppScope::class)
  fun provideInMemorySqlDriver(
    @ApplicationContext
    context: Context,
    schema: Set<SqlSchema<QueryResult.Value<Unit>>>,
  ): SqlDriver {
    return AndroidSqliteDriver(
      schema = merge(schema),
      context = context,
      name = null,
    )
  }
}
