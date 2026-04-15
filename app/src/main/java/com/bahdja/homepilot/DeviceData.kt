package com.bahdja.homepilot

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val opening: Int?,
    val power: Int?
) : Parcelable
