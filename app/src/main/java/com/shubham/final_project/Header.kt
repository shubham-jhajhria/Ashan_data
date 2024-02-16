package com.shubham.final_project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubham.final_project.components.InputField

@Preview
@Composable
fun CreateHeader() {
    var sidebarVisible by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {


        Surface (modifier = Modifier,
            color = Color(0xFF9F7FA5)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aashan Data Collection",
                    style = TextStyle(color = Color(0xFFFFFFFF), fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(12.dp)
                )

                Button(onClick = { sidebarVisible = !sidebarVisible },Modifier.size(50.dp)) {

                }


            }

        }
    }
    if (sidebarVisible) {
        Sidebar()
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Sidebar(onValChange: (String, String) -> Unit = { _, _ -> }) {
    val asanNameState = remember { mutableStateOf("") }
    val validState = remember(asanNameState.value) {
        asanNameState.value.trim().isNotEmpty()
    }
    val timeState = remember { mutableStateOf("") }
    val validtimeState = remember(timeState.value) {
        timeState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .height(300.dp)
            .width(200.dp)
            .padding(16.dp)
            .background(Color(0xFF389ECC)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
            InputField(
                modifier = Modifier.fillMaxWidth(),
                valueState = asanNameState,
                labelId = "Enter Aasan Label",
                leadingIcon = Icons.Rounded.Create,
                leadingIconContentDescription = "Aasan Label",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(asanNameState.value.trim(), timeState.value.trim()) // Logging both values
                    keyboardController?.hide()
                }
            )
        }
        Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
            InputField(
                modifier = Modifier.fillMaxWidth(),
                valueState = timeState,
                labelId = "Time In Seconds",
                leadingIcon = Icons.Rounded.CheckCircle,
                leadingIconContentDescription = "Timer Label",
                enabled = true,
                isSingleLine = true,
                keyboardType = KeyboardType.Number,
                onAction = KeyboardActions {
                    if (!validtimeState) return@KeyboardActions
                    onValChange(asanNameState.value.trim(), timeState.value.trim()) // Logging both values
                    keyboardController?.hide()
                }

            )
        }
        if(validState && validtimeState){
            Row(modifier = Modifier.padding( bottom = 10.dp), horizontalArrangement = Arrangement.Center){
                CreateCircle()
            }
        }
        else{
            Box{

            }
        }
    }
}
@Composable
fun CreateCircle() {
    Surface(
        modifier = Modifier.size(150.dp, 50.dp),
        shape = CircleShape,
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Card(
            colors = CardDefaults.cardColors(Color(0xFF525B88)),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Start", style = TextStyle(color = Color(0xFFFFFFFF)))
            }
        }
    }
}