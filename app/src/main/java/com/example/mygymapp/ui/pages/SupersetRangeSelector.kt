package com.example.mygymapp.ui.pages

import androidx.compose.ui.geometry.Offset

/** Snapshot of an active or completed range selection. */
data class SupersetRangeSelection(
    val startId: Long,
    val endId: Long,
    val idsInRange: List<Long>
)

/** Simple data carrier for active pointers. */
data class PointerInfo(val id: Long, val position: Offset)

/**
 * Utility to detect two finger "bridge" gestures across a list of exercises and return the
 * corresponding range of ids between the touched items.
 *
 * The selector keeps minimal internal state so it can be reused across pointer events.
 */
class SupersetRangeSelector(private val itemBounds: Map<Long, Pair<Float, Float>>) {
    private var firstPointer: Long? = null
    private var secondPointer: Long? = null
    private var activeSelection: SupersetRangeSelection? = null

    /** Reset internal tracking. */
    fun reset() {
        firstPointer = null
        secondPointer = null
        activeSelection = null
    }

    /**
     * Process raw pointer information. [listY] is unused but kept for potential future
     * enhancements. Returns the current selection while two pointers are down or the final
     * selection once all pointers are released.
     */
    fun onPointerEvent(listY: Float, pointers: List<PointerInfo>): SupersetRangeSelection? {
        if (pointers.size > 2) {
            reset();
            return null
        }

        if (pointers.isEmpty()) {
            val result = activeSelection
            reset()
            return result
        }

        if (firstPointer == null && pointers.isNotEmpty()) {
            firstPointer = pointers.first().id
        }
        if (firstPointer != null && secondPointer == null && pointers.size == 2) {
            secondPointer = pointers.firstOrNull { it.id != firstPointer }?.id
        }

        if (pointers.size < 2 || firstPointer == null || secondPointer == null) {
            return null
        }

        val firstPos = pointers.firstOrNull { it.id == firstPointer }?.position
        val secondPos = pointers.firstOrNull { it.id == secondPointer }?.position

        if (firstPos == null || secondPos == null) {
            activeSelection = null
            return null
        }

        val boundsTop = itemBounds.values.minOfOrNull { it.first } ?: return null
        val boundsBottom = itemBounds.values.maxOfOrNull { it.second } ?: return null
        if (firstPos.y !in boundsTop..boundsBottom || secondPos.y !in boundsTop..boundsBottom) {
            activeSelection = null
            return null
        }

        val startId = idAt(firstPos.y) ?: return null
        val endId = idAt(secondPos.y) ?: return null

        val sorted = itemBounds.entries.sortedBy { it.value.first }
        val startIdx = sorted.indexOfFirst { it.key == startId }
        val endIdx = sorted.indexOfFirst { it.key == endId }
        if (startIdx == -1 || endIdx == -1) return null
        val range = if (startIdx <= endIdx) startIdx..endIdx else endIdx..startIdx
        val ids = range.map { sorted[it].key }
        val selection = SupersetRangeSelection(startId, endId, ids)
        activeSelection = selection
        return selection
    }

    private fun idAt(y: Float): Long? {
        return itemBounds.entries.firstOrNull { y >= it.value.first && y <= it.value.second }?.key
    }
}

