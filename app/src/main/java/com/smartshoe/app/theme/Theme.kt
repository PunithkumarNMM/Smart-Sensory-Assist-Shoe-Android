package com.smartshoe.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(

    primary = Primary,

    secondary = Secondary,

    background = Background,

    surface = Surface,

    error = Error

)

private val DarkColors = darkColorScheme(

    primary = Primary,

    secondary = Secondary,

    background = DarkBackground,

    surface = DarkSurface,

    error = Error

)

@Composable
fun SMARTSENSORYASSISTSHOETheme(

    darkTheme: Boolean = isSystemInDarkTheme(),

    content: @Composable () -> Unit

) {

    MaterialTheme(

        colorScheme =
            if (darkTheme)
                DarkColors
            else
                LightColors,

        typography = AppTypography,

        shapes = AppShapes,

        content = content

    )

}