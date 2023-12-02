package com.example.videobubble

import android.content.Context
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

private const val DOWNLOAD_CONTENT_DIRECTORY = "inner_video_cache"
private const val MAX_CACHE_SIZE_IN_BYTES = 100 * 1024 * 1024

object VideoCache {

    private var cache: SimpleCache? = null

    fun getInstance(context: Context): SimpleCache {
        return cache ?: run {
            //путь до файла в котором будет храниться кеш видео
            val cacheDir = File(context.externalCacheDir, DOWNLOAD_CONTENT_DIRECTORY)
            //стратегия очистки кеша (очистка последнего использованного кеша)
            val evictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE_IN_BYTES.toLong())
            val databaseProvider = StandaloneDatabaseProvider(context)
            SimpleCache(cacheDir, evictor, databaseProvider).apply {
                cache = this
            }
        }
    }
}