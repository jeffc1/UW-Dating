package com.example.uwrizz

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.vectorResource
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberImagePainter
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown


import com.example.uwrizz.R


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
                .fillMaxWidth().padding(top = 16.dp)
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

@Composable
fun ProfileSettingsScreen(
    profileImage: ImageVector, // Placeholder for profile image
    onImageClick: () -> Unit, // Placeholder click action for adding an image
    onImageSelected: (Uri) -> Unit,
    onNavigateToPreferences: () -> Unit,
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUri1 by remember { mutableStateOf<Uri?>(null) }
    var imageUri2 by remember { mutableStateOf<Uri?>(null) }
    var imageUri3 by remember { mutableStateOf<Uri?>(null) }
    var currentImageSelection by remember { mutableStateOf(0) }


    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var hobby by remember { mutableStateOf("") }
    var work by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(18f) } // Default initial age
    var showAgeSlider by remember { mutableStateOf(false) }

    // Gender dropdown states
    var expandedGender by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say") // Define your options here
    var selectedGender by remember { mutableStateOf("Please select your gender") }

    // Program dropdown states
    var expandedProgram by remember { mutableStateOf(false) }
    val programOptions = listOf("Arts", "Engineering", "Environment", "Health", "Mathematics", "Science") // Define your options here
    var selectedProgram by remember {mutableStateOf("Please select your program") }


    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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

            // Edit Preferences button
            Button(
                onClick = onNavigateToPreferences,
                modifier = Modifier
            ) {
                Text("Edit Preferences")
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
            ImageUploadButton(imageUri = imageUri1) { currentImageSelection = 1; galleryLauncher.launch("image/*") }
            ImageUploadButton(imageUri = imageUri2) { currentImageSelection = 2; galleryLauncher.launch("image/*") }
            ImageUploadButton(imageUri = imageUri3) { currentImageSelection = 3; galleryLauncher.launch("image/*") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Existing fields
        Text("Profile Settings", style = MaterialTheme.typography.h5)

        OutlinedTextField(
            value = firstname,
            onValueChange = { firstname = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
        OutlinedTextField(
            value = lastname,
            onValueChange = { lastname = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
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
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
        OutlinedTextField(
            value = selectedGender,
            onValueChange = { /* ReadOnly TextField */ },
            label = { Text("Gender") },
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
        OutlinedTextField(
            value = selectedProgram,
            onValueChange = { /* ReadOnly TextField */ },
            label = { Text("Program") },
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
        OutlinedTextField(
            value = hobby,
            onValueChange = { hobby = it },
            label = { Text("Hobby") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
        OutlinedTextField(
            value = work,
            onValueChange = { work = it },
            label = { Text("Work") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
        // Save button
        Button(
            onClick = {
                // Add logic here to save the profile settings
                // You can use the values of the mutable state variables like firstname, lastname, etc. to update the user's profile in the database
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Save")
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
    val work: String,
    val profilePictureUri: String? // Store the URI of the profile picture
)


//Image code
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
@Composable
fun PreferencesScreen(
    onNavigateToProfile: () -> Unit
) {
    val scrollState = rememberScrollState()

    var age by remember { mutableStateOf(18f) } // Default initial age
    var showAgeSlider by remember { mutableStateOf(false) }

    var expandedGender2 by remember { mutableStateOf(false) }
    val genderoptions2 = listOf("Male", "Female", "Other", "Prefer not to say") // Define your options here
    var selectGender2 by remember {mutableStateOf("Gender") }

    var expandedProgram2 by remember { mutableStateOf(false) }
    val programOptions2 = listOf("Arts", "Engineering", "Environment", "Health", "Mathematics", "Science") // Define your options here
    var selectedProgram2 by remember {mutableStateOf("Program") }

    Column(
        modifier = Modifier
            .verticalScroll(scrollState) // This adds the scrolling behavior
            .fillMaxHeight() // This makes the Column fill the available height
            .padding(16.dp) // Replace with your desired padding
    ) {
        Spacer(Modifier.weight(1f)) // This is used for layout purposes
        Button(
            onClick = onNavigateToProfile,
            modifier = Modifier
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
        OutlinedTextField(
            value = selectGender2,
            onValueChange = { /* ReadOnly TextField */ },
            label = { Text("I'm Interested In: ") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clickable { expandedGender2 = true },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown",
                    Modifier.clickable { expandedGender2 = true }
                )
            },
            readOnly = true // Make TextField readonly
        )
        DropdownMenu(
            expanded = expandedGender2,
            onDismissRequest = { expandedGender2 = false }
        ) {
            genderoptions2.forEach { gender ->
                DropdownMenuItem(onClick = {
                    selectGender2 = gender
                    expandedGender2 = false
                }) {
                    Text(text = gender)
                }
            }
        }

        OutlinedTextField(
            value = selectedProgram2,
            onValueChange = { /* ReadOnly TextField */ },
            label = { Text("I'm Interested In: ") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clickable { expandedProgram2 = true },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown",
                    Modifier.clickable { expandedProgram2 = true }
                )
            },
            readOnly = true // Make TextField readonly
        )
        DropdownMenu(
            expanded = expandedProgram2,
            onDismissRequest = { expandedProgram2 = false }
        ) {
            programOptions2.forEach { program ->
                DropdownMenuItem(onClick = {
                    selectedProgram2 = program
                    expandedProgram2 = false
                }) {
                    Text(text = program)
                }
            }
        }
        AgeSelector(
            ageRange = 18f..30f, // This is the range of the slider
            initialAge = age, // Pass the initial age, it could be a state if you need to remember it
            onAgeSelected = {
                age = it // Update the age when the user has finished selecting a new age
                showAgeSlider = false // Hide the slider
            },
            label = "Select Preferred Minimum Age"
        )
        AgeSelector(
            ageRange = 18f..30f, // This is the range of the slider
            initialAge = age, // Pass the initial age, it could be a state if you need to remember it
            onAgeSelected = {
                age = it // Update the age when the user has finished selecting a new age
                showAgeSlider = false // Hide the slider
            },
            label = "Select Preferred Maximum Age"
        )

        // Save button
        Button(
            onClick = {
                // Add logic here to save the profile settings
                // You can use the values of the mutable state variables like firstname, lastname, etc. to update the user's profile in the database
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Save")
        }
    }
}







