package com.example.mygymapp.ui.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource as foundationDragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget as foundationDragAndDropTarget
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.toAndroidDragEvent

/**
 * Compatibility layer adapting the old drag-and-drop helpers used in the
 * project to the new Compose drag-and-drop APIs. These wrappers delegate to the
 * official implementations so existing call sites continue to work without
 * rewriting call sites.
 */

// Expose the new TransferData type under the legacy name
typealias DragAndDropTransferData = androidx.compose.ui.draganddrop.DragAndDropTransferData

// Adapter for the old dataProvider signature
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.dragAndDropSource(
    dataProvider: () -> DragAndDropTransferData
): Modifier = foundationDragAndDropSource {
    startTransfer(dataProvider())
}

// Adapter for the old onDrop/shouldStartDragAndDrop signature
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.dragAndDropTarget(
    shouldStartDragAndDrop: () -> Boolean,
    onDrop: (DragAndDropTransferData) -> Boolean
): Modifier = foundationDragAndDropTarget(
    shouldStartDragAndDrop = { _: DragAndDropEvent -> shouldStartDragAndDrop() },
    target = object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            val clipData = event.toAndroidDragEvent().clipData ?: return false
            return onDrop(DragAndDropTransferData(clipData))
        }
    }
)
