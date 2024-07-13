package com.example.democheckedsafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.democheckedsafe.ui.theme.DemoCheckedSafeTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.coroutines.launch
import com.google.gson.annotations.SerializedName

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VehicleCheckScreen()
        }
    }
}

@Preview
@Composable
fun VehicleCheckScreen() {
    var fuelLevel by remember { mutableStateOf(true) }
    var warningLights by remember { mutableStateOf(false) }
    var wipers by remember { mutableStateOf(false) }
    var mirrors by remember { mutableStateOf(false) }
    var tyres by remember { mutableStateOf(false) }
    var lights by remember { mutableStateOf(false) }
    var brakes by remember { mutableStateOf(false) }
    var exhaustSmoke by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("No") }

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("FUEL LEVEL - SUFFICIENT TO DRIVE TO GARAGE/JOB")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.Right
        ) {
            Spinner(
                items = listOf("Yes", "No"),
                selectedItem = selectedOption,
                onItemSelected = { selectedOption = it }
            )
        }

        HorizontalDivider()
        CheckItem("Dashboard any warning lights", warningLights) { warningLights = it }
        HorizontalDivider()
        CheckItem("Wipers / Washers", wipers) { wipers = it }
        HorizontalDivider()
        CheckItem("Visual Check of Mirrors and Glass", mirrors) { mirrors = it }
        HorizontalDivider()
        CheckItem(
            "Visual check of Tyres, condition, tread depth, security of wheel nuts",
            tyres
        ) { tyres = it }
        HorizontalDivider()
        CheckItem("Lights/indicators lenses clean and bulbs operating", lights) { lights = it }
        HorizontalDivider()
        CheckItem("Check Brakes ABS/EBS", brakes) { brakes = it }
        HorizontalDivider()
        CheckItem("Check excessive exhaust Smoke", exhaustSmoke) { exhaustSmoke = it }
        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Handle previous */ },
                modifier = Modifier.width(120.dp)
            ) {
                Text("Previous")
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        sendCheckData(
                            CheckData(
                                fuelLevel = fuelLevel,
                                warningLights = warningLights,
                                wipers = wipers,
                                mirrors = mirrors,
                                tyres = tyres,
                                lights = lights,
                                brakes = brakes,
                                exhaustSmoke = exhaustSmoke,
                                selectedOption = selectedOption
                            )
                        )
                    }
                },
                modifier = Modifier.width(120.dp)
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun CheckItem(text: String, isPass: Boolean, onStatusChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Pass", modifier = Modifier.padding(end = 8.dp))
            RadioButton(
                selected = isPass,
                onClick = { onStatusChange(true) },
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Fail", modifier = Modifier.padding(end = 4.dp))
            RadioButton(
                selected = !isPass,
                onClick = { onStatusChange(false) }
            )
        }
    }
}

// Define the API interface
interface ApiService {
    @POST("demo")
    suspend fun postCheckData(@Body checkData: CheckData): retrofit2.Response<Unit>
}

// Create Retrofit instance
val retrofit = Retrofit.Builder()
    .baseUrl("http://192.168.0.100:8080/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(ApiService::class.java)

// Function to send POST request
suspend fun sendCheckData(checkData: CheckData) {
    try {
        val response = apiService.postCheckData(checkData)
        if (response.isSuccessful) {
            println("Data sent successfully")
        } else {
            println("Error: ${response.code()}")
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

data class CheckData(
    @SerializedName("fuelLevel") val fuelLevel: Boolean,
    @SerializedName("warningLights") val warningLights: Boolean,
    @SerializedName("wipers") val wipers: Boolean,
    @SerializedName("mirrors") val mirrors: Boolean,
    @SerializedName("tyres") val tyres: Boolean,
    @SerializedName("lights") val lights: Boolean,
    @SerializedName("brakes") val brakes: Boolean,
    @SerializedName("exhaustSmoke") val exhaustSmoke: Boolean,
    @SerializedName("selectedOption") val selectedOption: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}