package com.huanchengfly.tieba.post.ui.page.forum.generaltablist

import com.huanchengfly.tieba.post.api.models.protos.GeneralTabList.GeneralTabListResponse
import com.huanchengfly.tieba.post.api.models.protos.GeneralTabList.GeneralTabListResponseData
import com.huanchengfly.tieba.post.api.models.protos.ThreadInfo
import com.huanchengfly.tieba.post.api.models.protos.VideoInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneralTabPageTest {
    @Test
    fun hiddenLastThreadStillSuppliesTheServerCursor() {
        val response = GeneralTabListResponse(data_ = GeneralTabListResponseData(
            general_list = listOf(ThreadInfo(id = 10), ThreadInfo(id = 20, videoInfo = VideoInfo())),
            has_more = 1,
        ))
        val page = response.toPage(blockVideo = true)
        assertEquals(listOf(10L), page.visibleThreads.map { it.id })
        assertEquals(20L, page.lastThreadId)
        assertTrue(page.hasMore)
    }

    @Test
    fun fullyFilteredPageDoesNotBecomeTheLastPage() {
        val response = GeneralTabListResponse(data_ = GeneralTabListResponseData(
            general_list = listOf(ThreadInfo(id = 20, videoInfo = VideoInfo())),
            has_more = 1,
        ))
        val page = response.toPage(blockVideo = true)
        assertTrue(page.visibleThreads.isEmpty())
        assertTrue(page.hasMore)
        assertEquals(20L, page.lastThreadId)
        assertEquals(listOf(20L), response.toPage(blockVideo = false).visibleThreads.map { it.id })
    }

    @Test
    fun emptyServerPagePreservesThePreviousCursor() {
        val page = GeneralTabListResponse(data_ = GeneralTabListResponseData(has_more = 0))
            .toPage(blockVideo = true, fallbackThreadId = 99)
        assertEquals(99L, page.lastThreadId)
        assertEquals(false, page.hasMore)
    }
}
