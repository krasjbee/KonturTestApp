package com.krasjbee.konturtestapp.util

import kotlin.coroutines.cancellation.CancellationException


suspend inline fun <T> suspendRunCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (e: Exception) {
        Result.failure(e)
    }
}