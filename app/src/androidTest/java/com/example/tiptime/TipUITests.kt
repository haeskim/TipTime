package com.example.tiptime

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.example.tiptime.ui.theme.TipTimeTheme
import org.junit.Rule
import org.junit.Test
import java.text.NumberFormat
import java.util.Locale

class TipUITests {

    @get:Rule
    val composeTestRule = createComposeRule()

    /*컴파일러는 androidTest디렉터리의 Test주석 메서드는 계측 테스트를,
    test디렉터리의 Test주석 메서드는 로컬 테스트를 나타낸다고 인식한다
     */
    @Test
    fun calculate_20_percent_tip() {
        composeTestRule.setContent() {
            TipTimeTheme {
                TipTimeLayout()
            }
        }
        composeTestRule.onNodeWithText("Bill Amount")
            .performTextInput("10")
        composeTestRule.onNodeWithText("Tip Percentage").performTextInput("20")
        val expectedTip = NumberFormat.getCurrencyInstance(Locale.US).format(2)
        composeTestRule.onNodeWithText("Tip Amount: $expectedTip").assertExists(
            "No node with this text was found."
        )
    }
}