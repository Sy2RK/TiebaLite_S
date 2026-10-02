package com.huanchengfly.tieba.post.ui.page.webview

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BaiduHostsTest {
    @Test
    fun officialHostsAndSubdomainsRemainInternal() {
        listOf("wapp.baidu.com", "tieba.baidu.com", "tiebac.baidu.com",
            "m.tieba.baidu.com", "TIEBA.BAIDU.COM.").forEach {
            assertTrue(it, isTiebaHost(it))
        }
        listOf("wappass.baidu.com", "ufosdk.baidu.com", "m.help.baidu.com",
            "login.wappass.baidu.com").forEach {
            assertTrue(it, isInternalHost(it))
        }
    }

    @Test
    fun embeddedBaiduNamesDoNotMakeAnExternalHostInternal() {
        listOf("tieba.baidu.com.attacker.example", "eviltieba.baidu.com",
            "wappass.baidu.com.attacker.example", "evilufosdk.baidu.com",
            "m.help.baidu.com.attacker.example", "baidu.com", "", "example.com").forEach {
            assertFalse(it, isInternalHost(it))
        }
    }
}
