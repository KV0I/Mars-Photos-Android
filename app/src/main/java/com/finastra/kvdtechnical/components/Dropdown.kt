package com.finastra.kvdtechnical.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DropDown(list: List<String>, value: String, onClick: (it:String) -> Unit){


    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = {
                isExpanded = !isExpanded
            }
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = {
                    onClick(it)
                },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                label = { Text(text = "Rover") },

                )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            ) {
                list.forEachIndexed { _, item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            onClick(item)
                            isExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }


        }
        Text(
            text = "Selected: $value",
            color = MaterialTheme.colorScheme.onBackground

        )
    }

}