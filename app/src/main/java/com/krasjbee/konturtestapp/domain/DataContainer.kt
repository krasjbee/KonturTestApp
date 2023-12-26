package com.krasjbee.konturtestapp.domain

sealed class DataContainer<T> {

    class NoData<T>(val error: Exception) : DataContainer<T>()
    class Data<T>(val data: T) : DataContainer<T>()
    class DataWithError<T>(val data: T, val error: Exception) : DataContainer<T>()

    inline fun onHasData(block: (T) -> Unit): DataContainer<T> {
        when (this) {
            is Data -> block(data)
            is DataWithError -> block(data)
            is NoData -> Unit
        }
        return this
    }

    inline fun onHasError(block: (Exception) -> Unit): DataContainer<T> {
        when (this) {
            is Data -> Unit
            is DataWithError -> block(error)
            is NoData -> block(error)
        }
        return this
    }

    inline fun <R> transformData(transformer: (data: T) -> R): DataContainer<R> {
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

    fun getExceptionOrNull(): Exception? {
        return when (this) {
            is Data -> null
            is DataWithError -> error
            is NoData -> error
        }
    }

}