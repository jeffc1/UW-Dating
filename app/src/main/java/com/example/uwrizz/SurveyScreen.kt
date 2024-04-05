import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uwrizz.SurveyAnswers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.vectorResource
import com.example.uwrizz.R


// Data class representing a single survey response
data class SurveyAnswer(
    val question: String,
    val answer: Int // Store answer as an integer (1-5)
)

// Function to save survey responses to Firestore
fun saveSurveyResponses(responses: List<SurveyAnswer>) {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val userId = auth.currentUser?.uid as String
    val surveyCollection = db.collection("survey")

    val surveyAnswers = SurveyAnswers(userId, responses.map { it.answer })


    // Query the survey collection for documents where the "userId" field matches the current user's ID
    surveyCollection.whereEqualTo("userId", userId)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                // If documents are found with matching userId, update the first document found
                val surveyDoc = documents.first()
                surveyDoc.reference.update("answers", surveyAnswers.answers)
                    .addOnSuccessListener {
                        Log.d("Survey", "Survey responses updated successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Survey", "Error updating survey responses", e)
                    }
            } else {
                // Handle case where no document exists with matching userId
                Log.d("Survey", "No survey document found for the user ID")
            }
        }
        .addOnFailureListener { e ->
            Log.w("Survey", "Error fetching survey documents", e)
        }
}

@Composable
fun QuestionAnswerItem(
    question: String,
    answer: MutableState<String>,
    onAnswerChanged: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    val red = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color(0xFFE1474E),
        unfocusedBorderColor = Color(0xFFE1474E)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Top)
        ) {
            Text(question, style = MaterialTheme.typography.h5.copy(fontSize = 16.sp))

            OutlinedTextField(
                value = answer.value,
                onValueChange = { /* ReadOnly TextField */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clickable { expanded = true },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown",
                        Modifier.clickable { expanded = true }
                    )
                },
                readOnly = true,
                colors = red// Make TextField readonly
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        onAnswerChanged(option)
                        answer.value = option // Update the value of the answer field
                        expanded = false
                    }) {
                        Text(text = option)
                    }
                }
            }
        }
    }
}

@Composable
fun SurveyScreen(
    onNavigateToProfile: () -> Unit
) {

    var surveyComplete by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    val QuestionOptions = listOf("Strongly Disagree", "Disagree", "Neutral", "Agree", "Strongly Agree") // Define your options here

    val questions = listOf(
        "I enjoy spending time outdoors and engaging in adventurous activities.",
        "I enjoy maintaining a healthy lifestyle, including regular exercise and a balanced diet.",
        "I enjoy quiet, cozy nights in rather than going out to crowded social events.",
        "I enjoy deep conversations and meaningful connections over superficial interactions.",
        "I enjoy traveling and exploring new cultures as an essential part of my life.",
        "I enjoy trying new cuisines and experimenting with different flavors.",
        "I enjoy prioritizing spending quality time with family and close friends.",
        "I enjoy open communication and honesty in a relationship.",
        "I enjoy valuing ambition and striving to achieve my goals.",
        "I enjoy spontaneity and embracing new experiences without hesitation."
    )
    val red = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color(0xFFE1474E),
        unfocusedBorderColor = Color(0xFFE1474E)
    )

    // Mutable state for storing answers
    var answers by remember {
        mutableStateOf(List(questions.size) { mutableStateOf("Please select your answer") })
    }
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    remember {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val surveyCollection = db.collection("survey")
            val surveyRef = surveyCollection.whereEqualTo("userId", userId)

            surveyRef.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val surveyAnswersList = mutableListOf<String>()
                        querySnapshot.documents.forEach { document ->
                            val surveyAnswers = document.toObject(SurveyAnswers::class.java)
                            if (surveyAnswers != null) {
                                // Convert int answers to strings based on options
                                val stringAnswers = surveyAnswers.answers.map { answer ->
                                    when (answer) {
                                        1 -> "Strongly Disagree"
                                        2 -> "Disagree"
                                        3 -> "Neutral"
                                        4 -> "Agree"
                                        5 -> "Strongly Agree"
                                        else -> "Please select your answer"
                                    }
                                }
                                surveyAnswersList.addAll(stringAnswers)
                            }
                        }
                        // Update the answers array with fetched responses
                        answers = surveyAnswersList.map { mutableStateOf(it) }
                    } else {
                        Log.d("SurveyScreen", "No matching documents found for the userId")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("SurveyScreen", "Error getting survey responses", exception)
                }
        } else {
            Log.d("SurveyScreen", "User not authenticated or UID is null")
        }
    }


    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        // Content of your screen
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
        Text("Survey Questions", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(32.dp))

        questions.forEachIndexed { index, question ->
            QuestionAnswerItem(
                question = question,
                answer = answers[index],
                onAnswerChanged = { newValue -> answers[index].value = newValue },
                options = QuestionOptions
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Save button
        Button(
            onClick = {
                val allAnswered = answers.all { it.value != "Please select your answer" }
                if (allAnswered) {
                    surveyComplete = true
                    saveSurveyResponses(questions.mapIndexed { index, question ->
                        SurveyAnswer(
                            question = question,
                            answer = when (answers[index].value) {
                                "Strongly Disagree" -> 1
                                "Disagree" -> 2
                                "Neutral" -> 3
                                "Agree" -> 4
                                "Strongly Agree" -> 5
                                else -> 0 // This should never be hit due to the allAnswered check
                            }
                        )
                    })
                } else {
                    surveyComplete = false // Set to false if not all questions are answered
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE1474E)),
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            Text("Save")
        }
        if (!surveyComplete) {
            Text(
                "Please answer all questions before saving.",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
