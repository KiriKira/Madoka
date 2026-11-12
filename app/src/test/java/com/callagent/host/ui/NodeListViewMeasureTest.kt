package com.callagent.host.ui

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.view.ContextThemeWrapper
import com.callagent.host.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NodeListViewMeasureTest {
    @Test
    fun everyNodeIsLaidOutEvenBelowTheFoldOfAScrollingPage() {
        val context = ContextThemeWrapper(RuntimeEnvironment.getApplication(), R.style.AppTheme)
        val rows = (1..5).map { index ->
            NodeRow(
                key = "node-$index",
                title = "$index. node-$index.example.org",
                detail = "备用 · 42 ms · 可通话 · FCM",
                active = index == 1,
                healthy = true,
                removable = index != 1,
                quality = "Kyubey 正常 · 本机已收到 · 刚刚"
            )
        }
        val list = NodeListView(context, rows, onReorder = {}, onRemove = {})
        // The settings page: content above the node list, then the list, in a scroll view
        // whose viewport leaves the list far less room than its five rows need.
        val content = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
        content.addView(View(context), LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 700))
        content.addView(list, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        val centered = FrameLayout(context).apply {
            addView(content, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }
        val scroll = ScrollView(context).apply {
            isFillViewport = true
            addView(centered, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        scroll.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY)
        )
        scroll.layout(0, 0, 1080, 1000)

        assertEquals(5, list.childCount)
        val lastBottom = (0 until list.childCount).maxOf { list.getChildAt(it).bottom }
        assertTrue("list height ${list.height} cuts off the last row ending at $lastBottom", list.height >= lastBottom)
    }
}
