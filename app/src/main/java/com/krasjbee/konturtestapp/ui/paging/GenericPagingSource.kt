package com.krasjbee.konturtestapp.ui.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krasjbee.konturtestapp.domain.DataContainer
import javax.inject.Inject

class GenericPagingSource<T : Any> @Inject constructor(private val call: suspend (pageSize: Int, page: Int) -> DataContainer<List<T>>) :
    PagingSource<Int, T>() {
    // TODO: cleanup
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        Log.d("paging", "paging source :\n getRefreshKey: $state ")
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 0

        val result = call(params.loadSize, page)
        Log.d(
            "paging",
            "paging source : \n load: $page, params : load size ${params.loadSize} key ${params.key}"
        )
        return if (result.hasData()) {
            val data = result.getDataOrNull()!!
            Log.d("paging", "load: ")
            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.isEmpty() || data.size < params.loadSize) null else page + 1
            )
        } else {
            LoadResult.Error(result.getExceptionOrNull()!!)
        }
    }
}
