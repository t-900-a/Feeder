package com.nononsenseapps.feeder.ui.compose

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.nononsenseapps.feeder.ui.MainActivity
import com.nononsenseapps.feeder.ui.compose.theme.FeederTheme
import com.nononsenseapps.feeder.ui.robots.feedScreen
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.kodein.di.compose.withDI

class StartingNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun backWillExitApp() {
        composeTestRule.setContent {
            FeederTheme {
                withDI {
                    composeTestRule.activity.appContent()
                }
            }
        }

        feedScreen {
            pressBackButton()
            assertTrue {
                isAppRunning
            }
        }
    }
}
