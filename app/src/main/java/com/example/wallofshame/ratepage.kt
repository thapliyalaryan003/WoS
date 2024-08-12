package com.example.wallofshame

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.runBlocking

data class Organization(val name: String, var upvotes: Int = 0, var hasUpvoted:Boolean, var hadDownvoted:Boolean)
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
        UpvoteScreen()
        Spacer(modifier = Modifier.height(8.dp))



}}
fun openNewWindow(response: String?,context: Context) {
    val intent = Intent(context, Airesponse::class.java)
    intent.putExtra("response", response)
    startActivity(context,intent,null)
}
@Composable
fun UpvoteScreen() {
    val organizations = remember {
        mutableStateOf(
            listOf(
                Organization("NTA", 0,false,false), // National Testing Agency
                Organization("CBSE", 0,false,false), // Central Board of Secondary Education
                Organization("CSIR", 0,false,false), // Council of Scientific and Industrial Research
                Organization("UGC", 0,false,false), // University Grants Commission
                Organization("IBPS", 0,false,false), // Institute of Banking Personnel Selection
                Organization("SSC", 0,false,false), // Staff Selection Commission
                Organization("UPSC", 0,false,false), // Union Public Service Commission
                Organization("AICTE", 0,false,false), // All India Council for Technical Education
                Organization("NEET", 0,false,false), // National Eligibility cum Entrance Test
                Organization("JEE", 0,false,false),
                // ... more organizations
            )
        )
    }
    // Initialize organizations with data
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
                        if(!org.hasUpvoted){org.upvotes++
                    sortOrganizations(organizations)
                        org.hasUpvoted = true
                        org.hadDownvoted = false}
                        else if(!org.hasUpvoted && org.hadDownvoted){org.upvotes+= 2
                            sortOrganizations(organizations)
                        org.hasUpvoted = true
                        org.hadDownvoted = false}
                    }) {
                        Image(painter = painterResource(id = R.drawable.upvote), contentDescription = "Upvote")
                    }
                    Button(onClick = { if(!org.hadDownvoted){org.upvotes--
                        sortOrganizations(organizations)
                        org.hadDownvoted = true
                        org.hasUpvoted = false}
                    else if(org.hasUpvoted && !org.hadDownvoted){org.upvotes-= 2
                        sortOrganizations(organizations)
                        org.hasUpvoted = false
                        org.hadDownvoted = true}
                        }) {
                        Image(painter = painterResource(id = R.drawable.downvote), contentDescription = "Downvote")
                    }
                }
            }
        }}}

    fun sortOrganizations(organizations: MutableList<Organization>) {
        organizations.sortByDescending { it.upvotes }
    }

