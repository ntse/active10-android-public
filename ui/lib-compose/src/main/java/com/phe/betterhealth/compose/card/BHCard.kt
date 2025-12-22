package com.phe.betterhealth.compose.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.phe.betterhealth.compose.button.BHTextButton
import com.phe.betterhealth.compose.theme.BetterHealthTheme
import com.phe.betterhealth.compose.theme.BhCardHeaderBackground

/**
 * based on Widget.BetterHealth.CardHeader
 */
@Composable
fun BHCard(
    title: String,
    description: String,
    buttonLabel: String,
    onButtonClick: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    BHCard(
        header = {
            BHCardHeader(
                title = { Text(text = title) },
                description = { Text(description) },
            )
        },
        content = content,
        footer = {
            BHCardFooter(
                buttonLabel = buttonLabel,
                onClick = onButtonClick,
            )
        },
        modifier = modifier,
    )
}

@Composable
fun BHCard(
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = modifier,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BhCardHeaderBackground)
            ) {
                header?.invoke()
            }
            content()
            if (footer != null) {
                HorizontalDivider(color = Color(0xffE4E7EB))
                footer()
            }
        }
    }
}

/**
 * based on Widget.BetterHealth.CardHeader
 */
@Composable
private fun BHCardHeader(
    title: @Composable () -> Unit,
    description: @Composable () -> Unit,
) {
    val titleDefaultStyle = MaterialTheme.typography.displaySmall
    val descriptionDefaultSTyle = MaterialTheme.typography.bodyMedium

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 10.dp)
    ) {
        ProvideTextStyle(value = titleDefaultStyle) {
            title()
        }
        Spacer(Modifier.height(2.dp))
        ProvideTextStyle(value = descriptionDefaultSTyle) {
            description()
        }
    }
}

@Composable
private fun BHCardFooter(
    buttonLabel: String,
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        BHTextButton(
            label = buttonLabel,
            onClick = onClick,
        )
    }
}

@Composable
@Preview
private fun BHCardPreview() {
    BetterHealthTheme {
        BHCard(
            title = "Beat the cravings",
            description = "From handy distractions to practical advice, a range of things to help you get through the tough times",
            content = {
                Text(text = "TEST", Modifier.padding(15.dp))
            },
            buttonLabel = "View all",
            onButtonClick = {},
        )
    }
}
