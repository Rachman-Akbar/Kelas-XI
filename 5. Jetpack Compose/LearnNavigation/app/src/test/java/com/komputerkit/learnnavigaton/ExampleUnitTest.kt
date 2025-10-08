package com.komputerkit.learnnavigaton

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests untuk aplikasi Learn Navigation
 */
class NavigationUnitTest {
    
    @Test
    fun routes_screenA_isCorrect() {
        assertEquals("screen_a", Routes.SCREEN_A)
    }
    
    @Test
    fun routes_screenB_isCorrect() {
        assertEquals("screen_b/{name}", Routes.SCREEN_B)
    }
    
    @Test
    fun createScreenBRoute_withName_returnsCorrectRoute() {
        val name = "TestUser"
        val expectedRoute = "screen_b/TestUser"
        assertEquals(expectedRoute, Routes.createScreenBRoute(name))
    }
    
    @Test
    fun createScreenBRoute_withSpecialCharacters_returnsCorrectRoute() {
        val name = "Test User!"
        val expectedRoute = "screen_b/Test User!"
        assertEquals(expectedRoute, Routes.createScreenBRoute(name))
    }
    
    @Test
    fun createScreenBRoute_withEmptyString_returnsRouteWithEmpty() {
        val name = ""
        val expectedRoute = "screen_b/"
        assertEquals(expectedRoute, Routes.createScreenBRoute(name))
    }
}