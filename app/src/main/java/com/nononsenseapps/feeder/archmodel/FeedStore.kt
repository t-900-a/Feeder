package com.nononsenseapps.feeder.archmodel

import com.nononsenseapps.feeder.db.room.Feed
import com.nononsenseapps.feeder.db.room.FeedDao
import com.nononsenseapps.feeder.db.room.FeedTitle
import com.nononsenseapps.feeder.db.room.ID_UNSET
import com.nononsenseapps.feeder.db.room.upsertFeed
import com.nononsenseapps.feeder.model.FeedUnreadCount
import com.nononsenseapps.feeder.ui.compose.navdrawer.DrawerFeed
import com.nononsenseapps.feeder.ui.compose.navdrawer.DrawerItemWithUnreadCount
import com.nononsenseapps.feeder.ui.compose.navdrawer.DrawerTag
import com.nononsenseapps.feeder.ui.compose.navdrawer.DrawerTop
import java.net.URL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.threeten.bp.Instant

class FeedStore(override val di: DI) : DIAware {
    private val feedDao: FeedDao by instance()

    // Need only be internally consistent within the composition
    // this object outlives all compositions
    private var nextTagUiId: Long = -1000

    // But IDs need to be consistent even if tags come and go
    private val tagUiIds = mutableMapOf<String, Long>()

    private fun getTagUiId(tag: String): Long {
        return tagUiIds.getOrPut(tag) {
            --nextTagUiId
        }
    }

    suspend fun getFeed(feedId: Long): Feed? = feedDao.loadFeed(feedId)

    suspend fun getFeed(url: URL): Feed? = feedDao.loadFeedWithUrl(url)

    suspend fun saveFeed(feed: Feed): Long {
        return if (feed.id > ID_UNSET) {
            feedDao.updateFeed(feed)
            feed.id
        } else {
            feedDao.insertFeed(feed)
        }
    }

    suspend fun getDisplayTitle(feedId: Long): String? =
        feedDao.getFeedTitle(feedId)?.displayTitle

    suspend fun deleteFeeds(feedIds: List<Long>) {
        feedDao.deleteFeeds(feedIds)
    }

    val allTags: Flow<List<String>> = feedDao.loadAllTags()

    @OptIn(ExperimentalCoroutinesApi::class)
    val drawerItemsWithUnreadCounts: Flow<List<DrawerItemWithUnreadCount>> =
        feedDao.loadFlowOfFeedsWithUnreadCounts()
            .mapLatest { feeds ->
                mapFeedsToSortedDrawerItems(feeds)
            }

    private fun mapFeedsToSortedDrawerItems(
        feeds: List<FeedUnreadCount>,
    ): List<DrawerItemWithUnreadCount> {
        var topTag = DrawerTop(unreadCount = 0, syncingChildren = 0, totalChildren = 0)
        val tags: MutableMap<String, DrawerTag> = mutableMapOf()
        val data: MutableList<DrawerItemWithUnreadCount> = mutableListOf()

        for (feedDbo in feeds) {
            val feed = DrawerFeed(
                unreadCount = feedDbo.unreadCount,
                tag = feedDbo.tag,
                id = feedDbo.id,
                displayTitle = feedDbo.displayTitle,
                currentlySyncing = feedDbo.currentlySyncing,
            )

            data.add(feed)
            topTag = topTag.copy(
                unreadCount = topTag.unreadCount + feed.unreadCount,
                totalChildren = topTag.totalChildren + 1,
                syncingChildren = if (feedDbo.currentlySyncing) {
                    topTag.syncingChildren + 1
                } else {
                    topTag.syncingChildren
                }
            )

            if (feed.tag.isNotEmpty()) {
                val tag = tags[feed.tag] ?: DrawerTag(
                    tag = feed.tag,
                    unreadCount = 0,
                    uiId = getTagUiId(feed.tag),
                    syncingChildren = 0,
                    totalChildren = 0,
                )
                tags[feed.tag] = tag.copy(
                    unreadCount = tag.unreadCount + feed.unreadCount,
                    totalChildren = tag.totalChildren + 1,
                    syncingChildren = if (feedDbo.currentlySyncing) {
                        tag.syncingChildren + 1
                    } else {
                        tag.syncingChildren
                    },
                )
            }
        }

        data.add(topTag)
        data.addAll(tags.values)

        return data.sorted()
    }

    fun getFeedTitles(feedId: Long, tag: String): Flow<List<FeedTitle>> =
        when {
            feedId > ID_UNSET -> feedDao.getFeedTitlesWithId(feedId)
            tag.isNotBlank() -> feedDao.getFeedTitlesWithTag(tag)
            else -> feedDao.getAllFeedTitles()
        }

    fun getCurrentlySyncingLatestTimestamp(): Flow<Instant?> =
        feedDao.getCurrentlySyncingLatestTimestamp()

    suspend fun setCurrentlySyncingOn(feedId: Long, syncing: Boolean, lastSync: Instant? = null) {
        if (lastSync != null) {
            feedDao.setCurrentlySyncingOn(feedId = feedId, syncing = syncing, lastSync = lastSync)
        } else {
            feedDao.setCurrentlySyncingOn(feedId = feedId, syncing = syncing)
        }
    }

    suspend fun upsertFeed(feedSql: Feed) =
        feedDao.upsertFeed(feed = feedSql)

    suspend fun getFeedsOrderedByUrl(): List<Feed> {
        return feedDao.getFeedsOrderedByUrl()
    }

    fun getFlowOfFeedsOrderedByUrl(): Flow<List<Feed>> {
        return feedDao.getFlowOfFeedsOrderedByUrl()
    }

    suspend fun deleteFeed(url: URL) {
        feedDao.deleteFeedWithUrl(url)
    }
}
