package com.nononsenseapps.feeder.ui.compose.navigation

import androidx.navigation.NavController
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsDestinationTest {
    @MockK
    private lateinit var navController: NavController

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)
    }

    @Test
    fun settingsHasCorrectRoute() {
        assertEquals(
            "settings",
            SettingsDestination.route
        )
    }

    @Test
    fun settingsNavigateDefaults() {
        SettingsDestination.navigate(
            navController
        )

        verify {
            navController.navigate("settings")
        }
    }
}
