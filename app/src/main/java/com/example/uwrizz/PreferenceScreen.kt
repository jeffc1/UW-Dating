import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.uwrizz.BasicUserInfo
import com.example.uwrizz.R
import com.example.uwrizz.UserPreference
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color

@Composable
fun MultiSelect(
    options: List<String>,
    selectedOptions: List<String>,
    onOptionSelected: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    isError: Boolean = false, // Add this parameter
    errorMessage: String = "" // And this

) {
    var expanded by remember { mutableStateOf(false) }
    val red = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color(0xFFE1474E),
        unfocusedBorderColor = Color(0xFFE1474E)
    )

    Column(modifier = modifier) {
        androidx.compose.material.OutlinedTextField(
            value = selectedOptions.joinToString(", "),
            onValueChange = { },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Close dropdown" else "Open dropdown",
                    Modifier.clickable { expanded = !expanded }
                )
            },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            colors = red
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedOptions.contains(option),
                            onClick = {
                                onOptionSelected(
                                    option,
                                    !selectedOptions.contains(option)
                                )
                            }
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedOptions.contains(option),
                        onCheckedChange = { checked ->
                            onOptionSelected(option, checked)
                        }
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.body1.merge(),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PreferencesScreen(
    onNavigateToProfile: () -> Unit
) {
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

    val scrollState = rememberScrollState()

    var age by rememberSaveable { mutableStateOf(18) } // Default initial age
    var age2 by rememberSaveable { mutableStateOf(30) }
    var showAgeSlider by rememberSaveable { mutableStateOf(false) }

    val genderOptions2 = listOf("Male", "Female", "Other")
    var selectGenders2 by rememberSaveable { mutableStateOf(listOf<String>()) }
    var genderError2 by remember { mutableStateOf(false) }
    val programOptions2 = listOf("Arts", "Engineering", "Environment", "Health", "Mathematics", "Science")
    var selectedPrograms2 by rememberSaveable { mutableStateOf(listOf<String>()) }
    var programError2 by remember { mutableStateOf(false) }
    val ethnicityOptions2 = listOf("Black/African Descent", "East Asian", "Hispanic/Latino",
        "Middle Eastern", "Native", "Pacific Islander", "South Asian", "South East Asian", "White/Caucasian", "Other") // Define your options here
    var selectedEthnicity2 by rememberSaveable { mutableStateOf(listOf<String>()) }
    var ethnicityError2 by remember { mutableStateOf(false) }

    var showError2 by remember { mutableStateOf(false) }
    var showSuccess2 by remember { mutableStateOf(false) }


    remember {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val prefCollection = db.collection("preferences")
            val userRef = prefCollection.whereEqualTo("userId", userId)

            userRef.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val preference =
                            querySnapshot.documents[0].toObject(UserPreference::class.java)
                        if (preference != null) {
                            // Update mutable state variables with user data
                            age = preference.agePreferenceMin.toInt()
                            age2 = preference.agePreferenceMax.toInt()
                            selectGenders2 = preference.interestedInGender
                            selectedPrograms2 = preference.interestedInProgram
                            selectedEthnicity2 = preference.interestedInEthnicity
                        }
                    } else {
                        Log.d(
                            "PreferenceScreen",
                            "No matching document found for the userId"
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("PreferenceScreen", "get failed with ", exception)
                }
        } else {
            Log.d("PreferenceScreen", "User not authenticated or UID is null")
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(scrollState) // This adds the scrolling behavior
            .fillMaxHeight() // This makes the Column fill the available height
            .padding(16.dp)
    ) {
        Spacer(Modifier.weight(1f)) // This is used for layout purposes
        Button(
            onClick = onNavigateToProfile,
            modifier = Modifier,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE1474E))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) { // Align icon and text vertically
                Icon(
                    ImageVector.vectorResource(id = R.drawable.ic_arrow), // Replace with your icon's resource ID
                    contentDescription = "Edit Profile" // Accessibility description
                )
                Spacer(Modifier.width(4.dp)) // Add some spacing between the icon and the text
                Text("Profile") // Text following the icon
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
        Text("Preference Settings", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(24.dp))

        MultiSelect(
            options = genderOptions2,
            selectedOptions = selectGenders2,
            onOptionSelected = { option, isSelected ->
                selectGenders2 = if (isSelected) {
                    selectGenders2 + option
                } else {
                    selectGenders2 - option
                }
            },
            label = "I'm Interested In (Gender)*"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MultiSelect(
            options = ethnicityOptions2,
            selectedOptions = selectedEthnicity2,
            onOptionSelected = { option, isSelected ->
                selectedEthnicity2 = if (isSelected) {
                    selectedEthnicity2 + option
                } else {
                    selectedEthnicity2 - option
                }
            },
            label = "I'm Interested In (Ethnicity)*"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MultiSelect(
            options = programOptions2,
            selectedOptions = selectedPrograms2,
            onOptionSelected = { option, isSelected ->
                selectedPrograms2 = if (isSelected) {
                    selectedPrograms2 + option
                } else {
                    selectedPrograms2 - option
                }
            },
            label = "I'm Interested In (Program)*"
        )
        var showSlider by remember { mutableStateOf(false) } // State to control the visibility of the slider

        Column() {
            OutlinedTextField(
                value = "Ages: ${age.toInt()} to ${age2.toInt()}",
                onValueChange = {},
                label = { Text("Select Age Range*") },
                readOnly = true, // Makes it non-editable
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        "Select Age Range",
                        Modifier.clickable { showSlider = !showSlider })
                },
                modifier = Modifier
                    .clickable { showSlider = !showSlider }
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            if (showSlider) {
                RangeSlider(
                    value = age.toFloat()..age2.toFloat(),
                    onValueChange = { newRange ->
                        if (newRange.start == newRange.endInclusive) {
                            // If both sliders are at the same position, increment age2 by 1
                            age = newRange.start.toInt()
                            age2 = (newRange.endInclusive + 1).toInt()
                        } else if (newRange.start < newRange.endInclusive) {
                            // If the range is valid, update age and age2 accordingly
                            age = newRange.start.toInt()
                            age2 = newRange.endInclusive.toInt()
                        } else {
                            // If the start value is greater than the end value, swap them
                            age = newRange.endInclusive.toInt()
                            age2 = newRange.start.toInt()
                        }
                    },
                    steps = 30 - 18 - 1,
                    valueRange = 18f..30f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
        }
        if (showError2) {
            AlertDialog(
                onDismissRequest = {
                    showError2 = false // Dismiss the dialog when the user clicks outside it or on the dismiss button
                },
                title = {
                    Text(text = "Missing Information")
                },
                text = {
                    Text("Please fill in the required field(s)*.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showError2 = false // Dismiss dialog when the user clicks the confirm button
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE1474E))
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        if (showSuccess2) {
            AlertDialog(
                onDismissRequest = {
                    // Reset the flag when the dialog is dismissed
                    showSuccess2 = false
                },
                title = {
                    Text(text = "Success")
                },
                text = {
                    Text("Preferences saved successfully!")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Reset the flag when the user acknowledges the success
                            showSuccess2 = false
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE1474E))
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        // Save button
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE1474E)),
            onClick = {
                genderError2 = selectGenders2.isEmpty()
                ethnicityError2 = selectedEthnicity2.isEmpty()
                programError2 = selectedPrograms2.isEmpty()

                if (genderError2 || ethnicityError2 || programError2) {
                    showError2 = true // This shows a dialog, or you could use it to indicate error in other ways
                } else {
                    val userId = auth.currentUser?.uid
                    if (userId == null) {
                        // Handle the case where the user is not authenticated or the UID is null
                        Log.e("Preference", "User not authenticated or UID is null")
                        return@Button
                    }
                    val prefCollection = db.collection("preferences")
                    val userRef = prefCollection.whereEqualTo("userId", userId)
                    val updatedUser = UserPreference(
                        userId = auth.currentUser?.uid as String,
                        interestedInGender = selectGenders2,
                        interestedInEthnicity = selectedEthnicity2,
                        interestedInProgram = selectedPrograms2,
                        agePreferenceMin = age.toInt(),
                        agePreferenceMax = age2.toInt()

                    )
                    userRef.get()
                        .addOnSuccessListener { querySnapshot ->
                            // Assuming only one document should match the query
                            val document = querySnapshot.documents.firstOrNull()
                            if (document != null) {
                                val docId = document.id // Get the document ID
                                prefCollection.document(docId)
                                    .set(updatedUser) // Update the document with updatedUser data
                                    .addOnSuccessListener {
                                        Log.d("Preference", "DocumentSnapshot successfully updated!")
                                        // Handle success
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Preference", "Error updating document", e)
                                        // Handle failure
                                    }
                            } else {
                                Log.d("Preference", "No matching document found for the userId")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("Preference", "Error getting documents: ", exception)
                        }
                    showSuccess2 = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Save")
        }
    }
}

@Composable
fun AgeSelector(
    modifier: Modifier = Modifier,
    ageRange: ClosedFloatingPointRange<Float> = 18f..30f,
    initialAge: Float,
    onAgeSelected: (Float) -> Unit,
    label: String
) {
    var age by remember { mutableStateOf(initialAge) }
    var showSlider by remember { mutableStateOf(false) } // State to control the visibility of the slider

    Column(modifier = modifier) {
        androidx.compose.material.OutlinedTextField(
            value = "Age: ${age.toInt()}",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true, // Makes it non-editable
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, "Select Age", Modifier.clickable { showSlider = !showSlider })
            },
            modifier = Modifier
                .clickable { showSlider = !showSlider }
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        // Show the Slider when the OutlinedTextField is clicked
        if (showSlider) {
            Slider(
                value = age,
                onValueChange = { age = it },
                valueRange = ageRange,
                steps = (ageRange.endInclusive.toInt() - ageRange.start.toInt()) - 1,
                onValueChangeFinished = {
                    onAgeSelected(age)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}