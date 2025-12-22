package com.phe.betterhealth.compose.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.phe.betterhealth.compose.R
import com.phe.betterhealth.compose.theme.BetterHealthTheme
import com.phe.betterhealth.compose.theme.StrongBlue

@Composable
fun BHTextButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = StrongBlue,
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            painter = painterResource(R.drawable.bh_indicator_right),
            contentDescription = null,
            tint = StrongBlue,
            modifier = Modifier
                .size(24.dp)
                .padding(5.dp)
        )
    }
}

@Composable
@Preview
private fun BHTextButtonPreview() {
    BetterHealthTheme {
        BHTextButton(
            label = "View all missions",
            onClick = {},
        )
    }
}
