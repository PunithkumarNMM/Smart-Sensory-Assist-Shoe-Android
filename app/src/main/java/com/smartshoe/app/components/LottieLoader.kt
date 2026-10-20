package com.smartshoe.app.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieLoader(
    fileName: String,
    modifier: Modifier = Modifier
) {

    val composition = rememberLottieComposition(
        LottieCompositionSpec.Asset(fileName)
    )

    LottieAnimation(
        composition = composition.value,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
    )
}