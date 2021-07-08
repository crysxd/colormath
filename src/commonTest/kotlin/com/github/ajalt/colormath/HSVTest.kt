package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class HSVTest {
    @Test
    fun roundtrip() {
        HSV(0.01, 0.02, 0.03, 0.04).let { it.toHSV() shouldBeSameInstanceAs it }
        HSV(0.01, 0.02, 0.03, 0.04f).let { it.toRGB().toHSV().shouldEqualColor(it) }
    }

    @Test
    @JsName("HSV_to_RGB")
    fun `HSV to RGB`() = forAll(
        row(HSV(0, 0, 0), RGB(0, 0, 0)),
        row(HSV(180, 0, 0), RGB(0, 0, 0)),
        row(HSV(96, 50, 78), RGB(139, 199, 99)),
        row(HSV(289, 85, 87), RGB(187, 33, 222)),
        row(HSV(0, 0, 100), RGB(255, 255, 255))
    ) { hsv, rgb ->
        hsv should convertTo(rgb)
    }

    @Test
    @JsName("HSV_to_HSL")
    fun `HSV to HSL`() = forAll(
        row(HSV(0, 0, 0), HSL(0, 0, 0)),
        row(HSV(96, 50, 78), HSL(96, 47, 59)),
        row(HSV(289, 85, 87), HSL(289, 74, 50)),
        row(HSV(0, 0, 100), HSL(0, 0, 100))
    ) { hsv, hsl ->
        hsv.toHSL().shouldEqualColor(hsl, 0.005)
    }

    @Test
    @JsName("HSV_indirect_conversions")
    fun `HSV indirect conversions`() = forAll(
        row(HSV(240, 100, 100).toAnsi16(), Ansi16(94)),
        row(HSV(240, 100, 100).toAnsi256(), Ansi256(21))
    ) { actual, expected ->
        actual shouldBe expected
    }
}
