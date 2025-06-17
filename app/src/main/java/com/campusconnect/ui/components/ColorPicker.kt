package com.campusconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta,
        Color.LightGray, Color.Gray, Color.DarkGray, Color.Black
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color)
                    .clickable { onColorSelected(color) }
                    .padding(2.dp)
            )
        }
    }
}
