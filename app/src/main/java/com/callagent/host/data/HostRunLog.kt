package com.callagent.host.data

import android.content.Context
import android.os.Build
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A small on-device run log of what calls, push and call control did, so a
 * failure the user only sees as "it hung up" can be exported and read later.
 * Lines carry IDs and states, never phone numbers or message text. The file
 * keeps the newest lines and survives restarts.
 */
internal object HostRunLog {
    private const val FILE = "host-run-log.txt"
    private const val MAX_LINES = 3000
    private const val KEEP_LINES = 2000
    private val format = ThreadLocal.withInitial { SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US) }
    @Volatile private var appContext: Context? = null
    private var lines = -1
    private var lastStatus: String? = null

    fun install(context: Context) { appContext = context.applicationContext }

    fun add(tag: String, message: String) {
        val context = appContext ?: return
        val line = "${format.get()!!.format(Date())} $tag: ${message.replace('\n', ' ')}\n"
        runCatching { synchronized(this) { append(context, line) } }
    }

    /** A status line only when it changes: the UI sets the same text often. */
    fun status(text: String) {
        synchronized(this) { if (text == lastStatus) return; lastStatus = text }
        add("STATUS", maskNumbers(text))
    }

    private val PHONE_NUMBER = Regex("""\+?\d[\d ()-]{3,}\d""")

    /** Status texts can name the number being dialed; keep it out of the log. */
    internal fun maskNumbers(text: String): String = text.replace(PHONE_NUMBER, "[号码]")

    /** Shared as intent text, so capped well under the binder transaction limit. */
    fun export(context: Context, maxLines: Int = 1500): String {
        val body = tail(context, maxLines)
        val version = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "?"
        return "Madoka $version · Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}) · ${Build.MANUFACTURER} ${Build.MODEL}\n\n$body"
    }

    /** The newest [count] lines, newest last. */
    fun tail(context: Context, count: Int): String = synchronized(this) {
        runCatching { file(context).readLines().takeLast(count).joinToString("\n") }.getOrDefault("")
    }

    fun clear(context: Context) = synchronized(this) {
        runCatching { file(context).delete() }
        lines = 0
    }

    private fun append(context: Context, line: String) {
        val target = file(context)
        if (lines < 0) lines = runCatching { target.readLines().size }.getOrDefault(0)
        target.appendText(line)
        if (++lines > MAX_LINES) {
            val kept = target.readLines().takeLast(KEEP_LINES)
            target.writeText(kept.joinToString("\n", postfix = "\n"))
            lines = kept.size
        }
    }

    private fun file(context: Context) = File(context.filesDir, FILE)
}
