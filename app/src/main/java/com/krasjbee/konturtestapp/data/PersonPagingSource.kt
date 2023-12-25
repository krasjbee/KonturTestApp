package com.krasjbee.konturtestapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krasjbee.konturtestapp.domain.Person
import com.krasjbee.konturtestapp.domain.PersonRepository
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class PersonPagingSource<T : Any> @Inject constructor(private val call: suspend (pageSize: Int, page: Int) -> Result<List<T>>) :
    PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 0
        val result = call(params.loadSize, page)
        return if (result.isSuccess) {
            val data = result.getOrNull()!!
            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } else {
            LoadResult.Error(result.exceptionOrNull()!!)
        }
    }
}