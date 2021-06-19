package com.theapache64.reky.data.repo

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Created by theapache64 : May 29 Sat,2021 @ 17:08
 */
class RecordsRepo @Inject constructor(
    @ApplicationContext val context: Context
) {
    suspend fun getRecords(recordsDir: String): List<File> = withContext(Dispatchers.IO) {
        File(recordsDir).listFiles()?.toList()!!
    }


    fun getDurationInMillis(audioFile: File): Long {
        val uri = Uri.parse(audioFile.absolutePath)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return durationStr?.toLong() ?: -1
    }
}