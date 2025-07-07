package com.campusconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier
) {
    val colors = listOf(
        Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8),
        Color(0xFF64B5F6), Color(0xFF4DD0E1), Color(0xFF81C784),
        Color(0xFFFFD54F), Color(0xFFA1887F), Color(0xFF90A4AE)
    )

    Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color, shape = MaterialTheme.shapes.small)
                    .clickable { onColorSelected(color) }
            )
        }
    }
}
