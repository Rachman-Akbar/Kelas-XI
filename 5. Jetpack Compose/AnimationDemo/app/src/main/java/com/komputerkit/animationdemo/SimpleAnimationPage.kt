package com.komputerkit.animationdemo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.view.animation.OvershootInterpolator

@Composable
fun SimpleAnimationPage() {
    val scale = remember { Animatable(0f) }
    val animateAgain = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = animateAgain.value) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = { progress -> OvershootInterpolator(2f).getInterpolation(progress) }
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(200.dp)
                .weight(1f)
                .scale(scale.value)
        )
        
        Button(
            onClick = {
                GlobalScope.launch {
                    scale.snapTo(0f)
                }
                animateAgain.value = !animateAgain.value
            },
            modifier = Modifier.padding(bottom = 64.dp)
        ) {
            Text("Animate")
        }
    }
}