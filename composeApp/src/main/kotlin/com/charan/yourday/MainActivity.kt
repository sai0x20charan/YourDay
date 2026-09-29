package com.charan.yourday

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.handleDeepLink
import com.charan.yourday.root.RootComponent

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        val root =
            handleDeepLink { uri ->
                val authenticationCode = uri?.extractTodoistAuthentication()
                val error = uri?.extractErrorForTodoist()
                RootComponent(
                    componentContext = defaultComponentContext(
                        discardSavedState = authenticationCode != null,
                    ),
                    authorizationId = authenticationCode,
                    errorCode = error
                )
            } ?: return
        setContent {
            App(root)
        }
    }
    private fun Uri.extractTodoistAuthentication(): String? {
        val code = this.getQueryParameter("code")
        return code

    }

    private fun Uri.extractErrorForTodoist() : String? {
        val error = this.getQueryParameter("error")
        return error
    }


}
