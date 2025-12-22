package com.phe.betterhealth.compose.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.phe.betterhealth.compose.theme.BetterHealthTheme
import com.phe.betterhealth.compose.theme.colorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BHOutlinedButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val cornerSize = 40.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Disable ripple effect (to match the rest of the buttons in Smoke XML)
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        OutlinedButton(
            onClick = onClick,
            interactionSource = interactionSource,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(cornerSize),
            contentPadding = PaddingValues(vertical = 24.dp, horizontal = 16.dp),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
            ),
            modifier = modifier
                .then(
                    if (isPressed || isFocused) {
                        Modifier.drawWithContent {
                            drawContent()
                            val inset = 5.dp.toPx()
                            drawRoundRect(
                                color = colorScheme.primary,
                                style = Stroke(width = 2.dp.toPx()),
                                topLeft = Offset(inset, inset),
                                size = Size(
                                    width = size.width - (inset * 2),
                                    height = size.height - (inset * 2)
                                ),
                                cornerRadius = CornerRadius(cornerSize.toPx())
                            )
                        }
                    } else Modifier
                )
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .semantics {
                        if (contentDescription != null) {
                            this.contentDescription = contentDescription
                        }
                    }
            )
        }
    }
}

@Composable
@Preview
private fun BHOutlinedButtonPreview() {
    BetterHealthTheme {
        BHOutlinedButton(
            label = "Turn on notifications",
            contentDescription = "This is a content description",
            onClick = {},
        )
    }
}
