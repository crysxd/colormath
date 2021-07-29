package com.github.ajalt.colormath

import com.github.ajalt.colormath.RGBColorSpaces.LINEAR_SRGB
import io.kotest.assertions.withClue
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class RGBTest {
    @Test
    fun roundtrip() {
        RGB(0.01, 0.02, 0.03, 0.04).let { it.toSRGB() shouldBeSameInstanceAs it }
        RGB(0.01, 0.02, 0.03, 0.04f).let { it.toXYZ().toSRGB().shouldEqualColor(it) }
        LINEAR_SRGB(0.01, 0.02, 0.03, 0.04).let { it.toSRGB().convertTo(it.model).shouldEqualColor(it) }
    }

    @Test
    @JsName("RGB_to_HSV")
    fun `RGB to HSV`() = forAll(
        row(RGB(0, 0, 0), HSV(0, 0, 0)),
        row(RGB(140, 200, 100), HSV(96f, .5f, .784f)),
        row(RGB(96, 127, 83), HSV(102f, .346f, .498f)),
        row(RGB(255, 255, 255), HSV(0, 0, 100))
    ) { rgb, hsv ->
        rgb.toHSV() should convertTo(hsv)
    }

    @Test
    @JsName("RGB_to_HSL")
    fun `RGB to HSL`() = forAll(
        row(RGB(0, 0, 0), HSL(0, 0, 0)),
        row(RGB(140, 200, 100), HSL(96f, .476f, .588f)),
        row(RGB(96, 127, 83), HSL(102, 21, 41)),
        row(RGB(255, 255, 255), HSL(0, 0, 100))
    ) { rgb, hsl ->
        rgb.toHSL() should convertTo(hsl)
    }

    @Test
    @JsName("RGB_to_Hex")
    fun `RGB to Hex`() = forAll(
        row(RGB(0, 0, 0).toHex(false), "000000"),
        row(RGB(140, 200, 100).toHex(), "#8cc864"),
        row(RGB(140, 200, 100).toHex(true), "#8cc864"),
        row(RGB(255, 255, 255).toHex(false), "ffffff")
    ) { actual, expected ->
        actual shouldBe expected
    }

    @Test
    @JsName("Hex_to_RGB")
    fun `Hex to RGB`() = forAll(
        row("000000", RGB(0, 0, 0)),
        row("#8CC864", RGB(140, 200, 100)),
        row("ffffff", RGB(255, 255, 255)),
        row("ffffff00", RGB(255, 255, 255, 0f)),
        row("#ffffff00", RGB(255, 255, 255, 0f)),
        row("#3a30", RGB(51, 170, 51, 0f)),
        row("#3A3F", RGB(51, 170, 51, 1f)),
        row("#33aa3300", RGB(51, 170, 51, 0f)),
        row("#33AA3380", RGB(51, 170, 51, 0x80 / 0xff.toFloat())),
    ) { hex, rgb ->
        RGB(hex) shouldBe rgb
    }

    @Test
    @JsName("RGB_to_XYZ")
    fun `RGB to XYZ`() = forAll(
        row(RGB(0, 0, 0), XYZ(0.0, 0.0, 0.0)),
        row(RGB(255, 255, 255), XYZ(0.950470, 1.000000, 1.088830)),
        row(RGB(255, 0, 0), XYZ(0.412456, 0.212673, 0.019334)),
        row(RGB(0, 255, 0), XYZ(0.357576, 0.715152, 0.119192)),
        row(RGB(0, 0, 255), XYZ(0.180437, 0.072175, 0.950304)),
        row(RGB(255, 255, 0), XYZ(0.770033, 0.927825, 0.138526)),
        row(RGB(0, 255, 255), XYZ(0.538014, 0.787327, 1.069496)),
        row(RGB(255, 0, 255), XYZ(0.592894, 0.284848, 0.969638)),
        row(RGB(92, 191, 84), XYZ(0.246435, 0.401751, 0.148417)),
    ) { rgb, xyz ->
        rgb.toXYZ().shouldEqualColor(xyz, 0.000005)
    }

    @Test
    @JsName("RGB_to_LAB")
    fun `RGB to LAB`() = forAll(
        row(RGB(0, 0, 0), LAB(0.0, 0.0, 0.0)),
        row(RGB(255, 0, 0), LAB(53.2408, 80.0925, 67.2032)),
        row(RGB(255, 255, 0), LAB(97.1393, -21.5537, 94.4780)),
        row(RGB(0, 255, 0), LAB(87.7347, -86.1827, 83.1793)),
        row(RGB(0, 255, 255), LAB(91.1132, -48.0875, -14.1312)),
        row(RGB(0, 0, 255), LAB(32.2970, 79.1875, -107.8602)),
        row(RGB(255, 0, 255), LAB(60.3242, 98.2343, -60.8249)),
        row(RGB(255, 255, 255), LAB(100.000, 0.0000, 0.0000)),
        row(RGB(92, 191, 84), LAB(69.5940, -50.1108, 44.6468)),
    ) { rgb, lab ->
        rgb.toLAB().shouldEqualColor(lab)
    }

    @Test
    @JsName("RGB_to_LUV")
    fun `RGB to LUV`() = forAll(
        row(RGB(0, 0, 0), LUV(0.0, 0.0, 0.0)),
        row(RGB(255, 255, 255), LUV(100.0, 0.0, 0.0)),
        row(RGB(255, 0, 0), LUV(53.2408, 175.0151, 37.7564)),
        row(RGB(0, 255, 0), LUV(87.7347, -83.0776, 107.3985)),
        row(RGB(0, 0, 255), LUV(32.2970, -9.4054, -130.3423)),
        row(RGB(255, 255, 0), LUV(97.1393, 7.7056, 106.7866)),
        row(RGB(0, 255, 255), LUV(91.1132, -70.4773, -15.2042)),
        row(RGB(255, 0, 255), LUV(60.3242, 84.0714, -108.6834)),
        row(RGB(92, 191, 84), LUV(69.5940, -46.2383, 63.2284))
    ) { rgb, luv ->
        rgb.toLUV().shouldEqualColor(luv)
    }

    @Test
    @JsName("RGB_to_LCH")
    fun `RGB to LCH`() = forAll(
        row(RGB(0, 0, 0), HCL(0.0, 0.0, 0.0)),
        row(RGB(255, 0, 0), HCL(12.1740, 179.0414, 53.2408)),
        row(RGB(0, 255, 0), HCL(127.7236, 135.7804, 87.7347)),
        row(RGB(0, 0, 255), HCL(265.8727, 130.6812, 32.2970)),
        row(RGB(255, 255, 0), HCL(85.8727, 107.0643, 97.1393)),
        row(RGB(0, 255, 255), HCL(192.1740, 72.0987, 91.1132)),
        row(RGB(255, 0, 255), HCL(307.7236, 137.4048, 60.3242)),
        row(RGB(92, 191, 84), HCL(126.1776, 78.3314, 69.5940)),
    ) { rgb, lch ->
        rgb.toHCL().shouldEqualColor(lch)
    }

    @Test
    @JsName("RGB_white_to_LCH")
    fun `RGB white to LCH`() {
        // With white, any hue can be used, so only test L and C
        val lch = RGB(255, 255, 255).toHCL()
        lch.l shouldBe (100f plusOrMinus 0.0005f)
        lch.c shouldBe (0f plusOrMinus 0.0005f)
    }

    @Test
    @JsName("RGB_to_CMYK")
    fun `RGB to CMYK`() = forAll(
        row(RGB(0, 0, 0), CMYK(0, 0, 0, 100)),
        row(RGB(255, 255, 255), CMYK(0, 0, 0, 0)),
        row(RGB(255, 0, 0), CMYK(0, 100, 100, 0)),
        row(RGB(0, 255, 0), CMYK(100, 0, 100, 0)),
        row(RGB(0, 0, 255), CMYK(100, 100, 0, 0)),
        row(RGB(255, 255, 0), CMYK(0, 0, 100, 0)),
        row(RGB(0, 255, 255), CMYK(100, 0, 0, 0)),
        row(RGB(255, 0, 255), CMYK(0, 100, 0, 0)),
        row(RGB(140, 200, 100), CMYK(30, 0, 50, 22))
    ) { rgb, cmyk ->
        rgb.toCMYK().shouldEqualColor(cmyk, 0.005)
    }

    @Test
    @JsName("RGB_TO_HWB")
    // https://www.w3.org/TR/css-color-4/#hwb-examples
    fun `RGB to HWB`() = forAll(
        row(RGB("#996666"), HWB(0.0, .4, .4)),
        row(RGB("#998066"), HWB(30.0, .4, .4)),
        row(RGB("#999966"), HWB(60.0, .4, .4)),
        row(RGB("#809966"), HWB(90.0, .4, .4)),
        row(RGB("#669966"), HWB(120.0, .4, .4)),
        row(RGB("#66997f"), HWB(150.0, .4, .4)),
        row(RGB("#669999"), HWB(180.0, .4, .4)),
        row(RGB("#667f99"), HWB(210.0, .4, .4)),
        row(RGB("#666699"), HWB(240.0, .4, .4)),
        row(RGB("#7f6699"), HWB(270.0, .4, .4)),
        row(RGB("#996699"), HWB(300.0, .4, .4)),
        row(RGB("#996680"), HWB(330.0, .4, .4)),
        row(RGB("#80ff00"), HWB(90.0, .0, .0)),
        row(RGB("#b3cc99"), HWB(90.0, .6, .2)),
        row(RGB("#4c6633"), HWB(90.0, .2, .6)),
    ) { rgb, hwb ->
        // the tolerances here are really wide, due to the imprecision of the integer RGB.
        // The w3 spec doesn't have any rgb -> hwb test cases, so this is as precise as we can
        // get by flipping the hwb -> rgb examples.
        rgb.toHWB().shouldEqualColor(hwb, 0.6)
    }

    @Test
    @JsName("RGB_TO_HWB_gray")
    // https://www.w3.org/TR/css-color-4/#hwb-examples
    fun `RGB to HWB gray`() = forAll(
        row(RGB("#000000"), 0f, 1f),
        row(RGB("#666666"), .4f, .6f),
        row(RGB("#999999"), .6f, .4f),
        row(RGB("#ffffff"), 1f, 0f),
    ) { rgb, ew, eb ->
        // hue is arbitrary for grays
        val (_, w, b) = rgb.toHWB()
        withClue("w") { w shouldBe (ew plusOrMinus 0.0005f) }
        withClue("b") { b shouldBe (eb plusOrMinus 0.0005f) }
    }

    @Test
    @JsName("RGB_to_Linear")
    fun `sRGB to Linear`() = forAll(
        row(RGB(0, 0, 0), LINEAR_SRGB(0.0, 0.0, 0.0)),
        row(RGB(8, 8, 8), LINEAR_SRGB(0.00242, 0.00242, 0.00242)),
        row(RGB(16, 16, 16), LINEAR_SRGB(0.00518, 0.00518, 0.00518)),
        row(RGB(32, 32, 32), LINEAR_SRGB(0.01444, 0.01444, 0.01444)),
        row(RGB(64, 64, 64), LINEAR_SRGB(0.05126, 0.05126, 0.05126)),
        row(RGB(128, 128, 128), LINEAR_SRGB(0.21586, 0.21586, 0.21586)),
        row(RGB(255, 255, 255), LINEAR_SRGB(1.0, 1.0, 1.0)),
    ) { rgb, linear ->
        rgb.convertTo(LINEAR_SRGB).shouldEqualColor(linear, 0.00005)
    }

    @Test
    @JsName("Linear_to_RGB")
    fun `Linear to sRGB`() = forAll(
        row(LINEAR_SRGB(0.0, 0.0, 0.0), RGB(0, 0, 0)),
        row(LINEAR_SRGB(0.00242, 0.00242, 0.00242), RGB(8, 8, 8)),
        row(LINEAR_SRGB(0.00518, 0.00518, 0.00518), RGB(16, 16, 16)),
        row(LINEAR_SRGB(0.01444, 0.01444, 0.01444), RGB(32, 32, 32)),
        row(LINEAR_SRGB(0.05126, 0.05126, 0.05126), RGB(64, 64, 64)),
        row(LINEAR_SRGB(0.21586, 0.21586, 0.21586), RGB(128, 128, 128)),
        row(LINEAR_SRGB(1.0, 1.0, 1.0), RGB(255, 255, 255)),
    ) { linear, rgb ->
        linear should convertTo(rgb)
    }

    @Test
    @JsName("RGB_to_Oklab")
    fun `RGB to Oklab`() = forAll(
        row(RGB("#fff"), Oklab(1.0000, 0.0000, 0.0000)),
        row(RGB("#111"), Oklab(0.1776, 0.0000, 0.0000)),
        row(RGB("#000"), Oklab(0.0000, 0.0000, 0.0000)),
        row(RGB("#f00"), Oklab(0.6279, 0.2249, 0.1258)),
    ) { rgb, oklab ->
        rgb.toOklab().shouldEqualColor(oklab)
    }

    @Test
    @JsName("RGB_to_Oklch")
    fun `RGB to Oklch`() = forAll(
        row(RGB("#fff"), Oklch(1.0000, 0.0000, 00.0000)),
        row(RGB("#111"), Oklch(0.1776, 0.0000, 00.0000)),
        row(RGB("#000"), Oklch(0.0000, 0.0000, 00.0000)),
        row(RGB("#f00"), Oklch(0.6279, 0.2576, 29.2210)),
    ) { rgb, oklch ->
        rgb.toOklch().shouldEqualColor(oklch, 0.1)
    }

    @Test
    @JsName("RGB_to_Ansi16")
    fun `RGB to Ansi16`() = forAll(
        row(RGB(0, 0, 0), 30),
        row(RGB(128, 0, 0), 31),
        row(RGB(0, 128, 0), 32),
        row(RGB(128, 128, 0), 33),
        row(RGB(0, 0, 128), 34),
        row(RGB(128, 0, 128), 35),
        row(RGB(0, 128, 128), 36),
        row(RGB(170, 170, 170), 37),
        row(RGB(255, 0, 0), 91),
        row(RGB(0, 255, 0), 92),
        row(RGB(255, 255, 0), 93),
        row(RGB(0, 0, 255), 94),
        row(RGB(255, 0, 255), 95),
        row(RGB(0, 255, 255), 96),
        row(RGB(255, 255, 255), 97)
    ) { rgb, ansi ->
        rgb.toAnsi16() shouldBe Ansi16(ansi)
    }

    @Test
    @JsName("RGB_to_Ansi256")
    fun `RGB to Ansi256`() = forAll(
        row(RGB(0, 0, 0), 16),
        row(RGB(51, 102, 0), 64),
        row(RGB(92, 191, 84), 114),
        row(RGB(255, 255, 255), 231),
        row(RGB(100, 100, 100), 241),
        row(RGB(238, 238, 238), 254)
    ) { rgb, ansi ->
        rgb.toAnsi256() shouldBe Ansi256(ansi)
    }
}
