package com.dergoogler.mmrl.ui.component.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dergoogler.mmrl.ext.nullable
import com.dergoogler.mmrl.ui.token.DialogContainerTokens
import com.dergoogler.mmrl.ui.token.value

@Composable
fun DialogContainer(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    style: DialogContainerStyle = DialogContainerDefaults.style,
    contentPadding: DialogContentPadding = DialogContainerDefaults.contentPadding,
    title: (@Composable RowScope.() -> Unit)? = null,
    buttons: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Surface(
            modifier = Modifier.border(
                style.borderWidth,
                style.containerOutline,
                style.shape
            ),
            shape = style.shape,
            color = style.containerColor,
            tonalElevation = style.tonalElevation
        ) {
            Column {
                title.nullable {
                    CompositionLocalProvider(LocalContentColor provides style.titleContentColor) {
                        ProvideTextStyle(MaterialTheme.typography.headlineSmall) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(contentPadding.title),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                it()
                            }
                        }
                    }
                }

                CompositionLocalProvider(LocalContentColor provides style.textContentColor) {
                    Box(content = content)
                }

                buttons.nullable {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(contentPadding.buttons),
                        horizontalArrangement = Arrangement.End
                    ) {
                        it()
                    }
                }
            }
        }
    }
}

data class DialogContainerStyle(
    val borderWidth: Dp,
    val shape: Shape,
    val containerOutline: Color,
    val containerColor: Color,
    val iconContentColor: Color,
    val titleContentColor: Color,
    val textContentColor: Color,
    val tonalElevation: Dp,
)

data class DialogContentPadding(
    val title: PaddingValues,
    val buttons: PaddingValues
)

object DialogContainerDefaults {
    val contentPadding = DialogContentPadding(
        title = PaddingValues(
            top = 25.dp,
            bottom = 16.dp,
            start = 25.dp,
            end = 25.dp
        ),
        buttons = PaddingValues(
            vertical = 16.dp,
            horizontal = 24.dp
        )
    )

    val style
        @Composable get() = DialogContainerStyle(
            borderWidth = Dp.Hairline,
            shape = DialogContainerTokens.ContainerShape.value,
            containerOutline = DialogContainerTokens.ContainerOutline.value,
            containerColor = DialogContainerTokens.ContainerColor.value,
            iconContentColor = DialogContainerTokens.IconColor.value,
            titleContentColor = DialogContainerTokens.HeadlineColor.value,
            textContentColor = DialogContainerTokens.SupportingTextColor.value,
            tonalElevation = 0.dp
        )
}