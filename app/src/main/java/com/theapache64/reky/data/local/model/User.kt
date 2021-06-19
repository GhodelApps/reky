package com.theapache64.reky.data.local.model

import com.theapache64.reky.ui.composable.ListItem

/**
 * Created by theapache64 : May 30 Sun,2021 @ 01:10
 */
data class User(
    val contact: Contact,
    var recordCount: Int? = 1
) : ListItem {

    override fun getCircleText(): String {
        return (recordCount ?: 0).toString()
    }

    override fun getMainText(): String {
        return contact.name
    }
}