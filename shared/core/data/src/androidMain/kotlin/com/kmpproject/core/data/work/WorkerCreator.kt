package com.kmpproject.core.data.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

typealias WorkerCreator = (Context, WorkerParameters) -> ListenableWorker
