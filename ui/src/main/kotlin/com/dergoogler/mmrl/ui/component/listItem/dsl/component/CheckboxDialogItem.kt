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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
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

data class CheckboxItem<T>(
    val value: T,
    val checked: Boolean = false,
    val title: String? = null,
    val desc: String? = null,
    val enabled: Boolean = true,
)

@Composable
fun <T> ListScope.CheckboxDialogItem(
    enabled: Boolean = true,
    multiple: Boolean = false,
    maxChoices: Int = Int.MAX_VALUE,
    selection: T? = null, // Single selection for non-multiple mode
    selections: List<T> = emptyList(), // Multiple selections for multiple mode
    options: List<CheckboxItem<T>>,
    onConfirm: (List<CheckboxItem<T>>) -> Unit, // Changed to return list
    content: @Composable (ListItemScope.(List<CheckboxItem<T>>) -> Unit),
) {
    var open by remember { mutableStateOf(false) }

    // Initialize selected options based on mode
    val initialSelectedOptions = remember(selection, selections, options) {
        if (multiple) {
            options.filter { option -> selections.contains(option.value) }
        } else {
            selection?.let { sel ->
                options.find { it.value == sel }?.let { listOf(it) }
            } ?: emptyList()
        }
    }

    val selectedOptions by remember {
        mutableStateOf(initialSelectedOptions)
    }

    ButtonItem(
        enabled = enabled,
        onClick = {
            open = true
        },
        content = {
            content(selectedOptions)

            if (open) {
                this@CheckboxDialogItem.AlertCheckboxDialog(
                    title = {
                        ProvideTitleTypography(
                            token = TypographyKeyTokens.HeadlineSmall
                        ) {
                            this@ButtonItem.FromSlot(ListItemSlot.Title) {
                                content(selectedOptions)
                            }
                        }
                    },
                    multiple = multiple,
                    maxChoices = maxChoices,
                    initialSelections = selectedOptions.map { it.value },
                    options = options,
                    onClose = {
                        open = false
                    },
                    onConfirm = { confirmedOptions ->
                        onConfirm(confirmedOptions)
                    }
                )
            }
        }
    )
}

@Composable
private fun <T> ListScope.AlertCheckboxDialog(
    title: @Composable () -> Unit,
    multiple: Boolean = false,
    maxChoices: Int = Int.MAX_VALUE,
    initialSelections: List<T> = emptyList(),
    options: List<CheckboxItem<T>>,
    onDismiss: (() -> Unit)? = null,
    onClose: () -> Unit,
    onConfirm: (List<CheckboxItem<T>>) -> Unit,
) {
    var selectedValues by remember {
        mutableStateOf(initialSelections.toSet())
    }

    val selectedOptions = options.filter { selectedValues.contains(it.value) }
    val hasReachedMaxChoices = selectedValues.size >= maxChoices

    val onDone: () -> Unit = {
        onConfirm(selectedOptions)
        onClose()
    }

    val canSelectMore = !hasReachedMaxChoices || !multiple

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

                if (multiple && maxChoices != Int.MAX_VALUE) {
                    CompositionLocalProvider(LocalContentColor provides textContentColor) {
                        Text(
                            text = "Selected: ${selectedValues.size}/$maxChoices",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 25.dp, vertical = 4.dp)
                        )
                    }
                }

                CompositionLocalProvider(LocalContentColor provides textContentColor) {
                    Box {
                        LazyColumn {
                            stickyHeader {  }

                            items(
                                items = options,
                                key = { it.value.hashCode() }
                            ) { option ->
                                val isChecked = selectedValues.contains(option.value)
                                val interactionSource = remember { MutableInteractionSource() }

                                if (option.title == null) return@items

                                val isOptionEnabled = option.enabled &&
                                        (isChecked || canSelectMore || !multiple)

                                Row(
                                    modifier = Modifier
                                        .toggleable(
                                            enabled = isOptionEnabled,
                                            value = isChecked,
                                            onValueChange = { checked ->
                                                if (multiple) {
                                                    selectedValues = if (checked) {
                                                        if (selectedValues.size < maxChoices) {
                                                            selectedValues + option.value
                                                        } else {
                                                            selectedValues // Don't add if at max
                                                        }
                                                    } else {
                                                        selectedValues - option.value
                                                    }
                                                } else {
                                                    selectedValues = if (checked) {
                                                        setOf(option.value)
                                                    } else {
                                                        emptySet()
                                                    }
                                                }
                                            },
                                            role = Role.Checkbox,
                                            interactionSource = interactionSource,
                                            indication = ripple()
                                        )
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    this@AlertCheckboxDialog.Item(
                                        contentPadding = PaddingValues(
                                            vertical = 8.dp,
                                            horizontal = 25.dp
                                        )
                                    ) {
                                        Title(option.title)

                                        option.desc?.let {
                                            Description(it)
                                        }

                                        Start {
                                            Checkbox(
                                                enabled = isOptionEnabled,
                                                checked = isChecked,
                                                onCheckedChange = null
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

                    TextButton(
                        onClick = onDone,
                        enabled = if (multiple) selectedValues.isNotEmpty() else true
                    ) {
                        Text(stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}
