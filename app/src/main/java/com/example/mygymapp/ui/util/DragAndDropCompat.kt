package com.example.mygymapp.ui.util

import android.content.ClipData
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.awaitPointerEventScope

/**
 * Simplified drag-and-drop compatibility layer that recreates the legacy
 * helpers used throughout the app without relying on the newer experimental
 * Compose drag-and-drop APIs. A global in-memory transfer is used to bridge
 * sources and targets.
 */

/** Data container matching the older API surface. */
data class DragAndDropTransferData(val clipData: ClipData? = null)

private object DragAndDropState {
    var transferData: DragAndDropTransferData? = null
}

/**
 * Start a drag session after a long press. The provided [dataProvider] is
 * invoked once the gesture is confirmed and remains available until the
 * pointer is released.
 */
fun Modifier.dragAndDropSource(
    dataProvider: () -> DragAndDropTransferData
): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown()
        val longPress = awaitLongPressOrCancellation(down.id)
        if (longPress != null) {
            DragAndDropState.transferData = dataProvider()
            waitForUpOrCancellation()
            DragAndDropState.transferData = null
        }
    }
}

/**
 * Invoke [onDrop] when a pointer is released over this target while a drag
 * session is active. The [shouldStartDragAndDrop] callback mirrors the legacy
 * API and allows callers to veto drops.
 */
fun Modifier.dragAndDropTarget(
    shouldStartDragAndDrop: () -> Boolean,
    onDrop: (DragAndDropTransferData) -> Boolean
): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            val data = DragAndDropState.transferData
            if (data != null && shouldStartDragAndDrop() &&
                event.changes.any { it.changedToUpIgnoreConsumed() }) {
                onDrop(data)
                DragAndDropState.transferData = null
            }
        }
    }
}
