package com.example.mygymapp.ui.util

import android.content.ClipData
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.awaitPointerEvent
import kotlinx.coroutines.yield

/**
 * Simplified drag-and-drop compatibility layer that recreates the legacy
 * helpers used throughout the app without relying on the newer experimental
 * Compose drag-and-drop APIs. A global in-memory transfer is used to bridge
 * sources and targets.
 */

/** Data container matching the older API surface. */
data class DragAndDropTransferData(val clipData: ClipData? = null)

private data class DragSession(
    val data: DragAndDropTransferData,
    val pointerId: PointerId
)

private object DragAndDropState {
    var session: DragSession? = null
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
            val session = DragSession(dataProvider(), longPress.id)
            DragAndDropState.session = session
            waitForUpOrCancellation()
            // Yield to allow targets to process the up event before clearing.
            yield()
            if (DragAndDropState.session === session) {
                DragAndDropState.session = null
            }
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
    while (true) {
        val event = awaitPointerEvent()
        val session = DragAndDropState.session
        if (session != null && shouldStartDragAndDrop()) {
            val change = event.changes.find {
                it.id == session.pointerId && it.changedToUpIgnoreConsumed()
            }
            if (change != null && onDrop(session.data)) {
                DragAndDropState.session = null
            }
        }
    }
}