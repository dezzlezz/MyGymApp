package com.example.mygymapp.data

import android.net.Uri
import androidx.room.TypeConverter

class PlanConverters {
    @TypeConverter
    fun uriToString(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun stringToUri(value: String?): Uri? = value?.let { Uri.parse(it) }

    @TypeConverter
    fun planTypeToString(type: PlanType): String = type.name

    @TypeConverter
    fun stringToPlanType(value: String): PlanType = PlanType.valueOf(value)
}