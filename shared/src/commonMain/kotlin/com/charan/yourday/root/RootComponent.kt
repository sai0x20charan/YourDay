package com.charan.yourday.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.presentation.home.HomeScreenComponent
import com.charan.yourday.presentation.onboarding.OnBoardingScreenComponent
import com.charan.yourday.presentation.settings.SettingsScreenComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class RootComponent(
    var authorizationId: String? = null,
    var errorCode: String? = null,
    componentContext: ComponentContext
) : ComponentContext by componentContext, KoinComponent {
    private val userPreferencesRepository: UserPreferencesRepository = get()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        coroutineScope.launch {
            val shouldShowOnBoarding = userPreferencesRepository.shouldShowOnboarding.first()
            if (shouldShowOnBoarding) navigation.replaceCurrent(Configuration.OnBoardingScreen(authorizationId, errorCode))
        }
    }

    private val navigation = StackNavigation<Configuration>()
    val childStack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration =
             Configuration.HomeScreen(authorizationId, errorCode),
        handleBackButton = true,
        childFactory = ::createChild,
    )

    fun onBackClicked() {
        navigation.pop()
    }

    private fun createChild(
        config: Configuration,
        context: ComponentContext
    ): Child {
        return when (config) {
            is Configuration.HomeScreen -> Child.HomeScreen(
                HomeScreenComponent(
                    componentContext = context,
                    authorizationId = config.authorizationId,
                    errorCode = config.errorCode,
                    onSettingsOpen = {
                        navigation.pushNew(Configuration.SettingsScreen)
                    }
                )
            )
            Configuration.SettingsScreen -> Child.SettingsScreen(
                SettingsScreenComponent(
                    componentContext = context,
                    onBackClick = {
                        onBackClicked()
                    },
                    onLicenseClick = {
                        navigation.pushNew(Configuration.LicenseScreen)
                    }
                )
            )

            Configuration.LicenseScreen -> Child.LicenseScreen(
                SettingsScreenComponent(
                    componentContext = context,
                    onBackClick = {
                        onBackClicked()
                    }
                )
            )

            is Configuration.OnBoardingScreen -> Child.OnBoardingScreen(
                component = OnBoardingScreenComponent(
                    componentContext = context,
                    authorizationId = config.authorizationId,
                    onFinish = {
                        finishOnBoard()
                    }
                )
            )
        }
    }

    private fun finishOnBoard() = coroutineScope.launch {
        userPreferencesRepository.setShouldShowOnboarding(false)
        navigation.replaceAll(Configuration.HomeScreen(authorizationId, errorCode))
    }

    sealed class Child {
        data class HomeScreen(val component: HomeScreenComponent) : Child()
        data class SettingsScreen(val component: SettingsScreenComponent) : Child()
        data class LicenseScreen(val component: SettingsScreenComponent) : Child()
        data class OnBoardingScreen(val component: OnBoardingScreenComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data class HomeScreen(val authorizationId: String?, val errorCode: String?) : Configuration()
        @Serializable
        object SettingsScreen : Configuration()
        @Serializable
        object LicenseScreen : Configuration()
        @Serializable
        data class OnBoardingScreen(val authorizationId: String?, val error: String?) : Configuration()
    }
}
