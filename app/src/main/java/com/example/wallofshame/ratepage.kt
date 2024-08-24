package com.example.wallofshame

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking

data class Organization(val name: String= "", var upvotes: Int = 0, var hasUpvoted:Boolean, var hadDownvoted:Boolean) {
    // Add an empty public constructor
    constructor() : this("",0, false, false)  // This calls the primary constructor with default values
}
class ratepage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var awardCategory = intent.getStringExtra("award_category") ?: "Default Category"
        awardCategory = trimString(inputString = awardCategory)

        setContent {
            AwardCategoryScreen(awardCategory, context = this) // Replace with actual award category
        }
    }
}

fun trimString(inputString: String): String {
    val colonIndex = inputString.indexOf(":")
    return if (colonIndex != -1) {
        inputString.substring(colonIndex + 1)
    } else {
        inputString
    }
}
fun sortOrganizations(organizations: MutableState<List<Organization>>) {
    organizations.value = organizations.value.sortedByDescending { it.upvotes }.toMutableStateList()
}
fun fetchOrganizations(db: FirebaseFirestore, onFetched: (List<Organization>) -> Unit) {
    db.collection("organizations")
        .get()
        .addOnSuccessListener { querySnapshot ->
            val organizations = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Organization::class.java) ?: null
            }
            Log.d("FetchOrganizations", "Fetched organizations: $organizations")
            onFetched(organizations) // Pass a read-only list to the callback
        }
        .addOnFailureListener { e ->
            Log.e("FetchOrganizations", "Error fetching organizations", e)
            onFetched(emptyList())
        }
}

@Composable
fun AwardCategoryScreen(awardCategory: String, context: Context) {

    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .padding(top = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(

        text = awardCategory,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 16.dp) ,
            fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
            color = Color.Cyan
        )

        Image(painter = (painterResource(id = R.drawable.crown)), contentDescription = "winner" )
        //Image(painter = )

        Button(
            onClick =
               {
                val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                    apiKey = BuildConfig.API_KEY
            )
                var prompt = "1.conduct sentimental analysis of people's social media posts for the topic $awardCategory in category BEST PUBLIC IMAGE, MODERATE, WORST PUBLIC IMAGE  the list of contendors are -> NTA, CBSE, CSIR, UGC, IBPS, SSC, UPSC, AICTE, NEET, JEE 2. Then list 2 strong points to justify your stand(can also mention source). 3. Write in easy english 4. Give output in presentable format don't include unwanted symbols like | , -  and keep whole content within 600 words"
                   runBlocking { val response = generativeModel.generateContent(prompt)
                       openNewWindow(context = context, response = response.text)
                   }
               })
    { Text(text = "AI Truth Check",
                modifier = Modifier)
        }
        UpvoteScreen(context)
        Spacer(modifier = Modifier.height(8.dp))



}}
fun openNewWindow(response: String?,context: Context) {
    val intent = Intent(context, Airesponse::class.java)
    intent.putExtra("response", response)
    startActivity(context,intent,null)
}
@Composable
fun UpvoteScreen(context: Context){
    FirebaseApp.initializeApp(context)
    val db = FirebaseFirestore.getInstance()

    val organizations = remember { mutableStateOf<List<Organization>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) } // Add a loading state

    // Fetch organizations from Firestore
    LaunchedEffect(key1 = Unit) {
        fetchOrganizations(db) { fetchedOrganizations ->
            organizations.value = fetchedOrganizations
            isLoading.value = false
        }
    }

    // Initialize organizations with data
    if (isLoading.value) {
        // Show a loading indicator while data is being fetched
        Text("Loading...")
    } else {
        // Display the LazyColumn when data is available
        LazyColumn {
            items(organizations.value) { org ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = org.name,
                    modifier = Modifier.padding(top = 12.dp),
                    fontSize = 20.sp,
                    color = Color.Cyan,
                    )
                Row {

                    Text(text= "${org.upvotes.toString()} ",
                        color = Color.White,
                        modifier = Modifier,
                        fontSize = 20.sp,
                        )
                    Button(onClick = {

                        if(!org.hasUpvoted && org.hadDownvoted){org.upvotes+= 2
                            sortOrganizations(organizations)
                        org.hasUpvoted = true
                        org.hadDownvoted = false}
                        else if(!org.hasUpvoted){org.upvotes++
                            sortOrganizations(organizations)
                            org.hasUpvoted = true
                            org.hadDownvoted = false}
                    }) {
                        Image(painter = painterResource(id = R.drawable.upvote), contentDescription = "Upvote")
                    }
                    Button(onClick = { if(org.hasUpvoted && !org.hadDownvoted){org.upvotes-= 2
                        sortOrganizations(organizations)
                        org.hasUpvoted = false
                        org.hadDownvoted = true}
                    else if(!org.hadDownvoted){org.upvotes--
                        sortOrganizations(organizations)
                        org.hadDownvoted = true
                        org.hasUpvoted = false}

                        }) {
                        Image(painter = painterResource(id = R.drawable.downvote), contentDescription = "Downvote")
                    }
                }
            }
        }
        organizations.value.forEach { org ->db.collection("organizations")
            .document(org.name) // Use the organization name as the document ID
            .set(org)
            .addOnSuccessListener {
                Log.d(TAG, "Organization ${org.name} added/updated successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding/updating organization ${org.name}", e)
            }
        } }
        }

   }

