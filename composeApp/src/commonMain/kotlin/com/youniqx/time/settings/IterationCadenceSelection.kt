package com.youniqx.time.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.youniqx.time.gitlab.models.NamespaceQuery
import kotlin.collections.flatMap
import kotlin.collections.orEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IterationCadenceSelection(
    iterationCadence: IterationCadence?,
    namespaces: NamespaceQuery.Data?,
    onIterationCadenceChange: (iterationCadence: IterationCadence?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val iterationCadences = namespaces?.frecentGroups.orEmpty().flatMap {
        it.groupWithIterationCadences.iterationCadences?.nodes?.filterNotNull()
            ?.map { iterationCadence -> iterationCadence to it.groupWithIterationCadences.fullPath }.orEmpty()
    }
    ExposedDropdownMenuBox(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 12.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = Modifier.fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            value = iterationCadences.firstOrNull { it.first.id == iterationCadence?.id }?.first?.title.orEmpty(),
            onValueChange = {},
            readOnly = true,
            maxLines = 1,
            label = { Text("Iteration Cadence") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (namespaces == null) CircularProgressIndicator()
            iterationCadences.forEach {
                DropdownMenuItem(
                    text = { Text(it.first.title) },
                    onClick = {
                        onIterationCadenceChange(
                             IterationCadence(
                                namespaceFullPath = it.second,
                                id = it.first.id.toString()
                            )
                        )
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}