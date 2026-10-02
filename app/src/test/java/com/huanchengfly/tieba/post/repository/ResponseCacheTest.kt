package com.huanchengfly.tieba.post.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class ResponseCacheTest {
    @Test
    fun differentRequestCannotReusePreviousResponse() {
        val cache = ResponseCache<String, String>()
        cache.put("forum-a", "response-a")
        // Starting or failing a different request does not publish a response.
        assertNull(cache.get("forum-b"))
        assertEquals("response-a", cache.get("forum-a"))
        cache.put("forum-b", "response-b")
        assertEquals("response-b", cache.get("forum-b"))
        assertNull(cache.get("forum-a"))
    }

    @Test
    fun concurrentPublicationsNeverMixKeysAndResponses() {
        val cache = ResponseCache<Int, Int>()
        val start = CountDownLatch(1)
        val failure = AtomicReference<Throwable?>()
        val workers = (0 until 4).map { key ->
            thread {
                try {
                    start.await()
                    repeat(10_000) {
                        cache.put(key, key)
                        val response = cache.get(key)
                        if (response != null) assertEquals(key, response.toInt())
                    }
                } catch (e: Throwable) {
                    failure.compareAndSet(null, e)
                }
            }
        }
        start.countDown()
        workers.forEach { it.join() }
        failure.get()?.let { throw it }
    }
}
