package com.example.uwrizz

import com.example.uwrizz.ui.theme.UWRizzTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.*
import androidx.compose.ui.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.vectorResource
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberImagePainter
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.mutableStateOf


import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

//UserPreferenceViewModel, needs to be changed, this ensures the user can save the information
//after clicking save" button"


// You can add more functions to save other types

@Composable
fun ProfileSettingsScreen(
    profileImage: ImageVector, // Placeholder for profile image
    onImageClick: () -> Unit, // Placeholder click action for adding an image
    onImageSelected: (Uri) -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToSurvey: () -> Unit
) {
    UWRizzTheme {
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()

        var imageUri by remember { mutableStateOf<Uri?>(null) }
        var imageUri1 by remember { mutableStateOf<Uri?>(null) }
        var imageUri2 by remember { mutableStateOf<Uri?>(null) }
        var imageUri3 by remember { mutableStateOf<Uri?>(null) }
        var currentImageSelection by remember { mutableStateOf(0) }


        var firstname by remember { mutableStateOf("") }
        var lastname by remember { mutableStateOf("") }
        var bio by remember { mutableStateOf("") }
        var hobby by remember { mutableStateOf("") }
        var job by remember { mutableStateOf("") }
        var age by remember { mutableStateOf(18f) } // Default initial age
        var showAgeSlider by remember { mutableStateOf(false) }
        //Dropdown states
        var expandedGender by remember { mutableStateOf(false) }
        val genderOptions =
            listOf("Male", "Female", "Other", "Prefer not to say") // Define your options here
        var selectedGender by remember { mutableStateOf("Please select your gender") }

        // Program dropdown states
        var expandedProgram by remember { mutableStateOf(false) }
        val programOptions = listOf(
            "Arts",
            "Engineering",
            "Environment",
            "Health",
            "Mathematics",
            "Science"
        ) // Define your options here
        var selectedProgram by remember { mutableStateOf("Please select your program") }

        var expandedEthnicity by remember { mutableStateOf(false) }
        val ethnicityOptions = listOf(
            "Black/African Descent",
            "East Asian",
            "Hispanic/Latino",
            "Middle Eastern",
            "Native",
            "Pacific Islander",
            "South Asian",
            "South East Asian",
            "White/Caucasian",
            "Other"
        ) // Define your options here
        var selectedEthnicity by remember { mutableStateOf("Please select your ethnicity") }


        val galleryLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    when (currentImageSelection) {
                        1 -> imageUri1 = it
                        2 -> imageUri2 = it
                        3 -> imageUri3 = it
                        else -> imageUri = it
                    }
                }
            }
        val scrollState = rememberScrollState()

        remember {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val usersCollection = db.collection("users")
                val userRef = usersCollection.whereEqualTo("userId", userId)

                userRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val user =
                                querySnapshot.documents[0].toObject(BasicUserInfo::class.java)
                            if (user != null) {
                                // Update mutable state variables with user data
                                firstname = user.firstName
                                lastname = user.lastName
                                bio = user.bio
                                selectedEthnicity = user.ethnicity
                                selectedGender = user.gender
                                selectedProgram = user.program
                                hobby = user.hobby
                                job = user.job
                                age = user.age
                            }
                        } else {
                            Log.d(
                                "ProfileSettingsScreen",
                                "No matching document found for the userId"
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("ProfileSettingsScreen", "get failed with ", exception)
                    }
            } else {
                Log.d("ProfileSettingsScreen", "User not authenticated or UID is null")
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(scrollState) // This adds the scrolling behavior
                .fillMaxHeight() // This makes the Column fill the available height
                .padding(16.dp) // Replace with your desired padding
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Fill the width of the parent to allow the Spacer to push the button to the right
                    .padding(bottom = 16.dp), //
                verticalAlignment = Alignment.Top // Align items to the top
            ) {
                // Profile picture
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(118.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                        .clickable { galleryLauncher.launch("image/*") } // Launch the gallery
                ) {
                    imageUri?.let {
                        Image(
                            painter = rememberImagePainter(it),
                            contentDescription = "Profile picture",
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_headc), // Placeholder icon resource
                        contentDescription = "Profile picture",
                        modifier = Modifier.size(120.dp)
                    )
                }

                Spacer(Modifier.weight(1f)) // This pushes the button to the right
                Column(horizontalAlignment = Alignment.End) {
                    // Edit Preferences button
                    Button(
                        onClick = onNavigateToPreferences,
                        modifier = Modifier
                    ) {
                        Text("Edit Preferences")
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Button(
                        onClick = onNavigateToSurvey,
                        modifier = Modifier
                    ) {
                        Text("Edit Survey")
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            Text("Insert your pictures:")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ImageUploadButton(imageUri = imageUri1) {
                    currentImageSelection = 1; galleryLauncher.launch("image/*")
                }
                ImageUploadButton(imageUri = imageUri2) {
                    currentImageSelection = 2; galleryLauncher.launch("image/*")
                }
                ImageUploadButton(imageUri = imageUri3) {
                    currentImageSelection = 3; galleryLauncher.launch("image/*")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Existing fields
            Text("Profile Settings", style = MaterialTheme.typography.h5)


            OutlinedTextField(
                value = firstname,
                onValueChange = { firstname = it },
                label = { Text("First Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            OutlinedTextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text("Last Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            //age
            AgeSelector(
                ageRange = 18f..30f, // This is the range of the slider
                initialAge = age, // Pass the initial age, it could be a state if you need to remember it
                onAgeSelected = {
                    age = it // Update the age when the user has finished selecting a new age
                    showAgeSlider = false // Hide the slider
                },
                label = "Select Age"
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            //Ethnicity
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top)
            ) {
                OutlinedTextField(
                    value = selectedEthnicity,
                    onValueChange = { /* ReadOnly TextField */ },
                    label = { Text("Ethnicity") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { expandedEthnicity = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { expandedEthnicity = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = expandedEthnicity,
                    onDismissRequest = { expandedEthnicity = false }
                ) {
                    ethnicityOptions.forEach { ethnicity ->
                        DropdownMenuItem(onClick = {
                            selectedEthnicity = ethnicity
                            expandedEthnicity = false
                        }) {
                            Text(text = ethnicity)
                        }
                    }
                }
            }

            //Gender
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top)
            ) {
                OutlinedTextField(
                    value = selectedGender,
                    onValueChange = { /* ReadOnly TextField */ },
                    label = { Text("Gender") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { expandedGender = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { expandedGender = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = expandedGender,
                    onDismissRequest = { expandedGender = false }
                ) {
                    genderOptions.forEach { gender ->
                        DropdownMenuItem(onClick = {
                            selectedGender = gender
                            expandedGender = false
                        }) {
                            Text(text = gender)
                        }
                    }
                }
            }

            //Program
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top)
            ) {
                OutlinedTextField(
                    value = selectedProgram,
                    onValueChange = { /* ReadOnly TextField */ },
                    label = { Text("Program") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { expandedProgram = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { expandedProgram = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = expandedProgram,
                    onDismissRequest = { expandedProgram = false }
                ) {
                    programOptions.forEach { program ->
                        DropdownMenuItem(onClick = {
                            selectedProgram = program
                            expandedProgram = false
                        }) {
                            Text(text = program)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = hobby,
                onValueChange = { hobby = it },
                label = { Text("Hobby") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            OutlinedTextField(
                value = job,
                onValueChange = { job = it },
                label = { Text("Job") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            // Save button
            Button(
                onClick = {
                    val userId = auth.currentUser?.uid
                    if (userId == null) {
                        // Handle the case where the user is not authenticated or the UID is null
                        Log.e("Profile", "User not authenticated or UID is null")
                        return@Button
                    }
                    val usersCollection = db.collection("users")
                    val userRef = usersCollection.whereEqualTo("userId", userId)
                    val updatedUser = BasicUserInfo(
                        userId = auth.currentUser?.uid as String,
                        firstName = firstname,
                        lastName = lastname,
                        age = age,
                        ethnicity = selectedEthnicity,
                        gender = selectedGender,
                        program = selectedProgram,
                        hobby = hobby,
                        job = job,
                        bio = bio,
                    )
                    userRef.get()
                        .addOnSuccessListener { querySnapshot ->
                            // Assuming only one document should match the query
                            val document = querySnapshot.documents.firstOrNull()
                            if (document != null) {
                                val docId = document.id // Get the document ID
                                usersCollection.document(docId)
                                    .set(updatedUser) // Update the document with updatedUser data
                                    .addOnSuccessListener {
                                        Log.d("Profile", "DocumentSnapshot successfully updated!")
                                        // Handle success
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Profile", "Error updating document", e)
                                        // Handle failure
                                    }
                            } else {
                                Log.d("Profile", "No matching document found for the userId")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("Profile", "Error getting documents: ", exception)
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
}



//Database interaction
data class UserProfile(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val bio: String,
    val gender: String,
    val program: String,
    val hobby: String,
    val Job: String,
    val profilePictureUri: String? // Store the URI of the profile picture
)


//Image selection
enum class ImageSource {
    Gallery,
    Camera
}

fun onImageSelected(source: ImageSource) {
    // Handle image selection based on the source (Gallery or Camera)
    when (source) {
        ImageSource.Gallery -> {
            // Logic to handle gallery image selection
        }
        ImageSource.Camera -> {
            // Logic to handle camera image capture
        }
    }
}

@Composable
fun ImageUploadButton(
    imageUri: Uri?,
    onImageClick: () -> Unit
) {
    Button(
        onClick = onImageClick,
        modifier = Modifier.size(100.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
        )
    ) {
        imageUri?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Uploaded image",
                modifier = Modifier.size(96.dp),
                contentScale = ContentScale.Crop
            )
        } ?: Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_add),
            contentDescription = "Add picture",
            modifier = Modifier.size(24.dp)
        )
    }
}


//Preference page

//Function for multi selection
@Composable
fun MultiSelect(
    options: List<String>,
    selectedOptions: List<String>,
    onOptionSelected: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
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
            modifier = Modifier.fillMaxWidth()
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
        OutlinedTextField(
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
