package com.krasjbee.konturtestapp.ui.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator

/**
 * Remote mediator used as workaround for force refresh
 * @param onRefreshCall call used to initiate force refresh
 * @param pageFetchCall call used to fetch next pages
 */
@OptIn(ExperimentalPagingApi::class)
class ForceRefreshMediator<T : Any>(
    private val onRefreshCall: suspend (pageSize: Int, page: Int) -> Result<List<T>>,
    private val pageFetchCall: suspend (pageSize: Int, page: Int) -> Result<List<T>>,
) : RemoteMediator<Int, T>() {

    override suspend fun initialize(): InitializeAction {
        //no need to do initial refresh. this logic is encapsulated in cache
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, T>): MediatorResult {
        Log.d("paging", "mediator\nload: loadType: $loadType\nstate: $state ")
        if (loadType == LoadType.REFRESH) onRefreshCall(state.config.pageSize, 0)
        val anchor = state.anchorPosition
        if (loadType == LoadType.APPEND && anchor != null) {
            val key1 = state.closestPageToPosition(anchor)?.nextKey
            Log.d("paging", "load: $key1 ")
            if (key1 != null) {
                val result = pageFetchCall(state.config.pageSize, key1)
                val data = result.getOrNull()
                if ((result.isSuccess && data != null && data.size < state.config.pageSize) || data?.isEmpty() == true) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
            }
        }
        if (loadType == LoadType.PREPEND) return MediatorResult.Success(true)
        return MediatorResult.Success(false)
    }
}