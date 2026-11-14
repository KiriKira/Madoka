package com.callagent.host.contacts

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactDirectoryTest {
    private var nextId = 1L

    private fun entry(contactId: Long, name: String, number: String, normalized: String? = null) =
        ContactEntry(id = nextId++, contactId = contactId, name = name, number = number, normalizedNumber = normalized)

    @Test
    fun chineseNamesFileByPinyinAndOthersByInitial() {
        assertEquals("Z", ContactDirectory.label("张三"))
        assertEquals("L", ContactDirectory.label("李四"))
        assertEquals("A", ContactDirectory.label("阿明"))
        assertEquals("B", ContactDirectory.label("bob"))
        assertEquals(ContactDirectory.OTHER, ContactDirectory.label("10086"))
        assertEquals(ContactDirectory.OTHER, ContactDirectory.label("  "))
    }

    @Test
    fun peopleAreOneRowPerContactSortedByLetterWithOthersLast() {
        val people = ContactDirectory.people(listOf(
            entry(1, "张三", "138 0013 8000", "+8613800138000"),
            entry(1, "张三", "+86 138-0013-8000", "+8613800138000"),
            entry(1, "张三", "010 1234 5678"),
            entry(2, "10086", "10086"),
            entry(3, "Alice", "555 0100"),
            entry(4, "李四", "139 0000 0000"),
        ))

        assertEquals(listOf("Alice", "李四", "张三", "10086"), people.map { it.name })
        assertEquals(listOf("A", "L", "Z", ContactDirectory.OTHER), people.map { it.label })
        // The same number written twice is offered once.
        assertEquals(2, people.first { it.contactId == 1L }.numbers.size)
        assertEquals(listOf("A", "L", "Z", ContactDirectory.OTHER),
            ContactDirectory.sections(people).map { it.label })
    }

    @Test
    fun numberQueryKeepsOnlyMatchingNumbersAndNameQueryKeepsAll() {
        val people = ContactDirectory.people(listOf(
            entry(1, "张三", "138 0013 8000"),
            entry(1, "张三", "010 1234 5678"),
            entry(2, "李四", "139 0000 0000"),
        ))

        val byNumber = ContactDirectory.filter(people, "1234")
        assertEquals(listOf("张三"), byNumber.map { it.name })
        assertEquals(listOf("010 1234 5678"), byNumber.single().numbers.map { it.number })

        val byName = ContactDirectory.filter(people, "张")
        assertEquals(2, byName.single().numbers.size)
        assertEquals(people, ContactDirectory.filter(people, "  "))
        assertTrue(ContactDirectory.filter(people, "王五").isEmpty())
    }

    @Test
    fun savedNumbersMatchAcrossNationalAndInternationalForms() {
        val people = ContactDirectory.people(listOf(entry(1, "张三", "138 0013 8000", "+8613800138000")))

        assertEquals("张三", ContactDirectory.nameFor(people, "+86 13800138000"))
        assertEquals("张三", ContactDirectory.nameFor(people, "13800138000"))
        assertNull(ContactDirectory.nameFor(people, "13900000000"))
        assertNull(ContactDirectory.nameFor(people, "00"))
        assertTrue(ContactDirectory.isSavedNumber(people, "138 0013 8000"))
        assertFalse(ContactDirectory.isSavedNumber(people, "张三"))
    }
}
