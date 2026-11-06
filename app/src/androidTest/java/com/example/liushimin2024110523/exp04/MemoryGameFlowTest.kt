package com.example.liushimin2024110523.exp04

import android.content.Intent
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

/** Public user flows; assertions use visible state rather than controller internals. */
@RunWith(AndroidJUnit4::class)
class MemoryGameFlowTest {
    @Test
    fun savedProfileReturnsToHomeAndSurvivesRecreation() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.btn_edit_info)).perform(reveal(), click())
            onView(withId(R.id.et_name)).perform(reveal(), replaceText("林见微"), closeSoftKeyboard())
            onView(withId(R.id.btn_save)).perform(reveal(), click())
            onView(withId(R.id.tv_name)).check(matches(withText("林见微")))

            scenario.recreate()
            onView(withId(R.id.tv_name)).check(matches(withText("林见微")))
        }
    }

    @Test
    fun cancellingEditKeepsTheOriginalProfile() {
        ActivityScenario.launch(MainActivity::class.java).use {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            onView(withId(R.id.btn_edit_info)).perform(reveal(), click())
            onView(withId(R.id.et_name)).perform(reveal(), replaceText("未保存的修改"), closeSoftKeyboard())
            onView(withId(R.id.btn_cancel)).perform(reveal(), click())
            onView(withId(R.id.tv_name)).check(matches(withText(context.getString(R.string.default_name))))
        }
    }

    @Test
    fun anUnsavedDraftSurvivesEditActivityRecreation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, EditInfoActivity::class.java).apply {
            putExtra(context.getString(R.string.extra_name), context.getString(R.string.default_name))
            putExtra(context.getString(R.string.extra_student_id), context.getString(R.string.default_student_id))
        }
        ActivityScenario.launch<EditInfoActivity>(intent).use { scenario ->
            onView(withId(R.id.et_name)).perform(reveal(), replaceText("许知遥"), closeSoftKeyboard())
            scenario.recreate()
            onView(withId(R.id.et_name)).check(matches(withText("许知遥")))
        }
    }

    @Test
    fun emptyNameShowsAnInlineErrorAndDoesNotLeaveTheForm() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btn_edit_info)).perform(reveal(), click())
            onView(withId(R.id.et_name)).perform(reveal(), replaceText(""), closeSoftKeyboard())
            onView(withId(R.id.btn_save)).perform(reveal(), click())
            onView(withId(R.id.input_name)).check(matches(hasInputError()))
        }
    }

    @Test
    fun aFlippedCardAndScoreSurviveGameActivityRecreation() {
        ActivityScenario.launch(MemoryGameActivity::class.java).use { scenario ->
            var startingScore = ""
            var flippedScore = ""
            var hiddenDescription = ""
            var openDescription = ""
            onView(withId(R.id.grid_cards)).check { view, noViewFound ->
                if (noViewFound != null) throw noViewFound
                hiddenDescription = (view as RecyclerView).getChildAt(0).contentDescription.toString()
            }
            onView(withId(R.id.tv_score)).check { view, noViewFound ->
                if (noViewFound != null) throw noViewFound
                startingScore = (view as TextView).text.toString()
            }
            onView(withId(R.id.grid_cards)).perform(clickFirstCard())
            onView(withId(R.id.tv_score)).check { view, noViewFound ->
                if (noViewFound != null) throw noViewFound
                flippedScore = (view as TextView).text.toString()
            }
            assertEquals(startingScore, flippedScore)
            onView(withId(R.id.grid_cards)).check { view, noViewFound ->
                if (noViewFound != null) throw noViewFound
                openDescription = (view as RecyclerView).getChildAt(0).contentDescription.toString()
            }
            assertNotEquals(hiddenDescription, openDescription)

            scenario.recreate()
            onView(withId(R.id.tv_score)).check(matches(withText(flippedScore)))
            onView(withId(R.id.grid_cards)).check { view, noViewFound ->
                if (noViewFound != null) throw noViewFound
                val grid = view as RecyclerView
                assertEquals(24, grid.adapter!!.itemCount)
                assertEquals(openDescription, grid.getChildAt(0).contentDescription.toString())
            }
        }
    }

    @Test
    fun aFreshGameCanRestartAndExitToHome() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btn_play_game)).perform(reveal(), click())
            onView(withId(R.id.btn_restart)).perform(reveal(), click())
            onView(withId(R.id.grid_cards)).check { view, noViewFound ->
                if (noViewFound != null) throw noViewFound
                assertEquals(24, (view as RecyclerView).adapter!!.itemCount)
            }
            onView(withId(R.id.btn_exit)).perform(reveal(), click())
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            onView(withId(R.id.tv_name)).check(matches(withText(context.getString(R.string.default_name))))
        }
    }

    @Test
    fun homeAboutMenuShowsExperimentDetails() {
        ActivityScenario.launch(MainActivity::class.java).use {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu(context)
            onView(withText(R.string.about_title)).perform(click())
            onView(withText(R.string.about_message)).check(matches(withText(R.string.about_message)))
            onView(withText(R.string.got_it)).perform(click())
        }
    }

    /** Works with both ScrollView and NestedScrollView, including a resized keyboard viewport. */
    private fun reveal() = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isAssignableFrom(View::class.java)
        override fun getDescription() = "scroll the target into its ancestor viewport"
        override fun perform(uiController: UiController, view: View) {
            view.requestRectangleOnScreen(Rect(0, 0, view.width, view.height), true)
            uiController.loopMainThreadUntilIdle()
        }
    }

    private fun clickFirstCard() = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isAssignableFrom(RecyclerView::class.java)
        override fun getDescription() = "choose the first card in the game board"
        override fun perform(uiController: UiController, view: View) {
            val grid = view as RecyclerView
            assertEquals(24, grid.adapter!!.itemCount)
            grid.getChildAt(0).performClick()
            uiController.loopMainThreadUntilIdle()
        }
    }

    private fun hasInputError(): Matcher<View> = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("TextInputLayout displaying an inline validation error")
        }
        override fun matchesSafely(view: View): Boolean =
            view is TextInputLayout && !view.error.isNullOrBlank()
    }
}
