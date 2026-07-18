package com.nimetatila.rencarapp_turkcell_gygy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.nimetatila.rencarapp_turkcell_gygy.data.preferences.ThemePreferenceRepository
import com.nimetatila.rencarapp_turkcell_gygy.ui.navigation.RenCarAppNavHost
import com.nimetatila.rencarapp_turkcell_gygy.ui.screens.SplashScreen
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.RenCarAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferenceRepository: ThemePreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themePreferenceRepository.isDarkTheme.collectAsState(initial = true)

            RenCarAppTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RenCarAppNavHost(
                        navController = navController,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = {
                            lifecycleScope.launch {
                                themePreferenceRepository.setDarkTheme(!isDarkTheme)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Welcome Screen Preview")
@Composable
fun AppWelcomePreview() {
    RenCarAppTheme {
        SplashScreen(
            onRegisterClick = {},
            onLoginClick = {},
            isDarkTheme = false
        )
    }
}