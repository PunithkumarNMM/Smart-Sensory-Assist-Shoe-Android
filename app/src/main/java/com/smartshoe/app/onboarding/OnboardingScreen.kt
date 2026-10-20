package com.smartshoe.app.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun OnboardingScreen(
    navController: NavController
) {

    val pages = listOf(

        OnboardingData(
            "Live GPS Tracking",
            "Track your child in real time anywhere.",
            "📍"
        ),

        OnboardingData(
            "AI Safety",
            "AI detects falls and dangerous situations.",
            "🧠"
        ),

        OnboardingData(
            "Instant SOS",
            "Emergency alerts are sent immediately to guardians.",
            "🚨"
        )

    )

    val pagerState = rememberPagerState(
        pageCount = { pages.size }
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) {

            OnboardingPage(
                pages[it]
            )

        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(55.dp),
            onClick = {

                if (pagerState.currentPage == pages.lastIndex) {

                    navController.navigate("login") {

                        popUpTo("onboarding") {
                            inclusive = true
                        }

                    }

                }

            }
        ) {

            if (pagerState.currentPage == pages.lastIndex)

                Text("Get Started")

            else

                Text("Swipe →")

        }

    }

}