package com.example.mygymapp.ui.util

/**
 * Moves an item inside a mutable list from [from] index to [to] index.
 */
fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to) return
    val item = removeAt(from)
    add(to, item)
}