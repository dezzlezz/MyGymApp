package com.example.mygymapp.data

import androidx.room.TypeConverter

class PlanConverters {
    @TypeConverter fun uriToString(uri: android.net.Uri?): String? = uri?.toString()
    @TypeConverter fun stringToUri(value: String?): android.net.Uri? = value?.let { android.net.Uri.parse(it) }
    @TypeConverter fun planTypeToString(type: PlanType): String = type.name
    @TypeConverter fun stringToPlanType(value: String): PlanType = PlanType.valueOf(value)
}
