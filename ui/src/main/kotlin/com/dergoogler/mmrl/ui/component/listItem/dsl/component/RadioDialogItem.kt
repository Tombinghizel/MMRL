package com.dergoogler.mmrl.ui.component.listItem.dsl.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.AlertDialogDefaults.textContentColor
import androidx.compose.material3.AlertDialogDefaults.titleContentColor
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dergoogler.mmrl.ext.nullable
import com.dergoogler.mmrl.ui.R
import com.dergoogler.mmrl.ui.component.listItem.dsl.ListItemScope
import com.dergoogler.mmrl.ui.component.listItem.dsl.ListItemSlot
import com.dergoogler.mmrl.ui.component.listItem.dsl.ListScope
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Description
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.FromSlot
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.ProvideTitleTypography
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Start
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Title
import com.dergoogler.mmrl.ui.token.TypographyKeyTokens

data class RadioDialogItem<T>(
    val value: T,
    val title: String? = null,
    val desc: String? = null,
    val enabled: Boolean = true,
)

@Composable
fun <T> ListScope.RadioDialogItem(
    selection: T,
    enabled: Boolean = true,
    options: List<RadioDialogItem<T>>,
    onConfirm: (RadioDialogItem<T>) -> Unit,
    content: @Composable (ListItemScope.(RadioDialogItem<T>) -> Unit),
) {
    var open by remember { mutableStateOf(false) }
    
    var selectedOption by remember {
        mutableStateOf(options.find { it.value == selection } ?: RadioDialogItem(selection))
    }

    ButtonItem(
        enabled = enabled,
        onClick = {
            open = true
        },
        content = {
            content(selectedOption)

            if (open) {
                this@RadioDialogItem.AlertRadioDialog(
                    title = {
                        ProvideTitleTypography(
                            token = TypographyKeyTokens.HeadlineSmall
                        ) {
                            this@ButtonItem.FromSlot(ListItemSlot.Title) {
                                content(selectedOption)
                            }
                        }
                    },
                    selection = selection,
                    options = options,
                    onClose = {
                        open = false
                    },
                    onConfirm = {
                        selectedOption = it
                        onConfirm(it)
                    }
                )
            }
        }
    )
}

@Composable
private fun <T> ListScope.AlertRadioDialog(
    title: @Composable () -> Unit,
    selection: T,
    options: List<RadioDialogItem<T>>,
    onDismiss: (() -> Unit)? = null,
    onClose: () -> Unit,
    onConfirm: (RadioDialogItem<T>) -> Unit,
) {
    var selectedOption by remember {
        mutableStateOf(options.find { it.value == selection } ?: RadioDialogItem(selection))
    }

    val onDone: () -> Unit = {
        onConfirm(selectedOption)
        onClose()
    }

    Dialog(
        onDismissRequest = {
            if (onDismiss != null) {
                onDismiss()
                return@Dialog
            }

            onClose()
        },
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column {
                CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                    ProvideTextStyle(MaterialTheme.typography.headlineSmall) {
                        Box(
                            modifier = Modifier
                                .padding(
                                    top = 25.dp,
                                    bottom = 16.dp,
                                    start = 25.dp,
                                    end = 25.dp
                                )
                        ) {
                            title()
                        }
                    }
                }

                CompositionLocalProvider(LocalContentColor provides textContentColor) {
                    Box {
                        LazyColumn {
                            items(
                                items = options,
                            ) { option ->
                                val checked = option.value == selectedOption.value
                                val interactionSource = remember { MutableInteractionSource() }

                                if (option.title == null) return@items

                                Row(
                                    modifier = Modifier
                                        .toggleable(
                                            enabled = option.enabled,
                                            value = checked,
                                            onValueChange = {
                                                selectedOption = option
                                            },
                                            role = Role.RadioButton,
                                            interactionSource = interactionSource,
                                            indication = ripple()
                                        )
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    this@AlertRadioDialog.Item(
                                        contentPadding = PaddingValues(
                                            vertical = 8.dp,
                                            horizontal = 25.dp
                                        )
                                    ) {
                                        Title(option.title)

                                        option.desc.nullable {
                                            Description(it)
                                        }

                                        Start {
                                            RadioButton(
                                                enabled = option.enabled,
                                                selected = checked,
                                                onClick = null
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onClose) {
                        Text(stringResource(id = R.string.cancel))
                    }

                    TextButton(onClick = onDone) {
                        Text(stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}