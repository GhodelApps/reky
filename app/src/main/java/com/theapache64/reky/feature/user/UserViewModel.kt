package com.theapache64.reky.feature.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theapache64.reky.data.local.model.Recording
import com.theapache64.reky.data.repo.ConfigRepo
import com.theapache64.reky.data.repo.RecordsRepo
import com.theapache64.reky.util.Resource
import com.theapache64.reky.util.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by theapache64 : May 29 Sat,2021 @ 17:20
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    recordsRepo: RecordsRepo,
    configRepo: ConfigRepo
) : ViewModel() {
    companion object {
        const val KEY_USER_NAME = "userName"
        const val KEY_USER_MOBILE = "userMobile"
    }

    private val _pageTitle = MutableStateFlow("")
    val pageTitle = _pageTitle.asStateFlow()

    private val _recordings = MutableStateFlow<Resource<List<Recording>>>(Resource.Idle())
    val recordings = _recordings.asStateFlow()

    init {
        val userName = savedStateHandle.get<String>(KEY_USER_NAME)!!
        val userMobile = savedStateHandle.get<String>(KEY_USER_MOBILE)!!

        _pageTitle.value = userName
        viewModelScope.launch {
            _recordings.value = Resource.Loading()

            val recordings: List<Recording> = recordsRepo
                .getRecords(configRepo.getConfig()!!.recordsDir)
                .filter { it.name.contains(userName) || it.name.contains(userMobile) }
                .map { file ->
                    val duration = millisToDuration(recordsRepo.getDurationInMillis(file))
                    val recordedAt = parseRecordedAt(file.name)
                    Recording(duration = duration, recordedAt = recordedAt, file = file)
                }

            _recordings.value = Resource.Success(recordings)
        }
    }

    private val inputDateTimeFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())

    private fun parseRecordedAt(fileName: String): String {
        val timestamp = fileName.split("-")[1].split(".")[0]
        val recordedAt = inputDateTimeFormat.parse(timestamp)?.time ?: 0
        return TimeUtils.getTimeAgo(recordedAt)!!
    }


    private fun millisToDuration(durationInMillis: Long): String {
        // 911171279953-20210425194141
        return "Y"
    }
}