import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.unit.sp
import com.example.uwrizz.R

@Composable
fun SurveyScreen(
    onNavigateToProfile: () -> Unit
) {
    val scrollState = rememberScrollState()

    var Question1 by remember { mutableStateOf(false) }
    var Question2 by remember { mutableStateOf(false) }
    var Question3 by remember { mutableStateOf(false) }
    var Question4 by remember { mutableStateOf(false) }
    var Question5 by remember { mutableStateOf(false) }
    var Question6 by remember { mutableStateOf(false) }
    var Question7 by remember { mutableStateOf(false) }
    var Question8 by remember { mutableStateOf(false) }
    var Question9 by remember { mutableStateOf(false) }
    var Question10 by remember { mutableStateOf(false) }
    val QuestionOptions = listOf("Strongly Disagree", "Disagree", "Neutral", "Agree", "Strongly Agree") // Define your options here
    var answer1 by remember { mutableStateOf("Please select your answer") }
    var answer2 by remember { mutableStateOf("Please select your answer") }
    var answer3 by remember { mutableStateOf("Please select your answer") }
    var answer4 by remember { mutableStateOf("Please select your answer") }
    var answer5 by remember { mutableStateOf("Please select your answer") }
    var answer6 by remember { mutableStateOf("Please select your answer") }
    var answer7 by remember { mutableStateOf("Please select your answer") }
    var answer8 by remember { mutableStateOf("Please select your answer") }
    var answer9 by remember { mutableStateOf("Please select your answer") }
    var answer10 by remember { mutableStateOf("Please select your answer") }
    val labelQ1: String = "I enjoy spending time outdoors and engaging in adventurous activities."
    val labelQ2: String = "I enjoy maintaining a healthy lifestyle, including regular exercise and a balanced diet."
    val labelQ3: String = "I enjoy quiet, cozy nights in rather than going out to crowded social events."
    val labelQ4: String = "I enjoy deep conversations and meaningful connections over superficial interactions."
    val labelQ5: String = "I enjoy traveling and exploring new cultures as an essential part of my life."
    val labelQ6: String = "I enjoy trying new cuisines and experimenting with different flavors."
    val labelQ7: String = "I enjoy prioritizing spending quality time with family and close friends."
    val labelQ8: String = "I enjoy open communication and honesty in a relationship."
    val labelQ9: String = "I enjoy valuing ambition and striving to achieve my goals."
    val labelQ10: String = "I enjoy spontaneity and embracing new experiences without hesitation."





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
        Text("Survey Questions", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(32.dp))

        //Question1
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
            ) {
                Text(labelQ1, style = MaterialTheme.typography.h5.copy(fontSize = 16.sp))

                OutlinedTextField(
                    value = answer1,
                    onValueChange = { /* ReadOnly TextField */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { Question1 = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { Question1 = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = Question1,
                    onDismissRequest = { Question1 = false }
                ) {
                    QuestionOptions.forEach { question1 ->
                        DropdownMenuItem(onClick = {
                            answer1 = question1
                            Question1 = false
                        }) {
                            Text(text = question1)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        //Question2
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
            ) {
                Text(labelQ2, style = MaterialTheme.typography.h5.copy(fontSize = 16.sp))

                OutlinedTextField(
                    value = answer2,
                    onValueChange = { /* ReadOnly TextField */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { Question2 = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { Question2 = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = Question2,
                    onDismissRequest = { Question2 = false }
                ) {
                    QuestionOptions.forEach { question2 ->
                        DropdownMenuItem(onClick = {
                            answer2 = question2
                            Question2 = false
                        }) {
                            Text(text = question2)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        //Question3
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
            ) {
                Text(labelQ3, style = MaterialTheme.typography.h5.copy(fontSize = 16.sp))

                OutlinedTextField(
                    value = answer3,
                    onValueChange = { /* ReadOnly TextField */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { Question3 = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { Question3 = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = Question3,
                    onDismissRequest = { Question3 = false }
                ) {
                    QuestionOptions.forEach { question3 ->
                        DropdownMenuItem(onClick = {
                            answer3 = question3
                            Question3 = false
                        }) {
                            Text(text = question3)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        //Question4
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
            ) {
                Text(labelQ4, style = MaterialTheme.typography.h5.copy(fontSize = 16.sp))

                OutlinedTextField(
                    value = answer4,
                    onValueChange = { /* ReadOnly TextField */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { Question2 = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { Question4 = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = Question4,
                    onDismissRequest = { Question4 = false }
                ) {
                    QuestionOptions.forEach { question4 ->
                        DropdownMenuItem(onClick = {
                            answer4 = question4
                            Question1 = false
                        }) {
                            Text(text = question4)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        //Question5
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
            ) {
                Text(labelQ5, style = MaterialTheme.typography.h5.copy(fontSize = 16.sp))

                OutlinedTextField(
                    value = answer5,
                    onValueChange = { /* ReadOnly TextField */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { Question5 = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { Question5 = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = Question5,
                    onDismissRequest = { Question5 = false }
                ) {
                    QuestionOptions.forEach { question5 ->
                        DropdownMenuItem(onClick = {
                            answer5 = question5
                            Question1 = false
                        }) {
                            Text(text = question5)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        //Question6
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
            ) {
                Text(labelQ6, style = MaterialTheme.typography.h5.copy(fontSize = 16.sp))

                OutlinedTextField(
                    value = answer6,
                    onValueChange = { /* ReadOnly TextField */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { Question6 = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { Question6 = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = Question6,
                    onDismissRequest = { Question6 = false }
                ) {
                    QuestionOptions.forEach { question6 ->
                        DropdownMenuItem(onClick = {
                            answer6 = question6
                            Question6 = false
                        }) {
                            Text(text = question6)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        //Question7
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
            ) {
                Text(labelQ7, style = MaterialTheme.typography.h5.copy(fontSize = 16.sp))

                OutlinedTextField(
                    value = answer7,
                    onValueChange = { /* ReadOnly TextField */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { Question7 = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { Question7 = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = Question7,
                    onDismissRequest = { Question7 = false }
                ) {
                    QuestionOptions.forEach { question7->
                        DropdownMenuItem(onClick = {
                            answer7 = question7
                            Question7 = false
                        }) {
                            Text(text = question7)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        //Question8
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
            ) {
                Text(labelQ8, style = MaterialTheme.typography.h5.copy(fontSize = 16.sp))

                OutlinedTextField(
                    value = answer8,
                    onValueChange = { /* ReadOnly TextField */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { Question2 = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { Question8 = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = Question8,
                    onDismissRequest = { Question8 = false }
                ) {
                    QuestionOptions.forEach { question8 ->
                        DropdownMenuItem(onClick = {
                            answer8 = question8
                            Question8 = false
                        }) {
                            Text(text = question8)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        //Question9
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
            ) {
                Text(labelQ9, style = MaterialTheme.typography.h5.copy(fontSize = 16.sp))

                OutlinedTextField(
                    value = answer9,
                    onValueChange = { /* ReadOnly TextField */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { Question2 = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { Question9 = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = Question9,
                    onDismissRequest = { Question9 = false }
                ) {
                    QuestionOptions.forEach { question9 ->
                        DropdownMenuItem(onClick = {
                            answer9 = question9
                            Question9 = false
                        }) {
                            Text(text = question9)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        //Question10
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
            ) {
                Text(labelQ10, style = MaterialTheme.typography.h5.copy(fontSize = 16.sp))

                OutlinedTextField(
                    value = answer10,
                    onValueChange = { /* ReadOnly TextField */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { Question2 = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { Question2 = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = Question10,
                    onDismissRequest = { Question10 = false }
                ) {
                    QuestionOptions.forEach { question10 ->
                        DropdownMenuItem(onClick = {
                            answer10 = question10
                            Question10 = false
                        }) {
                            Text(text = question10)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(70.dp))

        // Save button
        Button(
            onClick = {
                //action to be filled for the save button
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Save")
        }
    }
}

