package com.dergoogler.mmrl.ui.activity

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.datastore.model.WorkingMode
import com.dergoogler.mmrl.model.local.FeaturedManager
import com.dergoogler.mmrl.model.local.FeaturedManager.Companion.name
import com.dergoogler.mmrl.ui.component.card.Card
import com.dergoogler.mmrl.ui.component.listItem.dsl.List
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.RadioItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Icon
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Title
import com.dergoogler.mmrl.ui.component.text.BBCodeText

@Composable
fun SetupScreen(setWorkingMode: (WorkingMode) -> Unit) {
    var currentSelection: FeaturedManager? by remember { mutableStateOf(null) }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.welcome),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.select_your_platform),
                        fontSize = 20.sp,
                        modifier = Modifier.alpha(.3f)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    for (manager in FeaturedManager.managers) {
                        val interactionSource = remember { MutableInteractionSource() }
                        val selected =
                            remember(currentSelection) { currentSelection == manager }

                        Card {
                            List(
                                modifier = Modifier.relative()
                            ) {
                                RadioItem(
                                    selected = selected,
                                    interactionSource = interactionSource,
                                    onClick = {
                                        currentSelection = manager
                                    }
                                ) {
                                    Icon(painter = painterResource(manager.icon))
                                    Title(manager.name)
                                }
                            }
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        enabled = currentSelection != null,
                        onClick = {
                            Log.d("SetupScreen", "Selected: $currentSelection")

                            setWorkingMode(currentSelection!!.workingMode)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .absolutePadding(bottom = 5.dp),
                    ) {
                        Text(
                            text =
                                if (currentSelection != null) {
                                    stringResource(
                                        R.string.continue_with,
                                        currentSelection.name
                                    )
                                } else {
                                    stringResource(R.string.select)
                                }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    BBCodeText(
                        text = stringResource(R.string.setup_root_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}