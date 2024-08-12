package com.example.wallofshame

import android.graphics.fonts.FontStyle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wallofshame.ui.theme.WallofShameTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Airesponse : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var response = intent.getStringExtra("response") ?: ""
        setContent{
            WallofShameTheme {
                aiResponse(modifier= Modifier
                    .padding(all = 16.dp)
                    .wrapContentSize(), response= response)
            }
        }

    }
}
@Composable
fun aiResponse( modifier: Modifier = Modifier, response: String){
    Surface (color = Color.DarkGray,
        contentColor = Color.White){

        Text(text = response,
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            fontSize = 20.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Start, )

    }

}