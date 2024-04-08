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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.AccountCircle
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.ktx.storage
import androidx.compose.ui.text.style.TextDecoration

import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight


@Composable
fun ProfileSettingsScreen(
    profileImage: ImageVector, // Placeholder for profile image
    onImageClick: () -> Unit, // Placeholder click action for adding an image
    onImageSelected: (Uri) -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToSurvey: () -> Unit,
    context: Context
) {

    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val showDialog = remember { mutableStateOf(false) }

    // This will be `true` if the dialog has never been shown, hence `!hasShownDialog`
    val hasShownDialog = sharedPreferences.getBoolean("HasShownDialog", false)
    if (!hasShownDialog) {
        LaunchedEffect(Unit) {
            showDialog.value = true
            with(sharedPreferences.edit()) {
                putBoolean("HasShownDialog", true)
                apply()
            }
        }
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Welcome to the Profile Setup!") },
            text = {
                val message = buildAnnotatedString {
                    append("Please complete all the mandatory fields in ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Profile Setting")
                    }
                    append(", ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Edit Preferences")
                    }
                    append(", and ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Edit Survey")
                    }
                    append(" to start matching. \nClick ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Save")
                    }
                    append(" after finishing each!")
                }
                Text(message)
            },
            confirmButton = {
                Button(
                    onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE1474E))
                ) {
                    Text("Got it!")
                }
            }
        )
    }

    UWRizzTheme {
        val red = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFFE1474E),
            unfocusedBorderColor = Color(0xFFE1474E)
        )


        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()

        var imageUri by rememberSaveable{ mutableStateOf<Uri?>(null) }
        var imageUri1 by rememberSaveable { mutableStateOf<Uri?>(null) }
        var imageUri2 by rememberSaveable { mutableStateOf<Uri?>(null) }
        var imageUri3 by rememberSaveable { mutableStateOf<Uri?>(null) }
        var currentImageSelection by rememberSaveable { mutableStateOf(0) }


        var firstname by rememberSaveable { mutableStateOf("") }
        var firstnameError by remember { mutableStateOf(false) }
        var lastname by rememberSaveable { mutableStateOf("") }
        var hobby by rememberSaveable { mutableStateOf("") }
        var age by rememberSaveable { mutableStateOf(18) }
        var showAgeSlider by rememberSaveable { mutableStateOf(false) }
        var oneWord by rememberSaveable { mutableStateOf("") }
        var promptAnswer by rememberSaveable { mutableStateOf("") }
        var selectedHobbyEmoji by rememberSaveable { mutableStateOf("Please select hobby emoji") }
        var hEmojiError by remember { mutableStateOf(false) }
        var selectedProgramEmoji by rememberSaveable { mutableStateOf("Please select program emoji") }
        var pEmojiError by remember { mutableStateOf(false) }
        var selectedOneEmoji by rememberSaveable { mutableStateOf("An emoji that describes you") }
        var oEmojiError by remember { mutableStateOf(false) }

        val hobbyEmojis = listOf(
            "\uD83C\uDF3E", // 🌾 - Gardening
            "\uD83C\uDF54", // 🍔 - Cooking
            "\uD83D\uDCD6", // 📖 - Reading
            "\uD83C\uDFB5", // 🎵 - Music
            "\u26BD",       // ⚽ - Sports
            "\uD83D\uDCF7", // 📷 - Photography
            "\uD83D\uDDBC", // 🖼️ - Drawing
            "\u2708",       // ✈️ - Traveling
            "\uD83C\uDFAE", // 🎮 - Gaming
            "\u26F0"        // ⛰️ - Hiking
        )
        val randomEmojis = listOf(
            "\uD83C\uDF75", // 🍵 - Bubble Tea
            "\uD83E\uDD84", // 🚄 - High-Speed Train
            "\uD83D\uDD25", // 🔥 - Fire
            "\uD83D\uDE80", // 🚀 - Rocket
            "\uD83D\uDE0E", // 😎 - Smiling Face with Sunglasses
            "\uD83D\uDCDD", // 📝 - Memo
            "\uD83D\uDEB4", // 🚴 - Person Biking
            "\uD83D\uDCFA", // 📺 - Television
            "\uD83D\uDC8E", // 💎 - Gem Stone
            "\uD83D\uDD2E"  // 🔮 - Crystal Ball
        )
        val programEmojis = listOf(
            "\uD83D\uDCBB", // 💻 - Computer Science
            "\uD83D\uDD2C", // 🔬 - Engineering
            "\uD83E\uDD13", // 🏥 - Medicine
            "\uD83D\uDCBC", // 💼 - Business Administration
            "\uD83D\uDC68\u200D\uD83C\uDFEB", // 👨‍⚕️ - Psychology
            "\uD83D\uDD0E", // 📎 - Law
            "\uD83D\uDC4C", // 👌 - Biology
            "\uD83D\uDCB0", // 💰 - Economics
            "\uD83D\uDCA0", // 📊 - Mathematics
            "\uD83C\uDFA8"  // 🎨 - Fine Arts
        )
        val promptOptions = listOf(
            "My idea of a perfect weekend involves...",
            "One thing I can't live without is...",
            "The best way to win me over is...",
            "If I could travel anywhere tomorrow, I would go to...",
            "My favorite way to relax is...",
            "A hidden talent of mine is...",
            "The last book/movie/show that made me cry/laugh/think was...",
            "The way to my heart is through...",
            "One thing I'm passionate about is...",
            "If I could meet anyone, past or present, it would be..."
        )
        var selectedPrompt by rememberSaveable { mutableStateOf("Please select a prompt") }
        var expandedPrompt by rememberSaveable { mutableStateOf(false) }
        var expandedProgramEmoji by rememberSaveable { mutableStateOf(false) }
        var expandedHobbyEmoji by rememberSaveable { mutableStateOf(false) }
        var expandedOneEmoji by rememberSaveable { mutableStateOf(false) }
        //Dropdown states
        var expandedGender by rememberSaveable { mutableStateOf(false) }
        val genderOptions =
            listOf("Male", "Female", "Other", "Prefer not to say") // Define your options here
        var selectedGender by rememberSaveable { mutableStateOf("Please select your gender") }
        var GenderError by remember { mutableStateOf(false) }

        // Program dropdown states
        var expandedProgram by rememberSaveable { mutableStateOf(false) }
        val programOptions = listOf(
            "Arts",
            "Engineering",
            "Environment",
            "Health",
            "Mathematics",
            "Science"
        )
        var selectedProgram by rememberSaveable { mutableStateOf("Please select your program") }
        var ProgramError by remember { mutableStateOf(false) }

        var expandedEthnicity by rememberSaveable { mutableStateOf(false) }
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
        )
        var selectedEthnicity by rememberSaveable { mutableStateOf("Please select your ethnicity") }
        var ethnicityError by remember { mutableStateOf(false) }
        var showError by remember { mutableStateOf(false) }
        var picError by remember { mutableStateOf(false) }
        var showSuccess by remember { mutableStateOf(false) }


        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        fun saveImageUri(uri: Uri, key: String) {
            editor.putString(key, uri.toString()).apply()
        }
        fun loadImageUri(key: String): Uri? {
            val uriString = sharedPreferences.getString(key, null)
            return uriString?.let { Uri.parse(it) }
        }

        fun uploadImageToFirebaseStorage(imageUri: Uri, imageIndex: Int) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val fileName = when(imageIndex) {
                0 -> "profile_image"
                1 -> "image_1"
                2 -> "image_2"
                3 -> "image_3"
                else -> return // Invalid index, return early
            }
            val storageRef = Firebase.storage.reference.child("users/$userId/$fileName.jpg")

            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    Log.d("ProfileSettingsScreen", "Image $fileName uploaded successfully")
                    // If you need to do something after the upload, such as updating the user's profile with the new image URL, you can do it here.
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileSettingsScreen", "Failed to upload $fileName to Firebase", e)
                }
        }

        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Requesting persistable permission
                try {
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(it, takeFlags)
                } catch (e: SecurityException) {
                    Log.e("ProfileSettingsScreen", "Error taking persistable URI permission", e)
                }

                // Determine which image URI to update based on currentImageSelection
                when (currentImageSelection) {
                    0 -> imageUri = it
                    1 -> imageUri1 = it
                    2 -> imageUri2 = it
                    3 -> imageUri3 = it
                }

                // Upload the selected image to Firebase Storage
                uploadImageToFirebaseStorage(it, currentImageSelection)
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
                                age = user.age as Int
                                selectedEthnicity = user.ethnicity
                                selectedGender = user.gender
                                selectedProgram = user.program
                                selectedProgramEmoji = user.programEmoji
                                hobby = user.hobby
                                selectedHobbyEmoji = user.hobbyEmoji
                                oneWord = user.oneWord
                                selectedOneEmoji = user.oneEmoji
                                selectedPrompt = user.prompt
                                promptAnswer = user.promptAnswer

                                // Load image URIs
                                imageUri = loadImageUri("profile_image_uri")
                                imageUri1 = loadImageUri("image_uri_1")
                                imageUri2 = loadImageUri("image_uri_2")
                                imageUri3 = loadImageUri("image_uri_3")
                            }
                        } else {
                            Log.d(
                                "ProfileSettingsScreen",
                                "No matching document found for the userId"
                            )
                        }
                    }
            } else {
                Log.d("ProfileSettingsScreen", "User not authenticated or UID is null")
            }
        }
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Fill the width of the parent to allow the Spacer to push the button to the right
                    .padding(bottom = 16.dp), //
                verticalAlignment = Alignment.Top // Align items to the top
            ) {
                // Profile picture
                val storage = Firebase.storage

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(118.dp)
                        .clip(CircleShape)
                        .border(2.dp,
                            if (imageUri == null) Color.Red else Color.Gray,
                            CircleShape)
                        .clickable {
                            currentImageSelection = 0 // Assign a value indicating profile picture selection
                            galleryLauncher.launch("image/*") // Launch the gallery
                        }
                ) {
                    // jeff's image upload currently not working, so commenting out to prevent crash
//                    imageUri?.let { uri ->
//                        // Upload the selected image to Firebase Storage
//                        val storageRef = storage.reference
//                        val profilePicRef = storageRef.child("profile_pictures/${auth.currentUser?.uid.toString()}.jpg")
//                        try {
//                            val uploadTask = profilePicRef.putFile(uri)
//                            uploadTask.addOnSuccessListener {
//                                // Handle success
//                            }.addOnFailureListener { exception ->
//                                // Handle failure
//                            }
//                        } catch (e: SecurityException) {
//                            // Handle the exception, likely by requesting the user to re-select the image.
//                        }
//                        // Display the selected image
//                        Image(
//                            painter = rememberImagePainter(data = uri),
//                            contentDescription = "Profile picture",
//                            modifier = Modifier
//                                .size(120.dp)
//                                .clip(CircleShape),
//                            contentScale = ContentScale.Crop
//                        )
//                    } ?:
                    Icon(
                        // If imageUri is null, display a default icon to indicate profile picture upload
                        imageVector = Icons.Default.AccountCircle, // Use the correct default icon here
                        contentDescription = "Profile picture placeholder",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }
                if (imageUri == null) {
                    Text(
                        "*",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }


                Spacer(Modifier.weight(1f)) // This pushes the button to the right
                Column(horizontalAlignment = Alignment.End) {
                    // Edit Preferences button
                    Button(
                        onClick = onNavigateToPreferences,
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE1474E))
                    ) {
                        Text("Edit Preferences")
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Button(
                        onClick = onNavigateToSurvey,
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE1474E))
                    ) {
                        Text("Edit Survey")
                    }
                }
            }
            Text(
                text = "Important Notice!",
                modifier = Modifier
                    .clickable { showDialog.value = true }
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.body2.copy(textDecoration = TextDecoration.Underline)
            )

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
                onValueChange = {
                    firstname = it
                    firstnameError = it.isBlank() // Update error state
                },
                label = { Text("First Name*") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                isError = firstnameError, // Set error state
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    // Define colors for different states
                    textColor = Color.Black,
                    disabledTextColor = Color.Gray,
                    backgroundColor = Color.Transparent,
                    cursorColor = Color.Black,
                    errorCursorColor = MaterialTheme.colors.error,
                    focusedBorderColor = Color.Red, // Default color for the border when focused
                    unfocusedBorderColor = Color.Red, // Default color for the border when not focused
                    errorBorderColor = MaterialTheme.colors.error, // Color for the border when in error state
                )
            )
            if (firstnameError) {
                Text(
                    text = "First Name is required",
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            OutlinedTextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text("Last Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = red
            )
            //age
            val label = "Select Age*"
            val ageRange = 18f..30f
            var showSlider by remember { mutableStateOf(false) } // State to control the visibility of the slider

            Column() {
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
                        .padding(top = 16.dp),
                    colors = red
                )
                // Show the Slider when the OutlinedTextField is clicked
                if (showSlider) {
                    Slider(
                        value = age.toFloat(),
                        onValueChange = { age = it.toInt() },
                        valueRange = ageRange,
                        steps = (ageRange.endInclusive.toInt() - ageRange.start.toInt()) - 1,
                        onValueChangeFinished = {
                            age = age
                            showAgeSlider = false
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFA500),
                            activeTrackColor = Color(0xFFFFA500).copy(alpha = ContentAlpha.high),
                            inactiveTrackColor = Color(0xFFFFA500).copy(alpha = ContentAlpha.disabled), // Customizing to a lighter yellow for inactive part
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }


            //Ethnicity
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top)
            ) {
                OutlinedTextField(
                    value = selectedEthnicity,
                    onValueChange = { /* ReadOnly TextField */ },
                    label = { Text("Ethnicity*") },
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
                    readOnly = true,
                    colors = red
                )
                DropdownMenu(
                    expanded = expandedEthnicity,
                    onDismissRequest = { expandedEthnicity = false }
                ) {
                    ethnicityOptions.forEach { ethnicity ->
                        DropdownMenuItem(onClick = {
                            selectedEthnicity = ethnicity
                            expandedEthnicity = false
                            ethnicityError = false // Reset error state when ethnicity is selected
                        }) {
                            Text(text = ethnicity)
                        }
                    }
                }
            }
            if (selectedEthnicity.isEmpty()) {
                ethnicityError = true // Set error state if ethnicity is empty
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
                    label = { Text("Gender*") },
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
                    readOnly = true,
                    colors = red
                    // Make TextField readonly
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
            if (selectedGender.isEmpty()) {
                GenderError = true // Set error state if ethnicity is empty
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
                    label = { Text("Program*") },
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
                    readOnly = true,
                    colors = red// Make TextField readonly
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
            if (selectedProgram.isEmpty()) {
                ProgramError = true // Set error state if ethnicity is empty
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top)
            ) {
                OutlinedTextField(
                    value = selectedProgramEmoji,
                    onValueChange = { /* ReadOnly TextField */ },
                    label = { Text("Program Emoji*") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { expandedProgramEmoji = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { expandedProgramEmoji = true }
                        )
                    },
                    readOnly = true,
                    colors = red// Make TextField readonly
                )
                DropdownMenu(
                    expanded = expandedProgramEmoji,
                    onDismissRequest = { expandedProgramEmoji = false }
                ) {
                    programEmojis.forEach { emoji ->
                        DropdownMenuItem(onClick = {
                            selectedProgramEmoji = emoji
                            expandedProgramEmoji = false
                        }) {
                            Text(text = emoji)
                        }
                    }
                }
            }
            if (selectedProgramEmoji.isEmpty()) {
                pEmojiError = true // Set error state if ethnicity is empty
            }

            OutlinedTextField(
                value = hobby,
                onValueChange = { hobby = it },
                label = { Text("Hobby") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = red
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top)
            ) {
                OutlinedTextField(
                    value = selectedHobbyEmoji,
                    onValueChange = { /* ReadOnly TextField */ },
                    label = { Text("Hobby Emoji*") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { expandedHobbyEmoji = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { expandedHobbyEmoji = true }
                        )
                    },
                    readOnly = true,
                    colors = red
                    // Make TextField readonly
                )
                DropdownMenu(
                    expanded = expandedHobbyEmoji,
                    onDismissRequest = { expandedHobbyEmoji = false }
                ) {
                    hobbyEmojis.forEach { emoji ->
                        DropdownMenuItem(onClick = {
                            selectedHobbyEmoji = emoji
                            expandedHobbyEmoji = false
                        }) {
                            Text(text = emoji)
                        }
                    }
                }
            }
            if (selectedHobbyEmoji.isEmpty()) {
                hEmojiError = true // Set error state if ethnicity is empty
            }

            OutlinedTextField(
                value = oneWord,
                onValueChange = { oneWord = it },
                label = { Text("One Word to describe you") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = red
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top)
            ) {
                OutlinedTextField(
                    value = selectedOneEmoji,
                    onValueChange = { /* ReadOnly TextField */ },
                    label = { Text("An emoji that describes you*") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { expandedOneEmoji = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { expandedOneEmoji = true }
                        )
                    },
                    readOnly = true,
                    colors = red// Make TextField readonly
                )
                DropdownMenu(
                    expanded = expandedOneEmoji,
                    onDismissRequest = { expandedOneEmoji = false }
                ) {
                    randomEmojis.forEach { emoji ->
                        DropdownMenuItem(onClick = {
                            selectedOneEmoji = emoji
                            expandedOneEmoji = false
                        }) {
                            Text(text = emoji)
                        }
                    }
                }
            }
            if (selectedOneEmoji.isEmpty()) {
                oEmojiError = true // Set error state if ethnicity is empty
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top)
            ) {
                OutlinedTextField(
                    value = selectedPrompt,
                    onValueChange = { /* ReadOnly TextField */ },
                    label = { Text("Prompt") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { expandedPrompt = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { expandedPrompt = true }
                        )
                    },
                    readOnly = true,
                    colors = red// Make TextField readonly
                )
                DropdownMenu(
                    expanded = expandedPrompt,
                    onDismissRequest = { expandedPrompt = false }
                ) {
                    promptOptions.forEach { prompt ->
                        DropdownMenuItem(onClick = {
                            selectedPrompt = prompt
                            expandedPrompt = false
                        }) {
                            Text(text = prompt)
                        }
                    }
                }
            }
            OutlinedTextField(
                value = promptAnswer,
                onValueChange = { promptAnswer = it },
                label = { Text("Prompt Answer") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = red
            )

            if (showError) {
                AlertDialog(
                    onDismissRequest = {
                        showError = false // Dismiss the dialog when the user clicks outside it or on the dismiss button
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
                                showError = false // Dismiss dialog when the user clicks the confirm button
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE1474E))
                        ) {
                            Text("OK")
                        }
                    }
                )
            } else if (picError) {
                AlertDialog(
                    onDismissRequest = {
                        picError = false // Dismiss the dialog when the user clicks outside it or on the dismiss button
                    },
                    title = {
                        Text(text = "Missing Information")
                    },
                    text = {
                        Text("Please Insert a Profile Picture.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                picError = false // Dismiss dialog when the user clicks the confirm button
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE1474E))
                        ) {
                            Text("OK")
                        }
                    }
                )
            } else if (showSuccess) {
                AlertDialog(
                    onDismissRequest = {
                        // Reset the flag when the dialog is dismissed
                        showSuccess = false
                    },
                    title = {
                        Text(text = "Success")
                    },
                    text = {
                        Text("Profile saved successfully!")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Reset the flag when the user acknowledges the success
                                showSuccess = false
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
                    firstnameError = firstname.isBlank()
                    ethnicityError = selectedEthnicity.isBlank()
                    GenderError = selectedGender.isBlank()
                    ProgramError = selectedProgram.isBlank()
                    pEmojiError = selectedProgramEmoji.isBlank()
                    hEmojiError = selectedHobbyEmoji.isBlank()
                    oEmojiError = selectedOneEmoji.isBlank()
                    if ( firstnameError || ethnicityError|| GenderError ||
                        ProgramError || pEmojiError || hEmojiError || oEmojiError) {
                        showError = true // Show the dialog when validation fails
                    } else if (imageUri == null) {
                        picError = true
                    } else {
                        val userId = auth.currentUser?.uid
                        if (userId == null) {
                            // Handle the case where the user is not authenticated or the UID is null
                            Log.e("Profile", "User not authenticated or UID is null")
                            return@Button
                        }
                        val usersCollection = db.collection("users")
                        val userRef = usersCollection.whereEqualTo("userId", userId)
                        val updatedUser = BasicUserInfo(
                            userId = userId,
                            firstName = firstname,
                            lastName = lastname,
                            age = age,
                            ethnicity = selectedEthnicity,
                            gender = selectedGender,
                            program = selectedProgram,
                            hobby = hobby,
                            oneWord = oneWord,
                            oneEmoji = selectedOneEmoji,
                            programEmoji = selectedProgramEmoji,
                            hobbyEmoji = selectedHobbyEmoji,
                            prompt = selectedPrompt,
                            promptAnswer = promptAnswer,
                            profilePictureUri = imageUri?.toString() ?: "",
                            pictureUri1 = imageUri1?.toString() ?: "",
                            pictureUri2 = imageUri2?.toString() ?: "",
                            pictureUri3 = imageUri3?.toString() ?: ""
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
                        showSuccess = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            {
                Text("Save")
            }
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




