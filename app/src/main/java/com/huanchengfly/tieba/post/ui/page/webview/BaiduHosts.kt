package com.huanchengfly.tieba.post.ui.page.webview

import java.util.Locale

private fun String.matchesDomain(domain: String): Boolean {
    val host = lowercase(Locale.ROOT).trimEnd('.')
    return host == domain || host.endsWith(".$domain")
}

fun isTiebaHost(host: String): Boolean =
    host.matchesDomain("wapp.baidu.com") ||
        host.matchesDomain("tieba.baidu.com") ||
        host.matchesDomain("tiebac.baidu.com")

fun isInternalHost(host: String): Boolean =
    isTiebaHost(host) ||
        host.matchesDomain("wappass.baidu.com") ||
        host.matchesDomain("ufosdk.baidu.com") ||
        host.matchesDomain("m.help.baidu.com")
