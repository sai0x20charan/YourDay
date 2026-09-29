package com.charan.yourday

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.FaultyDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.charan.yourday.presentation.home.HomeScreen
import com.charan.yourday.presentation.onboarding.OnBoardingScreen
import com.charan.yourday.presentation.settings.LicenseScreen
import com.charan.yourday.presentation.settings.SettingsScreen
import com.charan.yourday.root.RootComponent
import com.charan.yourday.ui.theme.AppTheme
import com.charan.yourday.ui.theme.slideAndFade

@OptIn(ExperimentalDecomposeApi::class, FaultyDecomposeApi::class)
@Composable
fun App(root: RootComponent) {
    AppTheme {
        Surface {
            Children(
                stack = root.childStack,
                animation = predictiveBackAnimation(
                    backHandler = root.backHandler,
                    fallbackAnimation = stackAnimation { _, _, _ ->
                        slideAndFade()
                    },
                    selector = { backEvent, _, _ -> androidPredictiveBackAnimatableV2(backEvent) },
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
