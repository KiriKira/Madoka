package com.callagent.host.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.callagent.host.contacts.ContactDirectory
import com.callagent.host.contacts.ContactEntry
import com.callagent.host.contacts.ContactPerson
import com.callagent.host.contacts.ContactSection
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.R as MaterialR
import kotlin.math.ceil

/**
 * The phone book shared by the dialer and the new message screen: every
 * contact A to Z under letter headers, narrowed by [setQuery], with a letter
 * index on the right edge that jumps the page to a letter, like a phone app.
 *
 * The list lives in the page's scroll view, so the index draws itself over
 * the part of the list that is on screen and follows the page as it scrolls.
 * Rows are built once per phone book and reused while the query changes.
 */
@SuppressLint("ViewConstructor")
class ContactPickerView(context: Context) : LinearLayout(context) {
    /** A contact and the number chosen for it (asked for when it has several). */
    var onPick: (ContactPerson, ContactEntry) -> Unit = { _, _ -> }

    private var people: List<ContactPerson>? = null
    private var query = ""
    private var labels: List<String> = emptyList()
    private val headers = HashMap<String, TextView>()
    private val rows = HashMap<Long, Row>()
    private val shownHeaders = HashMap<String, View>()

    private val stripWidth = dp(28)
    private val letterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bubbleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }

    /** Letter column in the scroll view's own coordinates; frozen while a finger is on it. */
    private var columnTop = 0f
    private var cell = 0f
    private var tracking = false
    private var touchLabel: String? = null
    private var touchViewportY = 0f
    private val scrollListener = ViewTreeObserver.OnScrollChangedListener { if (labels.isNotEmpty()) invalidate() }

    private class Row(val view: LinearLayout, val subtitle: TextView) {
        var person: ContactPerson? = null
    }

    init {
        orientation = VERTICAL
        // Rows end before the letter column, which [rebuild] makes room for.
        setWillNotDraw(false)
    }

    /** Shows a new phone book; the same one again changes nothing. */
    fun setPeople(next: List<ContactPerson>) {
        if (next == people) return
        people = next
        rows.clear()
        rebuild()
    }

    fun setQuery(next: String) {
        val trimmed = next.trim()
        if (trimmed == query) return
        query = trimmed
        rebuild()
    }

    private fun rebuild() {
        removeAllViews()
        shownHeaders.clear()
        val all = people ?: return
        val sections = ContactDirectory.sections(ContactDirectory.filter(all, query))
        labels = if (query.isEmpty() && sections.size > 1) sections.map(ContactSection::label) else emptyList()
        setPadding(0, 0, if (labels.isEmpty()) 0 else stripWidth, 0)
        if (sections.isEmpty()) {
            addView(MaterialTextView(context).apply {
                text = if (query.isEmpty()) "通讯录中没有带号码的联系人。" else "没有匹配的联系人。"
                gravity = Gravity.CENTER
                setTextAppearance(MaterialR.style.TextAppearance_Material3_BodyMedium)
                setTextColor(color(MaterialR.attr.colorOnSurfaceVariant))
                setPadding(dp(16), dp(16), dp(16), dp(16))
            }, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            invalidate()
            return
        }
        sections.forEach { section ->
            val header = headers.getOrPut(section.label) {
                HostUi.groupHeader(context, section.label).also { ViewCompat.setAccessibilityHeading(it, true) }
            }
            addView(header)
            shownHeaders[section.label] = header
            section.people.forEachIndexed { index, person ->
                val row = rows.getOrPut(person.contactId) { buildRow(person) }
                row.person = person
                row.subtitle.text = numbersText(person)
                val position = HostUi.position(index, section.people.size)
                row.view.background = HostUi.tileBackground(context, position)
                addView(row.view, HostUi.tileParams(context, position))
            }
        }
        invalidate()
    }

    private fun buildRow(person: ContactPerson): Row {
        val view = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            minimumHeight = dp(64)
            setPadding(dp(16), dp(10), dp(16), dp(10))
            isClickable = true
            isFocusable = true
        }
        val first = person.numbers.first()
        view.addView(HostUi.avatar(context, person.name, first.normalizedNumber ?: first.number),
            LayoutParams(dp(40), dp(40)).apply { marginEnd = dp(16) })
        val texts = LinearLayout(context).apply { orientation = VERTICAL }
        texts.addView(HostUi.title(context, person.name))
        val subtitle = HostUi.subtitle(context, "")
        texts.addView(subtitle)
        view.addView(texts, LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        val row = Row(view, subtitle)
        view.setOnClickListener { row.person?.let(::pick) }
        return row
    }

    private fun numbersText(person: ContactPerson): String =
        if (person.numbers.size == 1) person.numbers.first().number
        else "${person.numbers.first().number} 等 ${person.numbers.size} 个号码"

    private fun pick(person: ContactPerson) {
        if (person.numbers.size == 1) {
            onPick(person, person.numbers.first())
            return
        }
        MaterialAlertDialogBuilder(context)
            .setTitle(person.name)
            .setItems(person.numbers.map { it.number }.toTypedArray()) { _, which ->
                person.numbers.getOrNull(which)?.let { onPick(person, it) }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnScrollChangedListener(scrollListener)
    }

    override fun onDetachedFromWindow() {
        viewTreeObserver.removeOnScrollChangedListener(scrollListener)
        tracking = false
        touchLabel = null
        super.onDetachedFromWindow()
    }

    private fun scrollParent(): ScrollView? {
        var current = parent
        while (current != null) {
            if (current is ScrollView) return current
            current = current.parent
        }
        return null
    }

    /** This view's top in the scroll view's content. */
    private fun topInScroll(scroll: ScrollView): Int {
        var y = 0
        var view: View = this
        while (true) {
            y += view.top
            val next = view.parent
            if (next === scroll || next !is View) break
            view = next
        }
        return y
    }

    /** Places the letter column over the visible part of the list; false when there is no room. */
    private fun layoutColumn(scroll: ScrollView): Boolean {
        if (labels.isEmpty()) return false
        val offset = topInScroll(scroll) - scroll.scrollY
        val visibleTop = maxOf(0, offset) + dp(8)
        val visibleBottom = minOf(scroll.height - scroll.paddingBottom, offset + height) - dp(8)
        val available = (visibleBottom - visibleTop).toFloat()
        if (available < dp(96)) return false
        cell = minOf(dp(22).toFloat(), available / labels.size)
        columnTop = visibleTop + (available - cell * labels.size) / 2f
        return true
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val scroll = scrollParent() ?: return
        if (!tracking && !layoutColumn(scroll)) return
        if (labels.isEmpty()) return
        // Viewport coordinates to this view's.
        val shift = (scroll.scrollY - topInScroll(scroll)).toFloat()
        val centerX = width - stripWidth / 2f
        letterPaint.textSize = minOf(cell * 0.72f, sp(12f))
        // Too many letters for the height: show every few and dots between.
        val step = ceil(sp(10f) / cell).toInt().coerceAtLeast(1)
        labels.forEachIndexed { index, label ->
            val middle = columnTop + cell * (index + 0.5f) + shift
            val active = label == touchLabel
            letterPaint.color = color(if (active) androidx.appcompat.R.attr.colorPrimary else MaterialR.attr.colorOnSurfaceVariant)
            val text = if (index % step == 0 || active) label else "·"
            canvas.drawText(text, centerX, middle - (letterPaint.ascent() + letterPaint.descent()) / 2f, letterPaint)
        }
        val current = touchLabel ?: return
        val radius = dp(28).toFloat()
        val bubbleX = width - stripWidth - dp(16) - radius
        val bubbleY = (touchViewportY + shift).coerceIn(radius + shift, shift + scroll.height - scroll.paddingBottom - radius)
        bubblePaint.color = color(MaterialR.attr.colorPrimaryContainer)
        canvas.drawCircle(bubbleX, bubbleY, radius, bubblePaint)
        bubbleTextPaint.color = color(MaterialR.attr.colorOnPrimaryContainer)
        bubbleTextPaint.textSize = sp(24f)
        canvas.drawText(current, bubbleX, bubbleY - (bubbleTextPaint.ascent() + bubbleTextPaint.descent()) / 2f, bubbleTextPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val scroll = scrollParent() ?: return super.dispatchTouchEvent(event)
        val viewportY = event.y + topInScroll(scroll) - scroll.scrollY
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x < width - stripWidth - dp(4) || !layoutColumn(scroll)) return super.dispatchTouchEvent(event)
                if (viewportY < columnTop - dp(12) || viewportY > columnTop + cell * labels.size + dp(12)) {
                    return super.dispatchTouchEvent(event)
                }
                tracking = true
                parent?.requestDisallowInterceptTouchEvent(true)
                jumpTo(viewportY, scroll)
                return true
            }
            MotionEvent.ACTION_MOVE -> if (tracking) {
                jumpTo(viewportY, scroll)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (tracking) {
                tracking = false
                touchLabel = null
                invalidate()
                return true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun jumpTo(viewportY: Float, scroll: ScrollView) {
        if (labels.isEmpty() || cell <= 0f) return
        touchViewportY = viewportY
        val label = labels[((viewportY - columnTop) / cell).toInt().coerceIn(0, labels.size - 1)]
        if (label != touchLabel) {
            touchLabel = label
            performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            shownHeaders[label]?.let { header -> scroll.scrollTo(0, topInScroll(scroll) + header.top) }
        }
        invalidate()
    }

    private fun color(attr: Int): Int = MaterialColors.getColor(this, attr)

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun sp(value: Float): Float =
        android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics)
}
