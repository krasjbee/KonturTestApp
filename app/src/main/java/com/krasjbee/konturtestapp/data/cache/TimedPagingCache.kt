package com.krasjbee.konturtestapp.data.cache

import android.content.Context
import androidx.core.content.edit
import com.krasjbee.konturtestapp.datasource.database.PersonDao
import com.krasjbee.konturtestapp.datasource.database.PersonLocal
import com.krasjbee.konturtestapp.datasource.database.mapToLocal
import com.krasjbee.konturtestapp.datasource.database.mapToPerson
import com.krasjbee.konturtestapp.domain.Person
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

private const val CACHE_EXPIRE_INTERVAL = 60 * 1000
private const val LAST_FETCH_KEY = "LAST_FETCH_KEY"
private const val PREFERENCES_NAME = "LAST_FETCH_PREFS"

class TimedPagingCache @Inject constructor(
    private val dao: PersonDao,
    private val fetchTimeProvider: PagingCache.FetchTimeProvider
) : PagingCache<Person> {

    private val mutex = Mutex()

    override val isExpired: Boolean
        get() = System.currentTimeMillis() - fetchTimeProvider.lastFetchTime > CACHE_EXPIRE_INTERVAL

    override suspend fun getItem(key: Any): Person {
        return mutex.withLock {
            dao.getPersonInfo(key as String).mapToPerson()
        }
    }

    override val isEmpty: Boolean
        get() = _isCacheEmpty

    private var _isCacheEmpty: Boolean = true

    override suspend fun clear() {
        mutex.withLock {
            dao.clear()
            _isCacheEmpty = true
        }
    }

    override suspend fun addAll(collection: List<Person>) {
        mutex.withLock {
            dao.insertAll(collection.map(Person::mapToLocal))
            _isCacheEmpty = false
        }
    }

    override suspend fun getPage(pageSize: Int, page: Int): List<Person> {
        return mutex.withLock { dao.getPersonList(pageSize, page).map(PersonLocal::mapToPerson) }
    }
}


class SharedPreferencesFetchTimeProvider @Inject constructor(@ApplicationContext context: Context) :
    PagingCache.FetchTimeProvider {

    private val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    override var lastFetchTime: Long
        get() {
            return prefs.getLong(LAST_FETCH_KEY, -1L)
        }
        set(value) {
            prefs.edit {
                putLong(LAST_FETCH_KEY, value)
            }
        }

}
