package com.krasjbee.konturtestapp.domain

sealed class DataHolder<T> {

    class NoData<T>(val error: Throwable?) : DataHolder<T>()
    class Data<T>(val data: T) : DataHolder<T>()
    class DataWithError<T>(val data: T, val error: Throwable?) : DataHolder<T>()

    inline fun onData(block: (T) -> Unit): DataHolder<T> {
        if (this is Data) block(data)
        return this
    }

    inline fun onNoData(block: (Throwable?) -> Unit): DataHolder<T> {
        if (this is NoData) block(error)
        return this
    }

    inline fun onDataWithError(block: (data: T, error: Throwable?) -> Unit): DataHolder<T> {
        if (this is DataWithError) block(data, error)
        return this
    }

    inline fun onHasData(block: (T) -> Unit): DataHolder<T> {
        when (this) {
            is Data -> block(data)
            is DataWithError -> block(data)
            is NoData -> Unit
        }
        return this
    }

    inline fun onHasError(block: (Throwable?) -> Unit): DataHolder<T> {
        when (this) {
            is Data -> Unit
            is DataWithError -> block(error)
            is NoData -> block(error)
        }
        return this
    }

    inline fun <R> transformData(transformer: (data: T) -> R): DataHolder<R> {
        return when (this) {
            is Data -> Data(transformer(data))
            is DataWithError -> DataWithError(transformer(data), error)
            is NoData -> NoData(error)
        }
    }

    fun hasData() = this is Data || this is DataWithError

    fun hasError() = this is NoData || this is DataWithError

    fun getDataOrNull(): T? {
        return when (this) {
            is Data -> data
            is DataWithError -> data
            is NoData -> null
        }
    }

    fun getThrowableOrNull(): Throwable? {
        return when (this) {
            is Data -> null
            is DataWithError -> error
            is NoData -> error
        }
    }

}