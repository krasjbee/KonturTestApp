package com.krasjbee.konturtestapp.ui.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.krasjbee.konturtestapp.domain.DataHolder

/**
 * Remote mediator used as workaround for force refresh
 * @param pageFetchCall call used to fetch next pages
 */
@OptIn(ExperimentalPagingApi::class)
class ForceRefreshMediator<T : Any>(
    private val pageFetchCall: suspend (force: Boolean, pageSize: Int, page: Int) -> DataHolder<List<T>>,
) : RemoteMediator<Int, T>() {
    // TODO: refactor
    override suspend fun initialize(): InitializeAction {
        //no need to do initial refresh. this logic is encapsulated in cache
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, T>
    ): MediatorResult {
        val isForce = loadType == LoadType.REFRESH
        val pageSize = state.config.pageSize
        return when (loadType) {
            LoadType.REFRESH -> refresh(isForce, pageSize, 0)
            LoadType.PREPEND -> prepend()
            LoadType.APPEND -> {
                val nextPageKey = state.anchorPosition?.let {
                    state.closestPageToPosition(it)?.nextKey
                }
                append(isForce, pageSize, nextPageKey)
            }
        }
    }

    private suspend fun refresh(isForce: Boolean, pageSize: Int, page: Int): MediatorResult {
        val result = pageFetchCall(isForce, pageSize, page)
        if (result.hasData()) {
            return MediatorResult.Success(result.getDataOrNull()!!.size == pageSize)
        }
        return MediatorResult.Success(true)
    }

    private fun prepend(): MediatorResult.Success {
        return MediatorResult.Success(true)
    }

    private suspend fun append(isForce: Boolean, pageSize: Int, page: Int?): MediatorResult {
        if (page != null) {
            val result = pageFetchCall(isForce, pageSize, page)
            val data = result.getDataOrNull()
            val isPaginationEndReached =
                (result.hasData() && data != null && data.size < pageSize) || data?.isEmpty() == true
            return MediatorResult.Success(isPaginationEndReached)
        }
        return MediatorResult.Success(true)
    }

}