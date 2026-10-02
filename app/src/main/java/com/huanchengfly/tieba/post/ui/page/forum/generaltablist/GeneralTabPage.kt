package com.huanchengfly.tieba.post.ui.page.forum.generaltablist

import com.huanchengfly.tieba.post.api.models.protos.GeneralTabList.GeneralTabListResponse
import com.huanchengfly.tieba.post.api.models.protos.ThreadInfo
import com.huanchengfly.tieba.post.api.retrofit.exception.TiebaUnknownException

internal data class GeneralTabPage(
    val visibleThreads: List<ThreadInfo>,
    val hasMore: Boolean,
    val lastThreadId: Long,
)

internal fun GeneralTabListResponse.toPage(
    blockVideo: Boolean,
    fallbackThreadId: Long = 0,
): GeneralTabPage {
    val data = data_ ?: throw TiebaUnknownException
    return GeneralTabPage(
        visibleThreads = data.general_list
            .filter { !blockVideo || it.videoInfo == null }
            .filter { it.ala_info == null },
        hasMore = data.has_more == 1,
        lastThreadId = data.general_list.lastOrNull()?.id ?: fallbackThreadId,
    )
}
