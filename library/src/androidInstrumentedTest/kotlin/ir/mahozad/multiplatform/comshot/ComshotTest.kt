package ir.mahozad.multiplatform.comshot

import android.view.ViewGroup
import android.widget.Button
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Semaphore

@RunWith(AndroidJUnit4::class)
class ComshotTest {

    @get:Rule
    // This rule automatically creates an ActivityScenario and closes it on test finish
    // Could manually create and close the scenario like below:
    // val activityScenario = ActivityScenario.launch(ComshotTestActivity::class.java)
    var activityScenarioRule = ActivityScenarioRule(ComshotTestActivity::class.java)

    @Test
    fun exampleVisualTest() {
        // A Condition variable to prevent the activity to finish immediately and automatically
        var passed = false
        val semaphore = Semaphore(0)
        // activityScenarioRule.scenario.moveToState(Lifecycle.State.RESUMED)
        activityScenarioRule.scenario.onActivity {
            val fail = Button(it).apply { text = "Fail" }
            val pass = Button(it).apply { text = "Pass" }
            it.addContentView(fail, ViewGroup.LayoutParams(200, 100))
            it.addContentView(pass, ViewGroup.LayoutParams(200, 100))
            fail.setOnClickListener {
                passed = false
                semaphore.release()
            }
            pass.setOnClickListener {
                passed = true
                semaphore.release()
            }
        }
        semaphore.acquire()
        assert(passed)
    }
}
