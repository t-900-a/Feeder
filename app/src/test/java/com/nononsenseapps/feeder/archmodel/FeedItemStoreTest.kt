package com.nononsenseapps.feeder.archmodel

import com.nononsenseapps.feeder.db.room.FeedItemDao
import com.nononsenseapps.feeder.db.room.FeedItemIdWithLink
import com.nononsenseapps.feeder.db.room.FeedItemWithFeed
import com.nononsenseapps.feeder.db.room.ID_UNSET
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

class FeedItemStoreTest : DIAware {
    private val store: FeedItemStore by instance()

    @MockK
    private lateinit var dao: FeedItemDao

    override val di by DI.lazy {
        bind<FeedItemDao>() with instance(dao)
        bind<FeedItemStore>() with singleton { FeedItemStore(di) }
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun markAsNotified() {
        runBlocking {
            store.markAsNotified(listOf(1L, 2L))
        }

        coVerify {
            dao.markAsNotified(listOf(1L, 2L), true)
        }
    }

    @Test
    fun markAsReadAndNotified() {
        runBlocking {
            store.markAsReadAndNotified(5L)
        }

        coVerify {
            dao.markAsReadAndNotified(5L)
        }
    }

    @Test
    fun markAsUnread() {
        runBlocking {
            store.markAsUnread(5L, true)
            store.markAsUnread(6L, false)
        }

        coVerify {
            dao.markAsRead(5L, true)
            dao.markAsRead(6L, false)
        }
    }

    @Test
    fun getFullTextByDefault() {
        coEvery { dao.getFullTextByDefault(5L) } returns true

        assertTrue {
            runBlocking {
                store.getFullTextByDefault(5L)
            }
        }
    }

    @Test
    fun getFeedItem() {
        coEvery { dao.loadFeedItemFlow(5L) } returns flowOf(FeedItemWithFeed(id = 5L))

        val feedItem = runBlocking {
            store.getFeedItem(5L).toList().first()
        }

        assertEquals(5L, feedItem?.id)
    }

    @Test
    fun getLink() {
        coEvery { dao.getLink(5L) } returns "foo"

        val link = runBlocking {
            store.getLink(5L)
        }

        assertEquals("foo", link)
    }

    @Test
    fun getArticleOpener() {
        coEvery { dao.getOpenArticleWith(5L) } returns "foo"

        val result = runBlocking {
            store.getArticleOpener(5L)
        }

        assertEquals("foo", result)
    }

    @Test
    fun markAllAsReadInFeed() {
        runBlocking {
            store.markAllAsReadInFeed(5L)
        }

        coVerify {
            dao.markAllAsRead(5L)
        }
    }

    @Test
    fun markAllAsReadInTag() {
        runBlocking {
            store.markAllAsReadInTag("sfz")
        }

        coVerify {
            dao.markAllAsRead("sfz")
        }
    }

    @Test
    fun markAllAsRead() {
        runBlocking {
            store.markAllAsRead()
        }

        coVerify {
            dao.markAllAsRead()
        }
    }

    /*
    Commented out because PagingSource is not easily mocked :(
     */

//    @Test
//    fun feedPagedUnreadNewest() {
//        runBlocking {
//            store.getPagedFeedItems(1L, "foo", onlyUnread = true, newestFirst = true)
//                .toList()
//        }
//
//        verify {
//            dao.pagingUnreadPreviewsDesc(feedId = 1L)
//        }
//    }
//
//    @Test
//    fun feedPagedUnreadOldest() {
//        runBlocking {
//            store.getPagedFeedItems(1L, "foo", onlyUnread = true, newestFirst = false)
//                .toList()
//        }
//
//        verify {
//            dao.pagingUnreadPreviewsAsc(feedId = 1L)
//        }
//    }
//
//    @Test
//    fun feedPagedReadOldest() {
//        runBlocking {
//            store.getPagedFeedItems(1L, "foo", onlyUnread = false, newestFirst = false)
//                .toList()
//        }
//
//        verify {
//            dao.pagingPreviewsAsc(feedId = 1L)
//        }
//    }
//
//    @Test
//    fun feedPagedReadNewest() {
//        runBlocking {
//            store.getPagedFeedItems(1L, "foo", onlyUnread = false, newestFirst = true)
//                .toList()
//        }
//
//        verify {
//            dao.pagingPreviewsDesc(feedId = 1L)
//        }
//    }
//
//    @Test
//    fun tagPagedUnreadNewest() {
//        runBlocking {
//            store.getPagedFeedItems(ID_UNSET, "foo", onlyUnread = true, newestFirst = true)
//                .toList()
//        }
//
//        verify {
//            dao.pagingUnreadPreviewsDesc(tag = "foo")
//        }
//    }
//
//    @Test
//    fun tagPagedUnreadOldest() {
//        runBlocking {
//            store.getPagedFeedItems(ID_UNSET, "foo", onlyUnread = true, newestFirst = false)
//                .toList()
//        }
//
//        verify {
//            dao.pagingUnreadPreviewsAsc(tag = "foo")
//        }
//    }
//
//    @Test
//    fun tagPagedReadOldest() {
//        runBlocking {
//            store.getPagedFeedItems(ID_UNSET, "foo", onlyUnread = false, newestFirst = false)
//                .toList()
//        }
//
//        verify {
//            dao.pagingPreviewsAsc(tag = "foo")
//        }
//    }
//
//    @Test
//    fun tagPagedReadNewest() {
//        runBlocking {
//            store.getPagedFeedItems(ID_UNSET, "foo", onlyUnread = false, newestFirst = true)
//                .toList()
//        }
//
//        verify {
//            dao.pagingPreviewsDesc(tag = "foo")
//        }
//    }
//
//    @Test
//    fun allPagedUnreadNewest() {
//        runBlocking {
//            store.getPagedFeedItems(ID_UNSET, "", onlyUnread = true, newestFirst = true)
//                .toList()
//        }
//
//        verify {
//            dao.pagingUnreadPreviewsDesc()
//        }
//    }
//
//    @Test
//    fun allPagedUnreadOldest() {
//        runBlocking {
//            store.getPagedFeedItems(ID_UNSET, "", onlyUnread = true, newestFirst = false)
//                .toList()
//        }
//
//        verify {
//            dao.pagingUnreadPreviewsAsc()
//        }
//    }
//
//    @Test
//    fun allPagedReadOldest() {
//        runBlocking {
//            store.getPagedFeedItems(ID_UNSET, "", onlyUnread = false, newestFirst = false)
//                .toList()
//        }
//
//        verify {
//            dao.pagingPreviewsAsc()
//        }
//    }
//
//    @Test
//    fun allPagedReadNewest() {
//        runBlocking {
//            store.getPagedFeedItems(ID_UNSET, "", onlyUnread = false, newestFirst = true)
//                .toList()
//        }
//
//        verify {
//            dao.pagingPreviewsDesc()
//        }
//    }

    // markBeforeAsRead feed tests
    @Test
    fun markBeforeAsReadFeedUnreadNewestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = 6L,
                tag = "foo",
                onlyUnread = true,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc(6L, onlyUnread = 1, limit = 5, offset = 0)
        }
    }

    @Test
    fun markBeforeAsReadFeedUnreadOldestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = 6L,
                tag = "foo",
                onlyUnread = true,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc(6L, onlyUnread = 1, limit = 5, offset = 0)
        }
    }

    @Test
    fun markBeforeAsReadFeedReadNewestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = 6L,
                tag = "foo",
                onlyUnread = false,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc(6L, onlyUnread = 0, limit = 5, offset = 0)
        }
    }

    @Test
    fun markBeforeAsReadFeedReadOldestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = 6L,
                tag = "foo",
                onlyUnread = false,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc(6L, onlyUnread = 0, limit = 5, offset = 0)
        }
    }

    // markBeforeAsRead tag tests
    @Test
    fun markBeforeAsReadTagUnreadNewestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "foo",
                onlyUnread = true,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc("foo", onlyUnread = 1, limit = 5, offset = 0)
        }
    }

    @Test
    fun markBeforeAsReadTagUnreadOldestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "foo",
                onlyUnread = true,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc("foo", onlyUnread = 1, limit = 5, offset = 0)
        }
    }

    @Test
    fun markBeforeAsReadTagReadNewestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "foo",
                onlyUnread = false,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc("foo", onlyUnread = 0, limit = 5, offset = 0)
        }
    }

    @Test
    fun markBeforeAsReadTagReadOldestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "foo",
                onlyUnread = false,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc("foo", onlyUnread = 0, limit = 5, offset = 0)
        }
    }

    // markBeforeAsRead all tests
    @Test
    fun markBeforeAsReadAllUnreadNewestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "",
                onlyUnread = true,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc(onlyUnread = 1, limit = 5, offset = 0)
        }
    }

    @Test
    fun markBeforeAsReadAllUnreadOldestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "",
                onlyUnread = true,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc(onlyUnread = 1, limit = 5, offset = 0)
        }
    }

    @Test
    fun markBeforeAsReadAllReadNewestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "",
                onlyUnread = false,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc(onlyUnread = 0, limit = 5, offset = 0)
        }
    }

    @Test
    fun markBeforeAsReadAllReadOldestFirst() {
        runBlocking {
            store.markBeforeAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "",
                onlyUnread = false,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc(onlyUnread = 0, limit = 5, offset = 0)
        }
    }

    /*
     * markAfterAsRead tests
     */

    // markAfterAsRead feed tests
    @Test
    fun markAfterAsReadFeedUnreadNewestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = 6L,
                tag = "foo",
                onlyUnread = true,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc(6L, onlyUnread = 1, offset = 6)
        }
    }

    @Test
    fun markAfterAsReadFeedUnreadOldestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = 6L,
                tag = "foo",
                onlyUnread = true,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc(6L, onlyUnread = 1, offset = 6)
        }
    }

    @Test
    fun markAfterAsReadFeedReadNewestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = 6L,
                tag = "foo",
                onlyUnread = false,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc(6L, onlyUnread = 0, offset = 6)
        }
    }

    @Test
    fun markAfterAsReadFeedReadOldestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = 6L,
                tag = "foo",
                onlyUnread = false,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc(6L, onlyUnread = 0, offset = 6)
        }
    }

    // markAfterAsRead tag tests
    @Test
    fun markAfterAsReadTagUnreadNewestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "foo",
                onlyUnread = true,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc("foo", onlyUnread = 1, offset = 6)
        }
    }

    @Test
    fun markAfterAsReadTagUnreadOldestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "foo",
                onlyUnread = true,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc("foo", onlyUnread = 1, offset = 6)
        }
    }

    @Test
    fun markAfterAsReadTagReadNewestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "foo",
                onlyUnread = false,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc("foo", onlyUnread = 0, offset = 6)
        }
    }

    @Test
    fun markAfterAsReadTagReadOldestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "foo",
                onlyUnread = false,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc("foo", onlyUnread = 0, offset = 6)
        }
    }

    // markAfterAsRead all tests
    @Test
    fun markAfterAsReadAllUnreadNewestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "",
                onlyUnread = true,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc(onlyUnread = 1, offset = 6)
        }
    }

    @Test
    fun markAfterAsReadAllUnreadOldestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "",
                onlyUnread = true,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc(onlyUnread = 1, offset = 6)
        }
    }

    @Test
    fun markAfterAsReadAllReadNewestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "",
                onlyUnread = false,
                newestFirst = true,
            )
        }

        coVerify {
            dao.markAsReadDesc(onlyUnread = 0, offset = 6)
        }
    }

    @Test
    fun markAfterAsReadAllReadOldestFirst() {
        runBlocking {
            store.markAfterAsRead(
                index = 5,
                feedId = ID_UNSET,
                tag = "",
                onlyUnread = false,
                newestFirst = false,
            )
        }

        coVerify {
            dao.markAsReadAsc(onlyUnread = 0, offset = 6)
        }
    }

    @Test
    fun getFeedsItemsWithDefaultFullTextParse() {
        val expected = listOf(
            FeedItemIdWithLink(5L, "google.com"),
            FeedItemIdWithLink(6L, "cowboy.com"),
        )
        every { dao.getFeedsItemsWithDefaultFullTextParse() } returns flowOf(expected)

        val items = runBlocking {
            store.getFeedsItemsWithDefaultFullTextParse().first()
        }

        assertEquals(
            expected.size,
            items.size,
        )

        expected.zip(items) { expectedItem, actualItem ->
            assertEquals(
                expectedItem,
                actualItem,
            )
        }
    }

    @Test
    fun getFeedItemsNeedingNotifying() {
        val expected = listOf(1L, 2L)
        every { dao.getFeedItemsNeedingNotifying() } returns flowOf(expected)

        val items = runBlocking {
            store.getFeedItemsNeedingNotifying().first()
        }

        assertEquals(
            expected.size,
            items.size,
        )

        expected.zip(items) { expectedItem, actualItem ->
            assertEquals(
                expectedItem,
                actualItem,
            )
        }
    }

    @Test
    fun getFeedItemIdUrlAndGuid() {
        val url = URL("https://foo.bar")
        val guid = "foobar"
        coEvery { dao.getItemWith(url, guid) } returns 5L

        val id = runBlocking {
            store.getFeedItemId(url, guid)
        }

        assertEquals(5L, id)
    }

    @Test
    fun loadFeedItem() {
        coEvery { dao.loadFeedItem(any(), any()) } returns null

        val result = runBlocking {
            store.loadFeedItem("foo", 5L)
        }

        assertNull(result)

        coVerify { dao.loadFeedItem("foo", 5L) }
    }

    @Test
    fun getItemsToBeCleanedFromFeed() {
        coEvery { dao.getItemsToBeCleanedFromFeed(any(), any()) } returns listOf(5L)

        val result = runBlocking {
            store.getItemsToBeCleanedFromFeed(6L, 50)
        }

        assertEquals(5L, result.first())

        coVerify { dao.getItemsToBeCleanedFromFeed(6L, 50) }
    }

    @Test
    fun deleteFeedItems() {
        coEvery { dao.deleteFeedItems(any()) } returns 5

        runBlocking {
            store.deleteFeedItems(listOf(3L, 5L))
        }

        coVerify { dao.deleteFeedItems(listOf(3L, 5L)) }
    }
}
