package com.example.wallofshame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity


class WOS_Category : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        setContent {
            WOSCategoryScreen(modifier= Modifier, context)
        }
    }
}

@Composable
fun WOSCategoryScreen(modifier: Modifier = Modifier, context: Context ) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) { Image(painter = painterResource(id = R.drawable.trophy), contentDescription = "trophy")
        AwardButton(text = "Award 1: Master of Messy Exams", context)
        AwardButton(text = "Award 2: Call Wait Time World Record Holder", context)
        AwardButton(text = "Award 3: Inverse Customer Care Champion ", context)
    }
}

@Composable
fun AwardButton(text: String, context: Context) {
    Button(
        onClick = { val intent = Intent(context, ratepage::class.java)
            intent.putExtra("award_category", text)
            startActivity(context,intent,null) },
        modifier = Modifier.padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD4AF37), // Gold color
            contentColor = Color.Black
        )
    ) {
        Text(text = text)
    }
}

