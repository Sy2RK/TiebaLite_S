package com.huanchengfly.tieba.post.repository

import com.huanchengfly.tieba.post.api.TiebaApi
import com.huanchengfly.tieba.post.api.models.protos.GeneralTabList.GeneralTabListResponse
import com.huanchengfly.tieba.post.api.retrofit.exception.TiebaUnknownException
import com.huanchengfly.tieba.post.utils.AccountUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

object GeneralTabListRepository {
    private data class CacheKey(
        val accountId: Int?,
        val forumId: Long,
        val forumName: String,
        val tabId: Int,
        val tabType: Int,
        val tabName: String,
        val isGeneralTab: Int,
        val pn: Int,
        val sortType: Int,
        val lastThreadId: Long,
        val isDefaultNavTab: Int,
    )

    private val cache = ResponseCache<CacheKey, GeneralTabListResponse>()

    fun generalTabList(
        forumId: Long,
        forumName: String,
        tabId: Int,
        tabType: Int,
        tabName: String,
        isGeneralTab: Int,
        pn: Int = 1,
        sortType: Int = -1,
        lastThreadId: Long = 0,
        isDefaultNavTab: Int = 0,
        forceNew: Boolean = false,
    ): Flow<GeneralTabListResponse> {
        return flow {
            val key = CacheKey(AccountUtil.currentAccount?.id, forumId, forumName, tabId, tabType,
                tabName, isGeneralTab, pn, sortType, lastThreadId, isDefaultNavTab)
            val cached = if (forceNew) null else cache.get(key)
            if (cached != null) {
                emit(cached)
            } else {
                emitAll(TiebaApi.getInstance().generalTabList(
                    forumId, forumName, tabId, tabType, tabName, isGeneralTab,
                    pn, sortType, lastThreadId, isDefaultNavTab
                ).onEach { response ->
                    if (response.data_ == null) throw TiebaUnknownException
                    cache.put(key, response)
                })
            }
        }.map { response ->
            if (response.data_ == null) throw TiebaUnknownException
            val userList = response.data_.user_list
            val threadList = response.data_.general_list
                .map { threadInfo ->
                    threadInfo.copy(author = userList.find { it.id == threadInfo.authorId })
                }
            response.copy(data_ = response.data_.copy(general_list = threadList))
        }
    }
}
