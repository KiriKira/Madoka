package com.callagent.host.contacts

import android.icu.text.AlphabeticIndex
import android.icu.text.Collator
import java.util.Locale

/** One contact with its distinct phone numbers, filed under an index letter. */
data class ContactPerson(
    val contactId: Long,
    val name: String,
    /** "A" to "Z", or "#" for names that start with a digit or symbol. */
    val label: String,
    val numbers: List<ContactEntry>
)

/** People under one index letter, in name order. */
data class ContactSection(val label: String, val people: List<ContactPerson>)

/**
 * Turns Contacts Provider rows into the phone book the dialer and the new
 * message screen show: one entry per contact, sorted by name, under A to Z
 * like a phone app. Chinese names sort and file by pinyin (张三 under Z),
 * whatever the device language, because Madoka's own text is Chinese.
 */
object ContactDirectory {
    const val OTHER = "#"

    private val locale: Locale = Locale.SIMPLIFIED_CHINESE
    private val index: AlphabeticIndex.ImmutableIndex<Any> by lazy {
        AlphabeticIndex<Any>(locale).addLabels(Locale.ENGLISH).buildImmutableIndex()
    }
    private val collator: Collator by lazy { Collator.getInstance(locale).apply { strength = Collator.SECONDARY } }

    /** The index letter for [name]: its pinyin or Latin initial, else [OTHER]. */
    fun label(name: String): String {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return OTHER
        val bucket = synchronized(index) { index.getBucket(index.getBucketIndex(trimmed)) }
        val text = bucket?.label.orEmpty()
        return if (bucket?.labelType == AlphabeticIndex.Bucket.LabelType.NORMAL &&
            text.length == 1 && text[0] in 'A'..'Z') text else OTHER
    }

    /** Groups rows by contact, drops repeated numbers and sorts A to Z, with [OTHER] last. */
    fun people(entries: List<ContactEntry>): List<ContactPerson> {
        val people = entries.groupBy { it.contactId }.values.map { rows ->
            val numbers = rows.distinctBy { digits(it.normalizedNumber ?: it.number).ifEmpty { it.number } }
            ContactPerson(rows.first().contactId, rows.first().name, label(rows.first().name), numbers)
        }
        return people.sortedWith(Comparator { a, b ->
            val byLabel = labelRank(a.label).compareTo(labelRank(b.label))
            if (byLabel != 0) return@Comparator byLabel
            val byName = synchronized(collator) { collator.compare(a.name, b.name) }
            if (byName != 0) byName else a.contactId.compareTo(b.contactId)
        })
    }

    /**
     * People matching [query], keeping only the matching numbers when the
     * query is a number. A blank query keeps everyone.
     */
    fun filter(people: List<ContactPerson>, query: String): List<ContactPerson> {
        val needle = query.trim()
        if (needle.isEmpty()) return people
        return people.mapNotNull { person ->
            if (person.name.contains(needle, ignoreCase = true)) return@mapNotNull person
            val numbers = person.numbers.filter { HostContactsRepository.matches(it, needle) }
            if (numbers.isEmpty()) null else person.copy(numbers = numbers)
        }
    }

    /** Consecutive people under the same letter; [people] is already in [people] order. */
    fun sections(people: List<ContactPerson>): List<ContactSection> {
        val sections = ArrayList<ContactSection>()
        var label: String? = null
        var members = ArrayList<ContactPerson>()
        people.forEach { person ->
            if (person.label != label) {
                label?.let { sections += ContactSection(it, members) }
                label = person.label
                members = ArrayList()
            }
            members += person
        }
        label?.let { sections += ContactSection(it, members) }
        return sections
    }

    /** The name saved for [number], comparing digits so "+86 138…" and "138…" agree. */
    fun nameFor(people: List<ContactPerson>, number: String): String? {
        val wanted = digits(number)
        if (wanted.length < 3) return null
        return people.firstOrNull { person ->
            person.numbers.any { entry -> sameNumber(wanted, digits(entry.normalizedNumber ?: entry.number)) ||
                sameNumber(wanted, digits(entry.number)) }
        }?.name
    }

    /** A typed recipient that already names one saved number exactly. */
    fun isSavedNumber(people: List<ContactPerson>, value: String): Boolean =
        value.any(Char::isDigit) && nameFor(people, value) != null

    private fun sameNumber(a: String, b: String): Boolean {
        if (a.isEmpty() || b.isEmpty()) return false
        if (a == b) return true
        // A national number matches its international form ("13800138000" and "8613800138000").
        val (short, long) = if (a.length < b.length) a to b else b to a
        return short.length >= 7 && long.endsWith(short)
    }

    private fun digits(value: String): String = value.filter(Char::isDigit)

    private fun labelRank(label: String): Int = when {
        label == OTHER -> 100
        label.length == 1 && label[0] in 'A'..'Z' -> label[0] - 'A'
        else -> 50
    }
}
