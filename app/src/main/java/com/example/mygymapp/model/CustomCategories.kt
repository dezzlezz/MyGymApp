package com.example.mygymapp.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

object CustomCategories {
    var list: SnapshotStateList<String> = mutableStateListOf()
}
