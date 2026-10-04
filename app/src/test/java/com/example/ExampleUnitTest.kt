package com.example

import com.example.data.model.HscGroup
import com.example.data.model.HscSubjectCatalog
import com.example.ui.StudyBuddyViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testHscBangladeshSubjectCatalog() {
        val compulsory = HscSubjectCatalog.compulsorySubjects
        assertEquals(5, compulsory.size)
        assertTrue(compulsory.any { it.name.contains("Bangla — 1st Paper") })
        assertTrue(compulsory.any { it.name.contains("Bangla — 2nd Paper") })
        assertTrue(compulsory.any { it.name.contains("English — 1st Paper") })
        assertTrue(compulsory.any { it.name.contains("English — 2nd Paper") })
        assertTrue(compulsory.any { it.name.contains("ICT") })

        val scienceSubjects = HscSubjectCatalog.getSubjectsForGroup(HscGroup.SCIENCE)
        assertTrue(scienceSubjects.any { it.name.contains("Physics") })
        assertTrue(scienceSubjects.any { it.name.contains("Chemistry") })
        assertTrue(scienceSubjects.any { it.name.contains("Higher Mathematics") })
        assertTrue(scienceSubjects.any { it.name.contains("Biology") })
    }

    @Test
    fun testFormatDuration() {
        assertEquals("00:00:00", StudyBuddyViewModel.formatDuration(0L))
        assertEquals("00:01:05", StudyBuddyViewModel.formatDuration(65L))
        assertEquals("01:24:36", StudyBuddyViewModel.formatDuration(5076L))
    }

    @Test
    fun testFormatHoursMinutes() {
        assertEquals("45m", StudyBuddyViewModel.formatHoursMinutes(2700L))
        assertEquals("2h 35m", StudyBuddyViewModel.formatHoursMinutes(9300L))
    }
}
