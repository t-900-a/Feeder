package com.nononsenseapps.feeder.ui.compose.components

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsHeight
import com.nononsenseapps.feeder.ui.compose.theme.LocalDimens

@Composable
fun OkCancelWithContent(
    onOk: () -> Unit,
    onCancel: () -> Unit,
    okEnabled: Boolean,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        content()
        Spacer(modifier = Modifier.height(24.dp))
        OkCancelButtons(
            onOk = onOk,
            onCancel = onCancel,
            okEnabled = okEnabled,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.navigationBarsHeight())
    }
}

@Composable
@Preview(showBackground = true)
fun OkCancelButtons(
    onOk: () -> Unit = {},
    onCancel: () -> Unit = {},
    okEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val dimens = LocalDimens.current
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier.width(dimens.maxContentWidth)
    ) {
        TextButton(onClick = onCancel) {
            Text(
                text = stringResource(id = R.string.cancel)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(
            enabled = okEnabled,
            onClick = onOk
        ) {
            Text(
                text = stringResource(id = R.string.ok)
            )
        }
    }
}
