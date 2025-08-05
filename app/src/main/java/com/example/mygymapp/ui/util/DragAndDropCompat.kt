package com.example.mygymapp.ui.util

import android.content.ClipData
import androidx.compose.ui.Modifier

/**
 * Minimal compatibility layer for the old drag-and-drop APIs used in the
 * project. The new Compose drag-and-drop API changed significantly and the
 * previous helpers are no longer available. To keep the project compiling we
 * provide no-op implementations that match the earlier signatures.
 */

// Data container matching the previous DragAndDropTransferData type
class DragAndDropTransferData(val clipData: ClipData?)

// No-op source modifier so existing call sites continue to compile
fun Modifier.dragAndDropSource(
    dataProvider: () -> DragAndDropTransferData
): Modifier = this

// No-op target modifier so existing call sites continue to compile
fun Modifier.dragAndDropTarget(
    shouldStartDragAndDrop: () -> Boolean,
    onDrop: (DragAndDropTransferData) -> Boolean
): Modifier = this
