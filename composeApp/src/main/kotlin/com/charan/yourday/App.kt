package com.charan.yourday

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.unit.IntOffset
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.FaultyDecomposeApi
import com.charan.yourday.presentation.home.HomeScreen
import com.charan.yourday.presentation.navigation.NavigationAppHost
import com.charan.yourday.root.RootComponent
import com.charan.yourday.utils.DateUtils
import com.example.compose.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.isEnter
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.charan.yourday.presentation.onboarding.OnBoardingScreen
import com.charan.yourday.presentation.settings.LicenseScreen
import com.charan.yourday.presentation.settings.SettingsScreen
import com.charan.yourday.ui.theme.slideAndFade
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinContext
import org.koin.compose.getKoin
import org.koin.compose.koinInject



@OptIn(ExperimentalPermissionsApi::class, ExperimentalDecomposeApi::class,
    FaultyDecomposeApi::class
)
@Composable
fun App(root: RootComponent) {
    KoinContext  {
        val permissionsController: PermissionsController = koinInject()
        BindEffect(permissionsController = permissionsController)

        AppTheme {
            Surface {
                Children(
                    stack = root.childStack,
                    animation = predictiveBackAnimation(
                        backHandler = root.backHandler,
                        fallbackAnimation = stackAnimation { current, target, direction ->
                            slideAndFade()
                        },
                        selector = { backEvent, _, _ -> androidPredictiveBackAnimatable(backEvent) },
                        onBack = root::onBackClicked,
                    ),
                ) { child ->

                    when (val instance = child.instance) {

                        is RootComponent.Child.HomeScreen -> HomeScreen(instance.component)
                        is RootComponent.Child.SettingsScreen -> SettingsScreen(instance.component)
                        is RootComponent.Child.LicenseScreen -> LicenseScreen(instance.component)
                        is RootComponent.Child.OnBoardingScreen -> OnBoardingScreen(instance.component)
                    }

                }
            }
        }

    }

}