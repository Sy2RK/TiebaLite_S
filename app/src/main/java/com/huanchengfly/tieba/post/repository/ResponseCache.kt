package com.huanchengfly.tieba.post.repository

/** A single cached response whose key and value are published together. */
internal class ResponseCache<K, V> {
    private data class Entry<K, V>(val key: K, val value: V)

    @Volatile
    private var entry: Entry<K, V>? = null

    fun get(key: K): V? {
        val snapshot = entry
        return snapshot?.takeIf { it.key == key }?.value
    }

    fun put(key: K, value: V) {
        entry = Entry(key, value)
    }
}
