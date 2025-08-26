package com.dergoogler.mmrl.ui.screens.repository

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.ui.component.Cover
import com.dergoogler.mmrl.ui.component.LabelItem
import com.dergoogler.mmrl.ui.component.text.TextWithIcon
import com.dergoogler.mmrl.ui.component.card.Card
import com.dergoogler.mmrl.ui.providable.LocalUserPreferences
import com.dergoogler.mmrl.ext.fadingEdge
import com.dergoogler.mmrl.ext.isNotNullOrEmpty
import com.dergoogler.mmrl.ext.nullable
import com.dergoogler.mmrl.ui.component.LabelItemDefaults
import com.dergoogler.mmrl.ui.component.text.TextWithIconDefaults
import com.dergoogler.mmrl.ui.providable.LocalOnlineModule
import com.dergoogler.mmrl.ui.providable.LocalOnlineModuleState
import com.dergoogler.mmrl.utils.toFormattedDateSafely

@Composable
fun ModuleItemDetailed(
    alpha: Float = 1f,
    onClick: () -> Unit = {},
    decoration: TextDecoration = TextDecoration.None,
    enabled: Boolean = true,
) {
    val module = LocalOnlineModule.current
    val state = LocalOnlineModuleState.current

    val userPreferences = LocalUserPreferences.current
    val menu = userPreferences.repositoryMenu
    val hasLabel =
        (state.hasLicense && menu.showLicense) || state.installed || (module.track.hasAntifeatures && menu.showAntiFeatures)
    val isVerified = module.isVerified && menu.showVerified

    Card(
        enabled = enabled,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.relative()
        ) {
            module.cover.nullable(menu.showCover) {
                if (it.isNotEmpty()) {
                    Cover(
                        modifier = Modifier.fadingEdge(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black,
                                ),
                                startY = Float.POSITIVE_INFINITY,
                                endY = 0f
                            ),
                        ),
                        url = it,
                    )
                }
            }

            Row(
                modifier = Modifier.padding(all = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .alpha(alpha = alpha)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {


                    TextWithIcon(
                        style = TextWithIconDefaults.style.copy(
                            textStyle = MaterialTheme.typography.titleSmall.copy(
                                textDecoration = decoration
                            ),
                            iconTint = MaterialTheme.colorScheme.surfaceTint,
                            iconScaling = 1.0f,
                            rightIcon = true,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        ),
                        text = module.name,
                        icon = isVerified nullable R.drawable.rosette_discount_check,
                    )

                    Text(
                        text = stringResource(
                            id = R.string.module_version_author,
                            module.versionDisplay,
                            module.author
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        textDecoration = decoration,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (menu.showUpdatedTime) {
                        Text(
                            text = stringResource(
                                id = R.string.module_update_at,
                                state.lastUpdated.toFormattedDateSafely
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            textDecoration = decoration,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Text(
                modifier = Modifier
                    .alpha(alpha = alpha)
                    .padding(end = 16.dp, bottom = 16.dp, start = 16.dp),
                text = module.description
                    ?: stringResource(R.string.view_module_no_description),
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.apply {
                    if (module.description.isNullOrBlank()) {
                        copy(
                            fontStyle = FontStyle.Italic
                        )
                    }
                },
                textDecoration = decoration,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.weight(1f))

            if (hasLabel) {
                Row(
                    modifier = Modifier
                        .padding(end = 16.dp, bottom = 16.dp, start = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    module.categories.nullable {
                        if (it.isNotNullOrEmpty()) {
                            LabelItem(
                                icon = R.drawable.category,
                                text = it.first(),
                                style = LabelItemDefaults.style.copy(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }

                    module.license.nullable(menu.showLicense) {
                        LabelItem(
                            icon = R.drawable.tag,
                            text = it
                        )
                    }

                    module.track.antifeatures.nullable(menu.showAntiFeatures) {
                        if (it.isNotEmpty()) {
                            LabelItem(
                                icon = R.drawable.alert_triangle,
                                text = stringResource(id = R.string.view_module_antifeatures),
                                style = LabelItemDefaults.style.copy(
                                    containerColor = MaterialTheme.colorScheme.onTertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                )
                            )
                        }
                    }

                    when {
                        state.updatable ->
                            LabelItem(
                                text = stringResource(id = R.string.module_new),
                                style = LabelItemDefaults.style.copy(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                )
                            )

                        state.installed ->
                            LabelItem(text = stringResource(id = R.string.module_installed))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
