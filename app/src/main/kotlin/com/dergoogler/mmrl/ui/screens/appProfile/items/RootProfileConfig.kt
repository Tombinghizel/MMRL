package com.dergoogler.mmrl.ui.screens.appProfile.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import com.dergoogler.mmrl.R
import com.dergoogler.mmrl.ext.nullable
import com.dergoogler.mmrl.platform.ksu.Capabilities
import com.dergoogler.mmrl.platform.ksu.Groups
import com.dergoogler.mmrl.platform.ksu.Profile
import com.dergoogler.mmrl.platform.ksu.Profile.Namespace
import com.dergoogler.mmrl.ui.component.LabelItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.ListScope
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.ButtonItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.Item
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.RadioDialogItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.Section
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.TextEditDialogItem
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Description
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.DialogSupportingText
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Labels
import com.dergoogler.mmrl.ui.component.listItem.dsl.component.item.Title
import com.dergoogler.mmrl.utils.SePolicy.isSepolicyValid
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.input.InputDialog
import com.maxkeppeler.sheets.input.models.InputHeader
import com.maxkeppeler.sheets.input.models.InputSelection
import com.maxkeppeler.sheets.input.models.InputTextField
import com.maxkeppeler.sheets.input.models.InputTextFieldType
import com.maxkeppeler.sheets.input.models.ValidationResult
import com.maxkeppeler.sheets.list.ListDialog
import com.maxkeppeler.sheets.list.models.ListOption
import com.maxkeppeler.sheets.list.models.ListSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScope.RootProfileConfig(
    modifier: Modifier = Modifier,
    fixedName: Boolean,
    profile: Profile,
    onProfileChange: (Profile) -> Unit,
) {
//    Column(modifier = modifier) {
//        if (!fixedName) {
//            OutlinedTextField(
//                label = { Text(stringResource(R.string.profile_name)) },
//                value = profile.name,
//                onValueChange = { onProfileChange(profile.copy(name = it)) }
//            )
//        }

    Section(
        title = stringResource(R.string.profile_custom)
    ) {
        RadioDialogItem(
            selection = profile.namespace,
            options = NamespaceOptions,
            onConfirm = {
                onProfileChange(profile.copy(namespace = it.value))
            }
        ) {
            Title(R.string.profile_namespace)
            it.title.nullable { t ->
                Description(t)
            }
        }

        UidPanel(
            uid = profile.uid,
            label = "User Identifier (UID)",
            onUidChange = {
                onProfileChange(
                    profile.copy(
                        uid = it,
                        rootUseDefault = false
                    )
                )
            }
        )

        UidPanel(
            uid = profile.gid,
            label = "Group Identifier (GID)",
            onUidChange = {
                onProfileChange(
                    profile.copy(
                        gid = it,
                        rootUseDefault = false
                    )
                )
            }
        )

    }

    val selectedGroups = profile.groups.ifEmpty { listOf(0) }.let { e ->
        e.mapNotNull { g ->
            Groups.entries.find { it.gid == g }
        }
    }

    GroupsPanel(selectedGroups) {
        onProfileChange(
            profile.copy(
                groups = it.map { group -> group.gid }.ifEmpty { listOf(0) },
                rootUseDefault = false
            )
        )
    }

    val selectedCaps = profile.capabilities.mapNotNull { e ->
        Capabilities.entries.find { it.cap == e }
    }

    CapsPanel(selectedCaps) {
        onProfileChange(
            profile.copy(
                capabilities = it.map { cap -> cap.cap },
                rootUseDefault = false
            )
        )
    }

    SELinuxPanel(
        profile = profile,
        onSELinuxChange = { domain, rules ->
            onProfileChange(
                profile.copy(
                    context = domain,
                    rules = rules,
                    rootUseDefault = false
                )
            )
        }
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListScope.GroupsPanel(
    selected: List<Groups>,
    closeSelection: (selection: Set<Groups>) -> Unit,
) {
    var selectGroupsDialog by remember { mutableStateOf(false) }
    if (selectGroupsDialog) {
        val groups = Groups.entries.toTypedArray().sortedWith(
            compareBy<Groups> { if (selected.contains(it)) 0 else 1 }
                .then(compareBy {
                    when (it) {
                        Groups.ROOT -> 0
                        Groups.SYSTEM -> 1
                        Groups.SHELL -> 2
                        else -> Int.MAX_VALUE
                    }
                })
                .then(compareBy { it.name })

        )
        val options = groups.map { value ->
            ListOption(
                titleText = value.display,
                subtitleText = value.desc,
                selected = selected.contains(value),
            )
        }


        val selection = HashSet(selected)
        ListDialog(
            state = rememberUseCaseState(
                visible = true, onFinishedRequest = {
                    closeSelection(selection)
                },
                onCloseRequest = {
                    selectGroupsDialog = false
                }
            ),
            header = Header.Default(
                title = stringResource(R.string.profile_groups),
            ),
            selection = ListSelection.Multiple(
                showCheckBoxes = true,
                options = options,
                maxChoices = 32, // Kernel only supports 32 groups at most
            ) { indecies, _ ->
                // Handle selection
                selection.clear()
                indecies.forEach { index ->
                    val group = groups[index]
                    selection.add(group)
                }
            }
        )
    }

    ButtonItem(
        onClick = {
            selectGroupsDialog = true
        }
    ) {
        Title(R.string.profile_groups)
        Labels {
            selected.forEach {
                LabelItem(it.display)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListScope.CapsPanel(
    selected: Collection<Capabilities>,
    closeSelection: (selection: Set<Capabilities>) -> Unit,
) {
    var selectCapabilitiesDialog by remember { mutableStateOf(false) }
    if (selectCapabilitiesDialog) {
        val caps = Capabilities.entries.toTypedArray().sortedWith(
            compareBy<Capabilities> { if (selected.contains(it)) 0 else 1 }
                .then(compareBy { it.name })
        )
        val options = caps.map { value ->
            ListOption(
                titleText = value.display,
                subtitleText = value.desc,
                selected = selected.contains(value),
            )
        }

        val selection = HashSet(selected)
        ListDialog(
            state = rememberUseCaseState(
                visible = true, onFinishedRequest = {
                    closeSelection(selection)
                },
                onCloseRequest = {
                    selectCapabilitiesDialog = false
                }
            ),
            header = Header.Default(
                title = stringResource(R.string.profile_capabilities),
            ),
            selection = ListSelection.Multiple(
                showCheckBoxes = true,
                options = options
            ) { indecies, _ ->
                // Handle selection
                selection.clear()
                indecies.forEach { index ->
                    val group = caps[index]
                    selection.add(group)
                }
            }
        )
    }

    ButtonItem(
        onClick = {
            selectCapabilitiesDialog = true
        }
    ) {
        Title(R.string.profile_capabilities)
        Labels {
            selected.forEach {
                LabelItem(it.display)
            }
        }
    }
}

@Composable
private fun ListScope.UidPanel(uid: Int, label: String, onUidChange: (Int) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var lastValidUid by remember {
        mutableIntStateOf(uid)
    }

    TextEditDialogItem(
        value = uid.toString(),
        onValid = {
            isTextValidUid(it)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
        onConfirm = {
            if (it.isEmpty()) {
                onUidChange(0)
                return@TextEditDialogItem
            }
            val valid = isTextValidUid(it)

            val targetUid = if (valid) it.toInt() else lastValidUid
            if (valid) {
                lastValidUid = it.toInt()
            }

            onUidChange(targetUid)
        }
    ) {
        Title(label)
        Description(it.value)
        if (it.isError) {
            DialogSupportingText("Invalid Input!")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListScope.SELinuxPanel(
    profile: Profile,
    onSELinuxChange: (domain: String, rules: String) -> Unit,
) {
    var editSELinuxDialog by remember { mutableStateOf(false) }
    if (editSELinuxDialog) {
        var domain by remember { mutableStateOf(profile.context) }
        var rules by remember { mutableStateOf(profile.rules) }

        val inputOptions = listOf(
            InputTextField(
                text = domain,
                header = InputHeader(
                    title = stringResource(id = R.string.profile_selinux_domain),
                ),
                type = InputTextFieldType.OUTLINED,
                required = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next
                ),
                resultListener = {
                    domain = it ?: ""
                },
                validationListener = { value ->
                    // value can be a-zA-Z0-9_
                    val regex = Regex("^[a-z_]+:[a-z0-9_]+:[a-z0-9_]+(:[a-z0-9_]+)?$")
                    if (value?.matches(regex) == true) ValidationResult.Valid
                    else ValidationResult.Invalid("Domain must be in the format of \"user:role:type:level\"")
                }
            ),
            InputTextField(
                text = rules,
                header = InputHeader(
                    title = stringResource(id = R.string.profile_selinux_rules),
                ),
                type = InputTextFieldType.OUTLINED,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                ),
                singleLine = false,
                resultListener = {
                    rules = it ?: ""
                },
                validationListener = { value ->
                    if (isSepolicyValid(value)) ValidationResult.Valid
                    else ValidationResult.Invalid("SELinux rules is invalid!")
                }
            )
        )

        InputDialog(
            state = rememberUseCaseState(
                visible = true,
                onFinishedRequest = {
                    onSELinuxChange(domain, rules)
                },
                onCloseRequest = {
                    editSELinuxDialog = false
                }),
            header = Header.Default(
                title = stringResource(R.string.profile_selinux_context),
            ),
            selection = InputSelection(
                input = inputOptions,
                onPositiveClick = { result ->
                    // Handle selection
                },
            )
        )
    }

    Item {
        Title {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        editSELinuxDialog = true
                    },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                label = { Text(text = stringResource(R.string.profile_selinux_context)) },
                value = profile.context,
                onValueChange = { }
            )
        }
    }
}


@get:Composable
private val NamespaceOptions: List<RadioDialogItem<Int>>
    get() {
        return enumValues<Namespace>().map {
            val title = when (it) {
                Namespace.INHERITED -> stringResource(R.string.profile_namespace_inherited)
                Namespace.GLOBAL -> stringResource(R.string.profile_namespace_global)
                Namespace.INDIVIDUAL -> stringResource(R.string.profile_namespace_individual)
            }

            RadioDialogItem(
                value = it.ordinal,
                title = title
            )
        }
    }

private fun isTextValidUid(text: String): Boolean {
    return text.isNotEmpty() && text.isDigitsOnly() && text.toInt() >= 0 && text.toInt() <= Int.MAX_VALUE
}