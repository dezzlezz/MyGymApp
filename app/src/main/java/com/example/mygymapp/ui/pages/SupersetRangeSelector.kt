package com.example.mygymapp.ui.pages

import android.content.res.Resources
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
    private var startAnchorId: Long? = null
    private var endAnchorId: Long? = null
    private var activeSelection: SupersetRangeSelection? = null
    private var initialDistance: Float? = null
    private var currentDistance: Float = 0f
    private val hysteresisPx = 20f * Resources.getSystem().displayMetrics.density

    /** Reset internal tracking. */
    fun reset() {
        firstPointer = null
        secondPointer = null
        startAnchorId = null
        endAnchorId = null
        activeSelection = null
        initialDistance = null
        currentDistance = 0f
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
            val p = pointers.first()
            firstPointer = p.id
            startAnchorId = idAt(p.position.y)
        }
        if (firstPointer != null && secondPointer == null && pointers.size >= 2) {
            val p = pointers.firstOrNull { it.id != firstPointer }
            if (p != null) {
                secondPointer = p.id
                endAnchorId = idAt(p.position.y)
            }
        }

        val sorted = itemBounds.entries.sortedBy { it.value.first }
        val sortedIds = sorted.map { it.key }
        val idToIndex = sortedIds.withIndex().associate { it.value to it.index }

        val firstPos = pointers.firstOrNull { it.id == firstPointer }?.position
        if (firstPos != null) {
            startAnchorId = updateAnchor(startAnchorId, firstPos.y, sortedIds, idToIndex)
        }

        val secondPos = pointers.firstOrNull { it.id == secondPointer }?.position
        if (secondPos != null) {
            endAnchorId = updateAnchor(endAnchorId, secondPos.y, sortedIds, idToIndex)
        }

        if (firstPos != null && secondPos != null) {
            currentDistance = kotlin.math.abs(firstPos.y - secondPos.y)
            if (initialDistance == null) initialDistance = currentDistance
        }

        val startId = startAnchorId
        val endId = endAnchorId
        if (startId == null || endId == null) {
            return activeSelection
        }

        val startIdx = idToIndex[startId] ?: return activeSelection
        val endIdx = idToIndex[endId] ?: return activeSelection
        val range = if (startIdx <= endIdx) startIdx..endIdx else endIdx..startIdx
        val ids = range.map { sortedIds[it] }
        val selection = SupersetRangeSelection(startId, endId, ids)
        activeSelection = selection
        return selection
    }

    fun isOutwardPull(thresholdPx: Float): Boolean {
        val start = initialDistance ?: return false
        return currentDistance - start >= thresholdPx
    }

    val startDistance: Float?
        get() = initialDistance

    val distance: Float
        get() = currentDistance

    private fun idAt(y: Float): Long? {
        return itemBounds.entries.firstOrNull { y >= it.value.first && y <= it.value.second }?.key
    }

    private fun updateAnchor(
        currentId: Long?,
        y: Float,
        sortedIds: List<Long>,
        idToIndex: Map<Long, Int>
    ): Long? {
        var id = currentId ?: idAt(y) ?: return null
        var index = idToIndex[id] ?: return id
        while (true) {
            val bounds = itemBounds[id] ?: break
            if (y < bounds.first) {
                val prevIndex = index - 1
                if (prevIndex < 0) break
                val prevId = sortedIds[prevIndex]
                val prevBounds = itemBounds[prevId] ?: break
                val threshold = (prevBounds.first + prevBounds.second) / 2f - hysteresisPx
                if (y < threshold) {
                    id = prevId
                    index = prevIndex
                    continue
                } else {
                    break
                }
            } else if (y > bounds.second) {
                val nextIndex = index + 1
                if (nextIndex >= sortedIds.size) break
                val nextId = sortedIds[nextIndex]
                val nextBounds = itemBounds[nextId] ?: break
                val threshold = (nextBounds.first + nextBounds.second) / 2f + hysteresisPx
                if (y > threshold) {
                    id = nextId
                    index = nextIndex
                    continue
                } else {
                    break
                }
            } else {
                break
            }
        }
        return id
    }
}

