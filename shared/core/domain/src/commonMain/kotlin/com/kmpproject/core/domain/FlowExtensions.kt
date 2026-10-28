package com.kmpproject.core.domain

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.kmpproject.core.domain.entity.LceState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import ru.kode.remo.JobFlow
import ru.kode.remo.JobState
import ru.kode.remo.StartScheduled
import ru.kode.remo.Task3

fun <T, S> Flow<T>.mapDistinctChanges(transform: suspend (T) -> S): Flow<S> {
  return this.map(transform).distinctUntilChanged()
}

fun <T, S : Any> Flow<T>.mapDistinctNotNullChanges(transform: suspend (T) -> S?): Flow<S> {
  return this.mapNotNull(transform).distinctUntilChanged()
}

fun <T> JobFlow<T>.asLceState(): Flow<LceState<Unit>> {
  return channelFlow {
    launch {
      this@asLceState.results(replayLast = false).collect {
        when (it) {
          is Ok -> send(LceState.Content(Unit))
          is Err -> send(LceState.Error(it.error))
        }
      }
    }
    launch {
      this@asLceState.state.collect {
        when (it) {
          JobState.Idle -> Unit // relying on results() emissions to report idle
          JobState.Running -> send(LceState.Loading)
        }
      }
    }
  }
}

// An instant task can finish before asLceState() subscribes and its result would be lost,
// so the task waits for both results and state subscribers
fun <P1, P2, P3, R> Task3<P1, P2, P3, R>.startOnSubscribe(
  argument1: P1,
  argument2: P2,
  argument3: P3,
): Job {
  return start(
    argument1 = argument1,
    argument2 = argument2,
    argument3 = argument3,
    scheduled = StartScheduled.Lazily(
      minResultsSubscribers = 1,
      minStateSubscribers = 1
    )
  )
}
