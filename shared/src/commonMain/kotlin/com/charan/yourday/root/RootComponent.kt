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
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.charan.yourday.data.repository.DataStoreRepository
import com.charan.yourday.data.repository.LocalLLMRepository
import com.charan.yourday.presentation.home.HomeScreenComponent
import com.charan.yourday.presentation.settings.SettingsScreenComponent
import com.charan.yourday.utils.UserPreferencesStore
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
) : ComponentContext by componentContext, KoinComponent{
    private val userPreferences: UserPreferencesStore = get()
    private val dataStoreRepository : DataStoreRepository = get()
    private val localLLMRepository : LocalLLMRepository = get()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    init {
        coroutineScope.launch {
            val shouldShowOnBoarding = userPreferences.shouldShowOnboarding.first()
            if(shouldShowOnBoarding) navigation.replaceCurrent(Configuration.OnBoardingScreen(authorizationId,errorCode))
            dataStoreRepository.setModelDownloaded(localLLMRepository.isModelDownloaded())

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
    fun onBackClicked(){
        navigation.pop()
    }

    private fun createChild(
        config:  Configuration,
        context : ComponentContext
    ) : Child{
        return when(config) {
            is Configuration.HomeScreen -> Child.HomeScreen(
                HomeScreenComponent(
                    componentContext=context,
                    authorizationId = config.authorizationId,
                    errorCode = config.errorCode,
                    onSettingsOpen = {
                        navigation.pushNew(Configuration.SettingsScreen)
                    },
                    isResumed = lifecycle.state == Lifecycle.State.RESUMED
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
                ))

            Configuration.LicenseScreen -> Child.LicenseScreen(
                SettingsScreenComponent(
                componentContext = context,
                    onBackClick = {
                        onBackClicked()
                    }
                )
            )

            is Configuration.OnBoardingScreen -> Child.OnBoardingScreen(
                component = HomeScreenComponent(
                    componentContext = context,
                    authorizationId = config.authorizationId,
                    errorCode = config.error,
                    onBoardFinish = {
                        finishOnBoard()
                    },
                    isResumed = false

                )
            )
        }

    }

    private fun finishOnBoard() = coroutineScope.launch {
        userPreferences.setShouldShowOnboarding(false)
        navigation.replaceAll(Configuration.HomeScreen(authorizationId,errorCode))
    }

    sealed class Child {
        data class HomeScreen(val component: HomeScreenComponent) : Child()
        data class SettingsScreen(val component : SettingsScreenComponent) : Child()
        data class LicenseScreen(val component : SettingsScreenComponent) : Child()
        data class OnBoardingScreen(val component: HomeScreenComponent) : Child()
    }
    @Serializable
    sealed class  Configuration {
        @Serializable
        data class HomeScreen(val authorizationId : String?,val errorCode : String?) : Configuration()
        @Serializable
        object SettingsScreen : Configuration()
        @Serializable
        object LicenseScreen : Configuration()
        @Serializable
        data class OnBoardingScreen(val authorizationId: String?,val error : String ?) : Configuration()
    }


}