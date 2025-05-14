package com.SPAccounting.HealthX

import com.SPAccounting.HealthX.utils.RemoveCountryCode
import org.junit.Assert.*
import org.junit.Test


class RemoveCountryCodeTest {

    @Test
    fun `phone number length is less than 10` () {
        val phone = "123456789"
        val result = RemoveCountryCode.remove(phone)
        assertEquals("", result)
    }

    @Test
    fun `phone number length is more than 10` () {
        val phone = "012345678910"
        val result = RemoveCountryCode.remove(phone)
        assertEquals("2345678910", result)
    }

    @Test
    fun `phone number length is exactly 10` () {
        val phone = "0123456789"
        val result = RemoveCountryCode.remove(phone)
        assertEquals("0123456789", result)
    }

}
