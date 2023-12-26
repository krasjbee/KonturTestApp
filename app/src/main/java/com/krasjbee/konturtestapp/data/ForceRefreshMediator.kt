package com.krasjbee.konturtestapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.krasjbee.konturtestapp.domain.Person

/*
Workaround which helps to
 */
@OptIn(ExperimentalPagingApi::class)
class ForceRefreshMediator(
    private val onRefreshCallback: suspend (pageSize: Int, page: Int) -> Result<List<Person>>,
    private val pageFetchCallback: suspend (pageSize: Int, page: Int) -> Result<List<Person>>,
) : RemoteMediator<Int, Person>() {

    override suspend fun initialize(): InitializeAction {

        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Person>): MediatorResult {
        Log.d("paging", "mediator\nload: loadType: $loadType\nstate: $state ")
        if (loadType == LoadType.REFRESH) onRefreshCallback(state.config.pageSize, 0)
        val anchor = state.anchorPosition
        if (loadType == LoadType.APPEND && anchor != null) {
            val key1 = state.closestPageToPosition(anchor)?.nextKey
            Log.d("paging", "load: $key1 ")
            if (key1 != null) {
                val result = pageFetchCallback(state.config.pageSize, key1)
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