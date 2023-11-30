package com.example.videobubble

import android.content.Context
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource

internal class MediaSourceFactory(
    private val context: Context
) {

    private val mediaSourceFactory by lazy {
        val cacheSink = CacheDataSink.Factory().setCache(VideoCache.getInstance(context))
        val upstreamFactory = DefaultDataSource.Factory(context, DefaultHttpDataSource.Factory())
        val cacheDataSourceFactory = CacheDataSource.Factory()
            //то куда будет сохраняться наш кеш. Если не указывать, то кеш будет read-only
            .setCacheWriteDataSinkFactory(cacheSink)
            //собственно сам кеш
            .setCache(VideoCache.getInstance(context))
            //то откуда будет подргужаться наше видео
            .setUpstreamDataSourceFactory(upstreamFactory)
            //игнорируем ошибки при зависи в кеш
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        //Нужно выбрать MediaSource в соответсвии с форматом видео. Для большинства форматов подходит ProgressiveMediaSource
        ProgressiveMediaSource.Factory(cacheDataSourceFactory)
    }

    fun createMediaSource(url: String): MediaSource {
        return mediaSourceFactory.createMediaSource(MediaItem.fromUri(url))
    }
}