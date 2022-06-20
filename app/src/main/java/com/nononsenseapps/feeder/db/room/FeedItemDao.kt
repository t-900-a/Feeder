package com.nononsenseapps.feeder.db.room

import android.database.Cursor
import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nononsenseapps.feeder.db.COL_ID
import com.nononsenseapps.feeder.db.COL_PLAINSNIPPET
import com.nononsenseapps.feeder.db.COL_TITLE
import com.nononsenseapps.feeder.db.COL_URL
import com.nononsenseapps.feeder.db.FEEDS_TABLE_NAME
import com.nononsenseapps.feeder.model.PreviewItem
import com.nononsenseapps.feeder.model.previewColumns
import java.net.URL
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFeedItem(item: FeedItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFeedItems(items: List<FeedItem>): List<Long>

    @Update
    suspend fun updateFeedItem(item: FeedItem): Int

    @Update
    suspend fun updateFeedItems(items: List<FeedItem>): Int

    @Delete
    suspend fun deleteFeedItem(item: FeedItem): Int

    @Query(
        """
        DELETE FROM feed_items WHERE id IN (:ids)
        """
    )
    suspend fun deleteFeedItems(ids: List<Long>): Int

    @Query(
        """
        SELECT id FROM feed_items
        WHERE feed_id IS :feedId AND pinned = 0 AND bookmarked = 0
        ORDER BY primary_sort_time DESC, pub_date DESC
        LIMIT -1 OFFSET :keepCount
        """
    )
    suspend fun getItemsToBeCleanedFromFeed(feedId: Long, keepCount: Int): List<Long>

    @Query("SELECT * FROM feed_items WHERE guid IS :guid AND feed_id IS :feedId")
    suspend fun loadFeedItem(guid: String, feedId: Long?): FeedItem?

    @Query("SELECT * FROM feed_items WHERE id IS :id")
    suspend fun loadFeedItem(id: Long): FeedItem?

    @Query(
        """
        SELECT $feedItemColumnsWithFeed
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_items.id IS :id
        """
    )
    suspend fun loadFeedItemWithFeed(id: Long): FeedItemWithFeed?

    @Query(
        """
        SELECT $FEEDS_TABLE_NAME.$COL_URL
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_items.id IS :id
        """
    )
    suspend fun loadFeedUrlOfFeedItem(id: Long): URL?

    @Query(
        """
        SELECT *
        FROM feed_items
        WHERE feed_items.feed_id = :feedId
        ORDER BY primary_sort_time DESC, pub_date DESC
        """
    )
    suspend fun loadFeedItemsInFeedDesc(feedId: Long): List<FeedItem>

    @Query(
        """
        SELECT $COL_ID as id, $COL_TITLE as title, $COL_PLAINSNIPPET as text
        FROM feed_items
        ORDER BY primary_sort_time DESC, pub_date DESC
        """
    )
    fun loadFeedItemsForContentProvider(): Cursor

    @Query(
        """
        SELECT $COL_ID as id, $COL_TITLE as title, $COL_PLAINSNIPPET as text
        FROM feed_items
        WHERE feed_items.feed_id = :feedId
        ORDER BY primary_sort_time DESC, pub_date DESC
        """
    )
    fun loadFeedItemsInFeedForContentProvider(feedId: Long): Cursor

    @Query(
        """
        SELECT *
        FROM feed_items
        WHERE feed_items.feed_id = :feedId
        ORDER BY primary_sort_time ASC, pub_date ASC
        """
    )
    suspend fun loadFeedItemsInFeedAsc(feedId: Long): List<FeedItem>

    @Query(
        """
        SELECT $feedItemColumnsWithFeed
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_items.id IS :id
        """
    )
    fun loadFeedItemFlow(id: Long): Flow<FeedItemWithFeed?>

    @Query(
        """
        SELECT $feedItemColumnsWithFeed
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_items.id IS :id
        """
    )
    suspend fun getFeedItem(id: Long): FeedItemWithFeed?

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_id IS :feedId
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun loadLivePreviewsDesc(feedId: Long): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_id IS :feedId
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun loadLivePreviewsAsc(feedId: Long): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE tag IS :tag
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun loadLivePreviewsDesc(tag: String): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE tag IS :tag
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun loadLivePreviewsAsc(tag: String): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun loadLivePreviewsDesc(): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun loadLivePreviewsAsc(): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_id IS :feedId AND (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun loadLiveUnreadPreviewsDesc(feedId: Long?, unread: Boolean = true): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_id IS :feedId AND (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun pagingUnreadPreviewsDesc(feedId: Long, unread: Boolean = true): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE tag IS :tag AND (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun pagingUnreadPreviewsDesc(tag: String, unread: Boolean = true): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun pagingUnreadPreviewsDesc(unread: Boolean = true): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_id IS :feedId AND (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun pagingUnreadPreviewsAsc(feedId: Long, unread: Boolean = true): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE tag IS :tag AND (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun pagingUnreadPreviewsAsc(tag: String, unread: Boolean = true): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun pagingUnreadPreviewsAsc(unread: Boolean = true): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_id IS :feedId
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun pagingPreviewsDesc(feedId: Long): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE tag IS :tag
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun pagingPreviewsDesc(tag: String): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun pagingPreviewsDesc(): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_id IS :feedId
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun pagingPreviewsAsc(feedId: Long): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE tag IS :tag
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun pagingPreviewsAsc(tag: String): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun pagingPreviewsAsc(): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE bookmarked = 1
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun pagingBookmarksAsc(): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE bookmarked = 1 AND feed_id IS :feedId
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun pagingBookmarksAsc(feedId: Long): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE bookmarked = 1 AND tag IS :tag
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun pagingBookmarksAsc(tag: String): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE bookmarked = 1
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun pagingBookmarksDesc(): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE bookmarked = 1 AND feed_id IS :feedId
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun pagingBookmarksDesc(feedId: Long): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE bookmarked = 1 AND tag IS :tag
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun pagingBookmarksDesc(tag: String): PagingSource<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_id IS :feedId AND (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun loadLiveUnreadPreviewsAsc(feedId: Long?, unread: Boolean = true): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE tag IS :tag AND (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun loadLiveUnreadPreviewsDesc(tag: String, unread: Boolean = true): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE tag IS :tag AND (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun loadLiveUnreadPreviewsAsc(tag: String, unread: Boolean = true): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time DESC, pub_date DESC
        """
    )
    fun loadLiveUnreadPreviewsDesc(unread: Boolean = true): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $previewColumns
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE (unread IS :unread OR pinned = 1)
        ORDER BY pinned DESC, primary_sort_time ASC, pub_date ASC
        """
    )
    fun loadLiveUnreadPreviewsAsc(unread: Boolean = true): DataSource.Factory<Int, PreviewItem>

    @Query(
        """
        SELECT $feedItemColumnsWithFeed
        FROM feed_items
        LEFT JOIN feeds ON feed_items.feed_id = feeds.id
        WHERE feed_id IN (:feedIds) AND notified IS 0 AND unread IS 1
        """
    )
    suspend fun loadItemsToNotify(feedIds: List<Long>): List<FeedItemWithFeed>

    @Query("UPDATE feed_items SET unread = 0, notified = 1")
    suspend fun markAllAsRead()

    @Query("UPDATE feed_items SET unread = 0, notified = 1 WHERE feed_id IS :feedId")
    suspend fun markAllAsRead(feedId: Long?)

    @Query(
        """
        UPDATE feed_items
        SET unread = 0, notified = 1
        WHERE id IN (
          SELECT feed_items.id
          FROM feed_items
          LEFT JOIN feeds ON feed_items.feed_id = feeds.id
          WHERE tag IS :tag
        )"""
    )
    suspend fun markAllAsRead(tag: String)

    @Query("UPDATE feed_items SET unread = :unread, notified = 1 WHERE id IS :id")
    suspend fun markAsRead(id: Long, unread: Boolean = false)

    @Query("UPDATE feed_items SET unread = :unread, notified = 1 WHERE id IN (:ids)")
    suspend fun markAsRead(ids: List<Long>, unread: Boolean = false)

    @Query("UPDATE feed_items SET pinned = :pinned WHERE id IS :id")
    suspend fun setPinned(id: Long, pinned: Boolean)

    @Query("UPDATE feed_items SET bookmarked = :bookmarked WHERE id IS :id")
    suspend fun setBookmarked(id: Long, bookmarked: Boolean)

    @Query("UPDATE feed_items SET notified = :notified WHERE id IN (:ids)")
    suspend fun markAsNotified(ids: List<Long>, notified: Boolean = true)

    @Query("UPDATE feed_items SET notified = :notified WHERE id IS :id")
    suspend fun markAsNotified(id: Long, notified: Boolean = true)

    @Query(
        """
        UPDATE feed_items
        SET notified = :notified
        WHERE id IN (
          SELECT feed_items.id
          FROM feed_items
          LEFT JOIN feeds ON feed_items.feed_id = feeds.id
          WHERE tag IS :tag
        )"""
    )
    suspend fun markTagAsNotified(tag: String, notified: Boolean = true)

    @Query("UPDATE feed_items SET notified = :notified")
    suspend fun markAllAsNotified(notified: Boolean = true)

    @Query("UPDATE feed_items SET unread = 0, notified = 1 WHERE id IS :id")
    suspend fun markAsReadAndNotified(id: Long)

    @Query(
        """
            UPDATE feed_items SET unread = 0, notified = 1
            WHERE id IN (
                SELECT feed_items.id
                FROM feed_items
                WHERE unread = 1 OR unread = :onlyUnread
                ORDER BY primary_sort_time DESC, pub_date DESC
                LIMIT :limit OFFSET :offset
            )
        """
    )
    suspend fun markAsReadDesc(onlyUnread: Int, limit: Int = Int.MAX_VALUE, offset: Int)

    @Query(
        """
            UPDATE feed_items SET unread = 0, notified = 1
            WHERE id IN (
                SELECT feed_items.id
                FROM feed_items
                WHERE unread = 1 OR unread = :onlyUnread
                ORDER BY primary_sort_time ASC, pub_date ASC
                LIMIT :limit OFFSET :offset
            )
        """
    )
    suspend fun markAsReadAsc(onlyUnread: Int, limit: Int = Int.MAX_VALUE, offset: Int)

    @Query(
        """
            UPDATE feed_items SET unread = 0, notified = 1
            WHERE id IN (
                SELECT feed_items.id
                FROM feed_items
                WHERE feed_id = :feedId AND (unread = 1 OR unread = :onlyUnread)
                ORDER BY primary_sort_time DESC, pub_date DESC
                LIMIT :limit OFFSET :offset
            )
        """
    )
    suspend fun markAsReadDesc(
        feedId: Long,
        onlyUnread: Int,
        limit: Int = Int.MAX_VALUE,
        offset: Int
    )

    @Query(
        """
            UPDATE feed_items SET unread = 0, notified = 1
            WHERE id IN (
                SELECT feed_items.id
                FROM feed_items
                WHERE feed_id = :feedId AND (unread = 1 OR unread = :onlyUnread)
                ORDER BY primary_sort_time ASC, pub_date ASC
                LIMIT :limit OFFSET :offset
            )
        """
    )
    suspend fun markAsReadAsc(
        feedId: Long,
        onlyUnread: Int,
        limit: Int = Int.MAX_VALUE,
        offset: Int
    )

    @Query(
        """
            UPDATE feed_items SET unread = 0, notified = 1
            WHERE id IN (
                SELECT feed_items.id
                FROM feed_items
                LEFT JOIN feeds ON feed_items.feed_id = feeds.id
                WHERE tag IS :tag AND (unread = 1 OR unread = :onlyUnread)
                ORDER BY primary_sort_time DESC, pub_date DESC
                LIMIT :limit OFFSET :offset
            )
        """
    )
    suspend fun markAsReadDesc(
        tag: String,
        onlyUnread: Int,
        limit: Int = Int.MAX_VALUE,
        offset: Int
    )

    @Query(
        """
            UPDATE feed_items SET unread = 0, notified = 1
            WHERE id IN (
                SELECT feed_items.id
                FROM feed_items
                LEFT JOIN feeds ON feed_items.feed_id = feeds.id
                WHERE tag IS :tag AND (unread = 1 OR unread = :onlyUnread)
                ORDER BY primary_sort_time ASC, pub_date ASC
                LIMIT :limit OFFSET :offset
            )
        """
    )
    suspend fun markAsReadAsc(tag: String, onlyUnread: Int, limit: Int = Int.MAX_VALUE, offset: Int)

    @Query(
        """
            SELECT feeds.open_articles_with
            FROM feed_items
            LEFT JOIN feeds ON feed_items.feed_id = feeds.id
            WHERE feed_items.id IS :itemId
        """
    )
    suspend fun getOpenArticleWith(itemId: Long): String?

    @Query(
        """
            SELECT feeds.fulltext_by_default
            FROM feed_items
            LEFT JOIN feeds ON feed_items.feed_id = feeds.id
            WHERE feed_items.id IS :itemId
        """
    )
    suspend fun getFullTextByDefault(itemId: Long): Boolean?

    @Query(
        """
            SELECT link
            FROM feed_items
            WHERE feed_items.id IS :itemid
        """
    )
    suspend fun getLink(itemid: Long): String?

    @Query(
        """
            SELECT fi.id, fi.link
            FROM feed_items fi
            JOIN feeds f ON feed_id = f.id
            WHERE f.fulltext_by_default = 1
        """
    )
    fun getFeedsItemsWithDefaultFullTextParse(): Flow<List<FeedItemIdWithLink>>

    @Query(
        """
            SELECT count(*)
            FROM feed_items fi
        """
    )
    fun getFeedItemCount(): Flow<Int>

    @Query(
        """
            SELECT count(*)
            FROM feed_items fi
            JOIN feeds f ON feed_id = f.id
            WHERE f.tag IS :tag
        """
    )
    fun getFeedItemCount(tag: String): Flow<Int>

    @Query(
        """
            SELECT count(*)
            FROM feed_items fi
            WHERE fi.feed_id IS :feedId
        """
    )
    fun getFeedItemCount(feedId: Long): Flow<Int>

    // //

    @Query(
        """
            SELECT count(*)
            FROM feed_items fi
            WHERE fi.unread IS 1 OR fi.pinned = 1
        """
    )
    fun getUnreadFeedItemCount(): Flow<Int>

    @Query(
        """
            SELECT count(*)
            FROM feed_items fi
            JOIN feeds f ON fi.feed_id = f.id
            WHERE f.tag IS :tag AND (fi.unread IS 1 OR fi.pinned = 1)
        """
    )
    fun getUnreadFeedItemCount(tag: String): Flow<Int>

    @Query(
        """
            SELECT count(*)
            FROM feed_items fi
            WHERE fi.feed_id IS :feedId AND (fi.unread IS 1 OR fi.pinned = 1)
        """
    )
    fun getUnreadFeedItemCount(feedId: Long): Flow<Int>

    @Query(
        """
            SELECT fi.id
            FROM feed_items fi
            JOIN feeds f ON feed_id = f.id
            WHERE f.notify IS 1 AND fi.notified IS 0 AND fi.unread is 1
        """
    )
    fun getFeedItemsNeedingNotifying(): Flow<List<Long>>

    @Query(
        """
            SELECT fi.id
            FROM feed_items fi
            JOIN feeds f ON fi.feed_id = f.id
            where f.url IS :feedUrl AND fi.guid IS :articleGuid
        """
    )
    suspend fun getItemWith(feedUrl: URL, articleGuid: String): Long?
}

suspend fun FeedItemDao.upsertFeedItem(item: FeedItem): Long = when (item.id > ID_UNSET) {
    true -> {
        updateFeedItem(item)
        item.id
    }
    false -> insertFeedItem(item)
}

suspend fun FeedItemDao.upsertFeedItems(
    itemsWithText: List<Pair<FeedItem, String>>,
    block: suspend (FeedItem, String) -> Unit
) {
    val updatedItems = itemsWithText.filter { (item, _) ->
        item.id > ID_UNSET
    }
    updateFeedItems(updatedItems.map { (item, _) -> item })

    val insertedItems = itemsWithText.filter { (item, _) ->
        item.id <= ID_UNSET
    }
    val insertedIds = insertFeedItems(insertedItems.map { (item, _) -> item })

    updatedItems.forEach { (item, text) ->
        block(item, text)
    }

    insertedIds.zip(insertedItems).forEach { (itemId, itemToText) ->
        val (item, text) = itemToText

        item.id = itemId

        block(item, text)
    }
}
