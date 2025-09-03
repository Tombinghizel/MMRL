package com.dergoogler.mmrl.ui.component.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dergoogler.mmrl.ext.nullable
import com.dergoogler.mmrl.ui.R

@Composable
fun ConfirmDialog(
    title: @Composable (RowScope.() -> Unit)?,
    description: (@Composable DialogContainerContentScope.() -> Unit)?,
    confirmText: @Composable () -> Unit = {
        Text(text = stringResource(R.string.confirm))
    },
    closeText: @Composable () -> Unit = {
        Text(text = stringResource(R.string.cancel))
    },
    onClose: (() -> Unit)? = null,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit = onClose ?: onConfirm,
) {
    DialogContainer(
        onDismissRequest = onDismissRequest,
        title = title,
        content = description,
        buttons = {
            onClose.nullable {
                TextButton(
                    onClick = it
                ) {
                    closeText()
                }
            }

            TextButton(
                onClick = onConfirm
            ) {
                confirmText()
            }
        }
    )
}

@Composable
fun ConfirmDialog(
    title: String,
    description: String,
    confirmText: String = stringResource(id = R.string.confirm),
    closeText: String = stringResource(id = R.string.cancel),
    onClose: (() -> Unit)? = null,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit = onClose ?: onConfirm,
) = ConfirmDialog(
    onDismissRequest = onDismissRequest,
    title = {
        Text(text = title)
    },
    description = {
        Text(text = description)
    },
    confirmText = {
        Text(text = confirmText)
    },
    closeText = {
        Text(text = closeText)
    },
    onClose = onClose,
    onConfirm = onConfirm
)

@Composable
fun ConfirmDialog(
    @StringRes title: Int,
    @StringRes description: Int,
    @StringRes confirmText: Int = R.string.confirm,
    @StringRes closeText: Int = R.string.cancel,
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit = onClose,
) = ConfirmDialog(
    onDismissRequest = onDismissRequest,
    title = {
        Text(text = stringResource(title))
    },
    description = {
        Text(text = stringResource(description))
    },
    confirmText = {
        Text(text = stringResource(confirmText))
    },
    closeText = {
        Text(text = stringResource(closeText))
    },
    onClose = onClose,
    onConfirm = onConfirm
)