package org.djutils.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.text.NumberFormat;
import java.util.Locale;

import org.djutils.exceptions.Try;
import org.junit.jupiter.api.Test;

/**
 * NumberParserTest tests the NumberParser.
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class NumberParserTest
{
    /**
     * Test the NumberParser for NullPointer and empty string exceptions.
     */
    @Test
    public void testNPE()
    {
        Try.testFail(() -> new NumberParser(true, true, null), NullPointerException.class);
        NumberParser np = new NumberParser();
        Try.testFail(() -> np.locale(null), NullPointerException.class);

        Try.testFail(() -> np.parseDouble(null), NullPointerException.class);
        Try.testFail(() -> np.parseDouble(""), NumberFormatException.class);
        Try.testFail(() -> np.parseDouble("  "), NumberFormatException.class);

        Try.testFail(() -> np.parseFloat(null), NullPointerException.class);
        Try.testFail(() -> np.parseFloat(""), NumberFormatException.class);
        Try.testFail(() -> np.parseFloat("  "), NumberFormatException.class);

        Try.testFail(() -> np.parseInt(null), NullPointerException.class);
        Try.testFail(() -> np.parseInt(""), NumberFormatException.class);
        Try.testFail(() -> np.parseInt("  "), NumberFormatException.class);

        Try.testFail(() -> np.parseLong(null), NullPointerException.class);
        Try.testFail(() -> np.parseLong(""), NumberFormatException.class);
        Try.testFail(() -> np.parseLong("  "), NumberFormatException.class);
    }

    /**
     * Test the NumberParser constructors.
     */
    @Test
    public void testConstructors()
    {
        NumberParser np1 = new NumberParser(true);
        assertEquals(10.0, np1.parseDouble("10.0 m/s"), 1E-6);
        NumberParser np2 = new NumberParser(false);
        Try.testFail(() -> np2.parseDouble("10.0 m/s"));

        NumberParser np3 = new NumberParser(true, true);
        assertEquals(10.0, np3.parseDouble("10.0 m/s"), 1E-6);
        NumberParser np4 = new NumberParser(true, false);
        assertEquals(10.0, np4.parseDouble("10.0 m/s"), 1E-6);
        NumberParser np5 = new NumberParser(true, false);
        Try.testFail(() -> np5.parseDouble("+10.0 m/s"));

        NumberParser np6 = new NumberParser(true, true, Locale.US);
        assertEquals(10000.0, np6.parseDouble("10,000.0 m/s"), 1E-6);
        NumberParser np7 = new NumberParser(true, true, Locale.forLanguageTag("NL"));
        assertNotEquals(10000.0, np7.parseDouble("10,000.0 m/s"), 1E-6);

        NumberParser np8 = new NumberParser(false, false, Locale.US);
        assertEquals(1.0E5, np8.parseDouble("10.0E4"), 1E-6);
        Try.testFail(() -> np8.parseDouble("10.0e4"));
        Try.testFail(() -> np8.parseDouble("10..0"));
        Try.testFail(() -> np8.parseDouble("10.,0"));
        assertEquals(1000.0, np8.parseDouble("1,,0,0,0.0"), 1E-6);

        assertEquals(100, np8.parseInt("100"));
        assertEquals(-100, np8.parseInt("-100"));
        Try.testFail(() -> np8.parseInt("100."));
        Try.testFail(() -> np8.parseInt("+100"));

        assertEquals(100L, np8.parseLong("100"));
        assertEquals(-100L, np8.parseLong("-100"));
        Try.testFail(() -> np8.parseLong("100."));
        Try.testFail(() -> np8.parseLong("+100"));
    }

    /**
     * Test with a few settings for the US locale.
     */
    @Test
    public void testNumberParserFloatUS()
    {
        // @formatter:off
        TestCase[] testCasesStrict = new TestCase[]
            {
                new TestCase("12000.12", 12000.12, ""),
                new TestCase("12,000.12", 12000.12, ""),
                new TestCase(" 12,000.12 ", 12000.12, ""),
                new TestCase("12000.12E-3", 12.00012, ""),
                new TestCase("12,000.12E-3", 12.00012, ""),
                new TestCase("12000.12E2", 1200012, ""),
                new TestCase("12,000.12E2", 1200012, ""),
                new TestCase(" 12,000.12 m/s ", 12000.12, " m/s "),
                new TestCase(" 12,000.12m/s", 12000.12, "m/s"),
                new TestCase("12000.12E-3 m/s", 12.00012, " m/s"),
                new TestCase("12,000.12E-3m/s", 12.00012, "m/s"),
                new TestCase("12000.12E2 m/s", 1200012, " m/s"),
                new TestCase("12,000.12E2m/s", 1200012, "m/s"),
                new TestCase(" 12,000.12 E+", 12000.12, " E+"),
                new TestCase(" 12,000.12E+", 12000.12, "E+"),
                new TestCase("12,0", 120.0, ""),
                
                new TestCase("-12000.12", -12000.12, ""),
                new TestCase("-12,000.12", -12000.12, ""),
                new TestCase(" -12,000.12 ", -12000.12, ""),
                new TestCase("-12000.12E-3", -12.00012, ""),
                new TestCase("-12,000.12E-3", -12.00012, ""),
                new TestCase("-12000.12E2", -1200012, ""),
                new TestCase("-12,000.12E2", -1200012, ""),
                new TestCase(" -12,000.12 m/s ", -12000.12, " m/s "),
                new TestCase(" -12,000.12m/s", -12000.12, "m/s"),
                new TestCase("-12000.12E-3 m/s", -12.00012, " m/s"),
                new TestCase("-12,000.12E-3m/s", -12.00012, "m/s"),
                new TestCase("-12000.12E2 m/s", -1200012, " m/s"),
                new TestCase("-12,000.12E2m/s", -1200012, "m/s"),
                new TestCase(" -12,000.12 E+", -12000.12, " E+"),
                new TestCase(" -12,000.12E+", -12000.12, "E+"),
                new TestCase("-12,0", -120.0, ""),
            };
        TestCase[] testCasesLenient = new TestCase[]
            {
                new TestCase("+12,000.12", 12000.12, ""),
                new TestCase(" +12,000.12 ", 12000.12, ""),
                new TestCase("+12,000.12m/s", 12000.12, "m/s"),
                new TestCase(" +12,000.12 m/s", 12000.12, " m/s"),
                new TestCase(" 12,000.12E+20 m/s", 1.200012E24, " m/s"),
                new TestCase(" +12,000.12e+20 m/s", 1.200012E24, " m/s"),
                new TestCase(" +12,000.12e+20 m/s e+E+..,,++--++--", 1.200012E24, " m/s e+E+..,,++--++--"),
                new TestCase(" +12e", 12.0, "e"),
                new TestCase(" +12,", 12.0, ""),
                new TestCase(" +12.", 12.0, ""),
                new TestCase(" +12,e", 12.0, ",e"),
                new TestCase(" +12.E", 12.0, "E"),
                new TestCase(" +12.0E", 12.0, "E"),
                new TestCase(" +12.0erg", 12.0, "erg"),
                new TestCase(" +12.0E-a", 12.0, "E-a"),

                new TestCase(" -12,000.12E+20 m/s", -1.200012E24, " m/s"),
                new TestCase(" -12e", -12.0, "e"),
                new TestCase(" -12,", -12.0, ""),
                new TestCase(" -12.", -12.0, ""),
                new TestCase(" -12,e", -12.0, ",e"),
                new TestCase(" -12.E", -12.0, "E"),
                new TestCase(" -12.0E", -12.0, "E"),
                new TestCase(" -12.0erg", -12.0, "erg"),
                new TestCase(" -12.0E-a", -12.0, "E-a"),
            };
        TestCaseFail[] testCasesFail = new TestCaseFail[]
            {
                new TestCaseFail("+12,000.12", true, false),
                new TestCaseFail(" +12,000.12 ", true, false),
                new TestCaseFail(" 12,000.12E+20 m/s", false, false), // E+20 m/s is the trail
                new TestCaseFail(" +12,000.12e+20 m/s", true, false),
                new TestCaseFail("12e", true, false),
                new TestCaseFail("12,,", true, false),
                new TestCaseFail("12..", true, false),
                new TestCaseFail("12.000,10", true, false),
                new TestCaseFail("12,0E+3", true, false),
                new TestCaseFail("12.0E+,3", true, false),
                new TestCaseFail("12.0E+.3", true, false),
                new TestCaseFail("12.0E-,3", true, false),
                new TestCaseFail("12.0E-3.", true, false),
                
                new TestCaseFail("-12e", true, false),
                new TestCaseFail("-12,,", true, false),
                new TestCaseFail("-12..", true, false),
                new TestCaseFail("-12.000,10", true, false),
                new TestCaseFail("-12,0E+3", true, false),
                new TestCaseFail("-12.0E+,3", true, false),
                new TestCaseFail("-12.0E+.3", true, false),
                new TestCaseFail("-12.0E-,3", true, false),
                new TestCaseFail("-12.0E-3.", true, false),            };
        // @formatter:on

        for (TestCase test : testCasesStrict)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, true, Locale.US);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, true, Locale.US);
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, Locale.US);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, Locale.US);
        }
        for (TestCase test : testCasesLenient)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, Locale.US);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, Locale.US);
        }
        for (TestCaseFail test : testCasesFail)
        {
            testDoubleFail(test.text, test.strict, test.trailing, Locale.US);
            testFloatFail(test.text, test.strict, test.trailing, Locale.US);
        }

        // special case
        String text = " 12,000.12E+20 m/s";
        testDouble(text, 12000.12, "E+20 m/s", true, Locale.US);
        testDouble(text, 1.200012E24, " m/s", false, Locale.US);
        testFloat(text, 12000.12f, "E+20 m/s", true, Locale.US);
        testFloat(text, 1.200012E24f, " m/s", false, Locale.US);
        testDoubleFail(text, true, false, Locale.US);
        testFloatFail(text, true, false, Locale.US);
    }

    /**
     * Test with a few settings for the NL locale.
     */
    @Test
    public void testNumberParserFloatNL()
    {
        // @formatter:off
        TestCase[] testCasesStrict = new TestCase[]
            {
                new TestCase("12000,12", 12000.12, ""),
                new TestCase("12.000,12", 12000.12, ""),
                new TestCase(" 12.000,12 ", 12000.12, ""),
                new TestCase("12000,12E-3", 12.00012, ""),
                new TestCase("12.000,12E-3", 12.00012, ""),
                new TestCase("12000,12E2", 1200012, ""),
                new TestCase("12.000,12E2", 1200012, ""),
                new TestCase(" 12.000,12 m/s ", 12000.12, " m/s "),
                new TestCase(" 12.000,12m/s", 12000.12, "m/s"),
                new TestCase("12000,12E-3 m/s", 12.00012, " m/s"),
                new TestCase("12.000,12E-3m/s", 12.00012, "m/s"),
                new TestCase("12000,12E2 m/s", 1200012, " m/s"),
                new TestCase("12.000,12E2m/s", 1200012, "m/s"),
                new TestCase(" 12.000,12 E+", 12000.12, " E+"),
                new TestCase(" 12.000,12E+", 12000.12, "E+"),
                new TestCase("12.0", 120.0, ""),

                new TestCase("-12000,12", -12000.12, ""),
                new TestCase("-12.000,12", -12000.12, ""),
                new TestCase(" -12.000,12 ", -12000.12, ""),
                new TestCase("-12000,12E-3", -12.00012, ""),
                new TestCase("-12.000,12E-3", -12.00012, ""),
                new TestCase("-12000,12E2", -1200012, ""),
                new TestCase("-12.000,12E2", -1200012, ""),
                new TestCase(" -12.000,12 m/s ", -12000.12, " m/s "),
                new TestCase(" -12.000,12m/s", -12000.12, "m/s"),
                new TestCase("-12000,12E-3 m/s", -12.00012, " m/s"),
                new TestCase("-12.000,12E-3m/s", -12.00012, "m/s"),
                new TestCase("-12000,12E2 m/s", -1200012, " m/s"),
                new TestCase("-12.000,12E2m/s", -1200012, "m/s"),
                new TestCase(" -12.000,12 E+", -12000.12, " E+"),
                new TestCase(" -12.000,12E+", -12000.12, "E+"),
                new TestCase("-12.0", -120.0, ""),
            };
        TestCase[] testCasesLenient = new TestCase[]
            {
                new TestCase("+12.000,12", 12000.12, ""),
                new TestCase(" +12.000,12 ", 12000.12, ""),
                new TestCase("+12.000,12m/s", 12000.12, "m/s"),
                new TestCase(" +12.000,12 m/s", 12000.12, " m/s"),
                new TestCase(" 12.000,12E+20 m/s", 1.200012E24, " m/s"),
                new TestCase(" +12.000,12e+20 m/s", 1.200012E24, " m/s"),
                new TestCase(" +12.000,12e+20 m/s e+E+..,,++--++--", 1.200012E24, " m/s e+E+..,,++--++--"),
                new TestCase(" +12e", 12.0, "e"),
                new TestCase(" +12,", 12.0, ""),
                new TestCase(" +12.", 12.0, ""),
                new TestCase(" +12.e", 12.0, ".e"),
                new TestCase(" +12,E", 12.0, "E"),
                new TestCase(" +12,0E", 12.0, "E"),
                new TestCase(" +12,0erg", 12.0, "erg"),
                new TestCase(" +12,0E-a", 12.0, "E-a"),

                new TestCase(" -12.000,12e+20 m/s", -1.200012E24, " m/s"),
                new TestCase(" -12e", -12.0, "e"),
                new TestCase(" -12,", -12.0, ""),
                new TestCase(" -12.", -12.0, ""),
                new TestCase(" -12.e", -12.0, ".e"),
                new TestCase(" -12,E", -12.0, "E"),
                new TestCase(" -12,0E", -12.0, "E"),
                new TestCase(" -12,0erg", -12.0, "erg"),
                new TestCase(" -12,0E-a", -12.0, "E-a"),
            };
        TestCaseFail[] testCasesFail = new TestCaseFail[]
            {
                new TestCaseFail("+12.000,12", true, false),
                new TestCaseFail(" +12.000,12 ", true, false),
                new TestCaseFail(" 12.000,12E+20 m/s", false, false), // E+20 m/s is the trail
                new TestCaseFail(" +12.000,12e+20 m/s", true, false),
                new TestCaseFail("12e", true, false),
                new TestCaseFail("12,,", true, false),
                new TestCaseFail("12..", true, false),
                new TestCaseFail("12,000.10", true, false),
                new TestCaseFail("12.0E+3", true, false),
                new TestCaseFail("12,0E+,3", true, false),
                new TestCaseFail("12,0E+.3", true, false),
                new TestCaseFail("12,0E-,3", true, false),
                new TestCaseFail("12,0E-3.", true, false),
                
                new TestCaseFail("-12e", true, false),
                new TestCaseFail("-12,,", true, false),
                new TestCaseFail("-12..", true, false),
                new TestCaseFail("-12,000.10", true, false),
                new TestCaseFail("-12.0E+3", true, false),
                new TestCaseFail("-12,0E+,3", true, false),
                new TestCaseFail("-12,0E+.3", true, false),
                new TestCaseFail("-12,0E-,3", true, false),
                new TestCaseFail("-12,0E-3.", true, false),            };
        // @formatter:on

        Locale localeNL = Locale.forLanguageTag("NL");
        for (TestCase test : testCasesStrict)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, true, localeNL);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, true, localeNL);
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, localeNL);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, localeNL);
        }
        for (TestCase test : testCasesLenient)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, localeNL);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, localeNL);
        }
        for (TestCaseFail test : testCasesFail)
        {
            testDoubleFail(test.text, test.strict, test.trailing, localeNL);
            testFloatFail(test.text, test.strict, test.trailing, localeNL);
        }

        // special case
        String text = " 12.000,12E+20 m/s";
        testDouble(text, 12000.12, "E+20 m/s", true, localeNL);
        testDouble(text, 1.200012E24, " m/s", false, localeNL);
        testFloat(text, 12000.12f, "E+20 m/s", true, localeNL);
        testFloat(text, 1.200012E24f, " m/s", false, localeNL);
        testDoubleFail(text, true, false, localeNL);
        testFloatFail(text, true, false, localeNL);
    }

    /**
     * Test with a few settings for the FR locale. Note that the Locale for France in jdk17 is different from jdk11. See
     * https://stackoverflow.com/questions/70865403/why-numberformat-working-different-for-openjdk11-and-openjdk17 and
     * https://bugs.openjdk.org/browse/JDK-8225245 for more information. The jdk17 standard can be downloaded from
     * https://unicode.org/Public/cldr/35.1/. The jdk11 standard can be downloaded from https://unicode.org/Public/cldr/33/.
     */
    public void testNumberParserFloatFRjdk17()
    {
        // @formatter:off
        TestCase[] testCasesStrict = new TestCase[]
            {
                new TestCase("12000,12", 12000.12, ""),
                new TestCase("12\u202F000,12", 12000.12, ""),
                new TestCase(" 12\u202F000,12 ", 12000.12, ""),
                new TestCase("12000,12E-3", 12.00012, ""),
                new TestCase("12\u202F000,12E-3", 12.00012, ""),
                new TestCase("12000,12E2", 1200012, ""),
                new TestCase("12\u202F000,12E2", 1200012, ""),
                new TestCase(" 12\u202F000,12 m/s ", 12000.12, " m/s "),
                new TestCase(" 12\u202F000,12m/s", 12000.12, "m/s"),
                new TestCase("12000,12E-3 m/s", 12.00012, " m/s"),
                new TestCase("12\u202F000,12E-3m/s", 12.00012, "m/s"),
                new TestCase("12000,12E2 m/s", 1200012, " m/s"),
                new TestCase("12\u202F000,12E2m/s", 1200012, "m/s"),
                new TestCase(" 12\u202F000,12 E+", 12000.12, " E+"),
                new TestCase(" 12\u202F000,12E+", 12000.12, "E+"),
                new TestCase("12\u202F0", 120.0, ""),
                
                new TestCase("-12000,12", -12000.12, ""),
                new TestCase("-12\u202F000,12", -12000.12, ""),
                new TestCase(" -12\u202F000,12 ", -12000.12, ""),
                new TestCase("-12000,12E-3", -12.00012, ""),
                new TestCase("-12\u202F000,12E-3", -12.00012, ""),
                new TestCase("-12000,12E2", -1200012, ""),
                new TestCase("-12\u202F000,12E2", -1200012, ""),
                new TestCase(" -12\u202F000,12 m/s ", -12000.12, " m/s "),
                new TestCase(" -12\u202F000,12m/s", -12000.12, "m/s"),
                new TestCase("-12000,12E-3 m/s", -12.00012, " m/s"),
                new TestCase("-12\u202F000,12E-3m/s", -12.00012, "m/s"),
                new TestCase("-12000,12E2 m/s", -1200012, " m/s"),
                new TestCase("-12\u202F000,12E2m/s", -1200012, "m/s"),
                new TestCase(" -12\u202F000,12 E+", -12000.12, " E+"),
                new TestCase(" -12\u202F000,12E+", -12000.12, "E+"),
                new TestCase("-12\u202F0", -120.0, ""),
            };
        TestCase[] testCasesLenient = new TestCase[]
            {
                new TestCase("+12\u202F000,12", 12000.12, ""),
                new TestCase(" +12\u202F000,12 ", 12000.12, ""),
                new TestCase("+12\u202F000,12m/s", 12000.12, "m/s"),
                new TestCase(" +12\u202F000,12 m/s", 12000.12, " m/s"),
                new TestCase(" 12\u202F000,12E+20 m/s", 1.200012E24, " m/s"),
                new TestCase(" +12\u202F000,12e+20 m/s", 1.200012E24, " m/s"),
                new TestCase(" +12\u202F000,12e+20 m/s e+E+..,,++--++--", 1.200012E24, " m/s e+E+..,,++--++--"),
                new TestCase(" +12e", 12.0, "e"),
                new TestCase(" +12,", 12.0, ""),
                new TestCase(" +12\u202F", 12.0, ""),
                new TestCase(" +12\u202Fe", 12.0, "\u202Fe"),
                new TestCase(" +12,E", 12.0, "E"),
                new TestCase(" +12,0E", 12.0, "E"),
                new TestCase(" +12,0erg", 12.0, "erg"),
                new TestCase(" +12,0E-a", 12.0, "E-a"),
                
                new TestCase(" -12\u202F000,12e+20 m/s", -1.200012E24, " m/s"),
                new TestCase(" -12e", -12.0, "e"),
                new TestCase(" -12,", -12.0, ""),
                new TestCase(" -12\u202F", -12.0, ""),
                new TestCase(" -12\u202Fe", -12.0, "\u202Fe"),
                new TestCase(" -12,E", -12.0, "E"),
                new TestCase(" -12,0E", -12.0, "E"),
                new TestCase(" -12,0erg", -12.0, "erg"),
                new TestCase(" -12,0E-a", -12.0, "E-a"),
            };
        TestCaseFail[] testCasesFail = new TestCaseFail[]
            {
                new TestCaseFail("+12\u202F000,12", true, false),
                new TestCaseFail(" +12\u202F000,12 ", true, false),
                new TestCaseFail(" 12\u202F000,12E+20 m/s", false, false), // E+20 m/s is the trail
                new TestCaseFail(" +12\u202F000,12e+20 m/s", true, false),
                new TestCaseFail("12e", true, false),
                new TestCaseFail("12,,", true, false),
                new TestCaseFail("12\u202F\u202F", true, false),
                new TestCaseFail("12,000.10", true, false),
                new TestCaseFail("12\u202F0E+3", true, false),
                new TestCaseFail("12,0E+\u202F3", true, false),
                new TestCaseFail("12,0E+.3", true, false),
                new TestCaseFail("12,0E-,3", true, false),
                new TestCaseFail("12,0E-3.", true, false),

                new TestCaseFail("-12e", true, false),
                new TestCaseFail("-12,,", true, false),
                new TestCaseFail("-12\u202F\u202F", true, false),
                new TestCaseFail("-12,000.10", true, false),
                new TestCaseFail("-12\u202F0E+3", true, false),
                new TestCaseFail("-12,0E+\u202F3", true, false),
                new TestCaseFail("-12,0E+.3", true, false),
                new TestCaseFail("-12,0E-,3", true, false),
                new TestCaseFail("-12,0E-3.", true, false),
            };
        // @formatter:on

        Locale localeFR = Locale.FRANCE;
        for (TestCase test : testCasesStrict)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, true, localeFR);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, true, localeFR);
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, localeFR);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, localeFR);
        }
        for (TestCase test : testCasesLenient)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, localeFR);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, localeFR);
        }
        for (TestCaseFail test : testCasesFail)
        {
            testDoubleFail(test.text, test.strict, test.trailing, localeFR);
            testFloatFail(test.text, test.strict, test.trailing, localeFR);
        }

        // special case
        String text = " 12\u202F000,12E+20 m/s";
        testDouble(text, 12000.12, "E+20 m/s", true, localeFR);
        testDouble(text, 1.200012E24, " m/s", false, localeFR);
        testFloat(text, 12000.12f, "E+20 m/s", true, localeFR);
        testFloat(text, 1.200012E24f, " m/s", false, localeFR);
        testDoubleFail(text, true, false, localeFR);
        testFloatFail(text, true, false, localeFR);
    }

    /**
     * Test with a few settings for the FR locale. Note that the Locale for France in jdk17 is different from jdk11. See
     * https://stackoverflow.com/questions/70865403/why-numberformat-working-different-for-openjdk11-and-openjdk17 and
     * https://bugs.openjdk.org/browse/JDK-8225245 for more information. The jdk17 standard can be downloaded from
     * https://unicode.org/Public/cldr/35.1/. The jdk11 standard can be downloaded from https://unicode.org/Public/cldr/33/.
     */
    public void testNumberParserFloatFRjdk11()
    {
        // @formatter:off
        TestCase[] testCasesStrict = new TestCase[]
            {
                new TestCase("12000,12", 12000.12, ""),
                new TestCase("12\u00A0000,12", 12000.12, ""),
                new TestCase(" 12\u00A0000,12 ", 12000.12, ""),
                new TestCase("12000,12E-3", 12.00012, ""),
                new TestCase("12\u00A0000,12E-3", 12.00012, ""),
                new TestCase("12000,12E2", 1200012, ""),
                new TestCase("12\u00A0000,12E2", 1200012, ""),
                new TestCase(" 12\u00A0000,12 m/s ", 12000.12, " m/s "),
                new TestCase(" 12\u00A0000,12m/s", 12000.12, "m/s"),
                new TestCase("12000,12E-3 m/s", 12.00012, " m/s"),
                new TestCase("12\u00A0000,12E-3m/s", 12.00012, "m/s"),
                new TestCase("12000,12E2 m/s", 1200012, " m/s"),
                new TestCase("12\u00A0000,12E2m/s", 1200012, "m/s"),
                new TestCase(" 12\u00A0000,12 E+", 12000.12, " E+"),
                new TestCase(" 12\u00A0000,12E+", 12000.12, "E+"),
                new TestCase("12\u00A00", 120.0, ""),
                
                new TestCase("-12000,12", -12000.12, ""),
                new TestCase("-12\u00A0000,12", -12000.12, ""),
                new TestCase(" -12\u00A0000,12 ", -12000.12, ""),
                new TestCase("-12000,12E-3", -12.00012, ""),
                new TestCase("-12\u00A0000,12E-3", -12.00012, ""),
                new TestCase("-12000,12E2", -1200012, ""),
                new TestCase("-12\u00A0000,12E2", -1200012, ""),
                new TestCase(" -12\u00A0000,12 m/s ", -12000.12, " m/s "),
                new TestCase(" -12\u00A0000,12m/s", -12000.12, "m/s"),
                new TestCase("-12000,12E-3 m/s", -12.00012, " m/s"),
                new TestCase("-12\u00A0000,12E-3m/s", -12.00012, "m/s"),
                new TestCase("-12000,12E2 m/s", -1200012, " m/s"),
                new TestCase("-12\u00A0000,12E2m/s", -1200012, "m/s"),
                new TestCase(" -12\u00A0000,12 E+", -12000.12, " E+"),
                new TestCase(" -12\u00A0000,12E+", -12000.12, "E+"),
                new TestCase("-12\u00A00", -120.0, ""),
            };
        TestCase[] testCasesLenient = new TestCase[]
            {
                new TestCase("+12\u00A0000,12", 12000.12, ""),
                new TestCase(" +12\u00A0000,12 ", 12000.12, ""),
                new TestCase("+12\u00A0000,12m/s", 12000.12, "m/s"),
                new TestCase(" +12\u00A0000,12 m/s", 12000.12, " m/s"),
                new TestCase(" 12\u00A0000,12E+20 m/s", 1.200012E24, " m/s"),
                new TestCase(" +12\u00A0000,12e+20 m/s", 1.200012E24, " m/s"),
                new TestCase(" +12\u00A0000,12e+20 m/s e+E+..,,++--++--", 1.200012E24, " m/s e+E+..,,++--++--"),
                new TestCase(" +12e", 12.0, "e"),
                new TestCase(" +12,", 12.0, ""),
                new TestCase(" +12\u00A0", 12.0, ""),
                new TestCase(" +12\u00A0e", 12.0, "\u00A0e"),
                new TestCase(" +12,E", 12.0, "E"),
                new TestCase(" +12,0E", 12.0, "E"),
                new TestCase(" +12,0erg", 12.0, "erg"),
                new TestCase(" +12,0E-a", 12.0, "E-a"),
                
                new TestCase(" -12\u00A0000,12e+20 m/s", -1.200012E24, " m/s"),
                new TestCase(" -12e", -12.0, "e"),
                new TestCase(" -12,", -12.0, ""),
                new TestCase(" -12\u00A0", -12.0, ""),
                new TestCase(" -12\u00A0e", -12.0, "\u00A0e"),
                new TestCase(" -12,E", -12.0, "E"),
                new TestCase(" -12,0E", -12.0, "E"),
                new TestCase(" -12,0erg", -12.0, "erg"),
                new TestCase(" -12,0E-a", -12.0, "E-a"),
            };
        TestCaseFail[] testCasesFail = new TestCaseFail[]
            {
                new TestCaseFail("+12\u00A0000,12", true, false),
                new TestCaseFail(" +12\u00A0000,12 ", true, false),
                new TestCaseFail(" 12\u00A0000,12E+20 m/s", false, false), // E+20 m/s is the trail
                new TestCaseFail(" +12\u00A0000,12e+20 m/s", true, false),
                new TestCaseFail("12e", true, false),
                new TestCaseFail("12,,", true, false),
                new TestCaseFail("12\u00A0\u00A0", true, false),
                new TestCaseFail("12,000.10", true, false),
                new TestCaseFail("12\u00A00E+3", true, false),
                new TestCaseFail("12,0E+\u00A03", true, false),
                new TestCaseFail("12,0E+.3", true, false),
                new TestCaseFail("12,0E-,3", true, false),
                new TestCaseFail("12,0E-3.", true, false),

                new TestCaseFail("-12e", true, false),
                new TestCaseFail("-12,,", true, false),
                new TestCaseFail("-12\u00A0\u00A0", true, false),
                new TestCaseFail("-12,000.10", true, false),
                new TestCaseFail("-12\u00A00E+3", true, false),
                new TestCaseFail("-12,0E+\u00A03", true, false),
                new TestCaseFail("-12,0E+.3", true, false),
                new TestCaseFail("-12,0E-,3", true, false),
                new TestCaseFail("-12,0E-3.", true, false),
            };
        // @formatter:on

        Locale localeFR = Locale.FRANCE;
        for (TestCase test : testCasesStrict)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, true, localeFR);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, true, localeFR);
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, localeFR);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, localeFR);
        }
        for (TestCase test : testCasesLenient)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, localeFR);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, localeFR);
        }
        for (TestCaseFail test : testCasesFail)
        {
            testDoubleFail(test.text, test.strict, test.trailing, localeFR);
            testFloatFail(test.text, test.strict, test.trailing, localeFR);
        }

        // special case
        String text = " 12\u00A0000,12E+20 m/s";
        testDouble(text, 12000.12, "E+20 m/s", true, localeFR);
        testDouble(text, 1.200012E24, " m/s", false, localeFR);
        testFloat(text, 12000.12f, "E+20 m/s", true, localeFR);
        testFloat(text, 1.200012E24f, " m/s", false, localeFR);
        testDoubleFail(text, true, false, localeFR);
        testFloatFail(text, true, false, localeFR);
    }

    /**
     * Test with a few settings for the FR locale. Note that the Locale for France in jdk17 is different from jdk11. See
     * https://stackoverflow.com/questions/70865403/why-numberformat-working-different-for-openjdk11-and-openjdk17 for more
     * information. The jdk17 standard can be downloaded from https://unicode.org/Public/cldr/35.1/. The jdk11 standard can be
     * downloaded from https://unicode.org/Public/cldr/33/.
     */
    @Test
    public void testNumberParserFloatFR()
    {
        if (Runtime.version().feature() < 13)
        {
            testNumberParserFloatFRjdk11();
        }
        else
        {
            testNumberParserFloatFRjdk17();
        }
    }

    /**
     * Test with a few settings for the Arabic locale. Note that Arabic is written right-to-left, but the numbers in a Java
     * String are still formatted left-to-right. Note that the Locale for Arabic in jdk17 is different from jdk11. See
     * https://stackoverflow.com/questions/72738425/numberformating-issue-with-negative-numbers-in-rtl-locale-like-arabic for
     * more information. The jdk17 standard can be downloaded from https://unicode.org/Public/cldr/35.1/. The jdk11 standard can
     * be downloaded from https://unicode.org/Public/cldr/33/.
     */
    public void testNumberParserFloatArabicJdk17()
    {
        // Numbers 0 to 9 are: ٠١٢٣٤٥٦٧٨٩

        // @formatter:off
        TestCase[] testCasesStrict = new TestCase[]
                {
                    new TestCase("١٢٠٠٠٫١٢", 12000.12, ""),
                    new TestCase("١٢٬٠٠٠٫١٢", 12000.12, ""),
                    new TestCase(" ١٢٬٠٠٠٫١٢ ", 12000.12, ""),
                    new TestCase(" ١٢٬٠٠٠٫١٢ m/s ", 12000.12, " m/s "),
                    new TestCase(" ١٢٬٠٠٠٫١٢m/s", 12000.12, "m/s"),
                    new TestCase(" ١٢٬٠٠٠٫١٢ E+", 12000.12, " E+"),
                    new TestCase(" ١٢٬٠٠٠٫١٢E+", 12000.12, "E+"),
                    new TestCase("١٢٬٠", 120.0, ""),
                    
                    new TestCase("\u061c\u002d١٢٠٠٠٫١٢", -12000.12, ""),
                    new TestCase("\u061c\u002d١٢٬٠٠٠٫١٢", -12000.12, ""),
                    new TestCase(" \u061c\u002d١٢٬٠٠٠٫١٢ ", -12000.12, ""),
                    new TestCase(" \u061c\u002d١٢٬٠٠٠٫١٢ m/s ", -12000.12, " m/s "),
                    new TestCase(" \u061c\u002d١٢٬٠٠٠٫١٢m/s", -12000.12, "m/s"),
                    new TestCase(" \u061c\u002d١٢٬٠٠٠٫١٢ E+", -12000.12, " E+"),
                    new TestCase(" \u061c\u002d١٢٬٠٠٠٫١٢E+", -12000.12, "E+"),
                    new TestCase("\u061c\u002d١٢٬٠", -120.0, ""),
                };
            TestCase[] testCasesLenient = new TestCase[]
                {
                    new TestCase("+١٢٬٠٠٠٫١٢", 12000.12, ""),
                    new TestCase(" +١٢٬٠٠٠٫١٢ ", 12000.12, ""),
                    new TestCase("+١٢٬٠٠٠٫١٢m/s", 12000.12, "m/s"),
                    new TestCase(" +١٢٬٠٠٠٫١٢ m/s", 12000.12, " m/s"),
                    new TestCase(" +١٢e", 12.0, "e"),
                    new TestCase(" +١٢٬", 12.0, ""),
                    new TestCase(" +١٢٫", 12.0, ""),
                    new TestCase(" +١٢٬e", 12.0, "٬e"),
                    new TestCase(" +١٢٫E", 12.0, "E"),
                    new TestCase(" +١٢٫٠E", 12.0, "E"),
                    new TestCase(" +١٢٫٠erg", 12.0, "erg"),
                    new TestCase(" +١٢٫٠E-a", 12.0, "E-a"),

                    new TestCase("\u061c\u002d١٢٬٠٠٠٫١٢", -12000.12, ""),
                    new TestCase(" \u061c\u002d١٢٬٠٠٠٫١٢ ", -12000.12, ""),
                    new TestCase("\u061c\u002d١٢٬٠٠٠٫١٢m/s", -12000.12, "m/s"),
                    new TestCase(" \u061c\u002d١٢٬٠٠٠٫١٢ m/s", -12000.12, " m/s"),
                    new TestCase(" \u061c\u002d١٢e", -12.0, "e"),
                    new TestCase(" \u061c\u002d١٢٬", -12.0, ""),
                    new TestCase(" \u061c\u002d١٢٫", -12.0, ""),
                    new TestCase(" \u061c\u002d١٢٬e", -12.0, "٬e"),
                    new TestCase(" \u061c\u002d١٢٫E", -12.0, "E"),
                    new TestCase(" \u061c\u002d١٢٫٠E", -12.0, "E"),
                    new TestCase(" \u061c\u002d١٢٫٠erg", -12.0, "erg"),
                    new TestCase(" \u061c\u002d١٢٫٠E-a", -12.0, "E-a"),
                };
            TestCaseFail[] testCasesFail = new TestCaseFail[]
                {
                    new TestCaseFail("+١٢٬٠٠٠٫١٢", true, false),
                    new TestCaseFail(" +١٢٬٠٠٠٫١٢ ", true, false),
                    new TestCaseFail(" ١٢٬٠٠٠٫١٢E+٢٠ m/s", false, false), // E+20 m/s is the trail
                    new TestCaseFail(" +١٢٬٠٠٠٫١٢e+٢٠ m/s", true, false),
                    new TestCaseFail("١٢e", true, false),
                    new TestCaseFail("١٢٬٬", true, false),
                    new TestCaseFail("١٢٫٫", true, false),
                    new TestCaseFail("١٢٫٠٠٠٬1٠", true, false),
                    new TestCaseFail("١٢,٠E+٣", true, false),
                    new TestCaseFail("١٢٫٠E+٬٣", true, false),
                    new TestCaseFail("١٢٫٠E+٫٣", true, false),
                    new TestCaseFail("١٢٫٠E-٬٣", true, false),
                    new TestCaseFail("١٢٫٠E-٣٫", true, false),

                    new TestCaseFail("\u061c\u002d١٢e", true, false),
                    new TestCaseFail("\u061c\u002d١٢٬٬", true, false),
                    new TestCaseFail("\u061c\u002d١٢٫٫", true, false),
                    new TestCaseFail("\u061c\u002d١٢٫٠٠٠٬1٠", true, false),
                    new TestCaseFail("\u061c\u002d١٢,٠E+٣", true, false),
                    new TestCaseFail("\u061c\u002d١٢٫٠E+٬٣", true, false),
                    new TestCaseFail("\u061c\u002d١٢٫٠E+٫٣", true, false),
                    new TestCaseFail("\u061c\u002d١٢٫٠E-٬٣", true, false),
                    new TestCaseFail("\u061c\u002d١٢٫٠E-٣٫", true, false),
                };
        // @formatter:on

        Locale localeArabic = Locale.forLanguageTag("ar-DZ-u-nu-arab");
        for (TestCase test : testCasesStrict)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, true, localeArabic);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, true, localeArabic);
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, localeArabic);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, localeArabic);
        }
        for (TestCase test : testCasesLenient)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, localeArabic);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, localeArabic);
        }
        for (TestCaseFail test : testCasesFail)
        {
            testDoubleFail(test.text, test.strict, test.trailing, localeArabic);
            testFloatFail(test.text, test.strict, test.trailing, localeArabic);
        }

        // int and long
        assertEquals(12000, new NumberParser().strict().noTrailing().locale(localeArabic).parseInt("١٢٠٠٠"));
        assertEquals(12000L, new NumberParser().strict().noTrailing().locale(localeArabic).parseLong("١٢٠٠٠"));
        // the String -12000 is:
        // \u061c\u002d\u0661\u0662\u066c\u0660\u0660\u0660
        // ALM - 1 2 ATHS 0 0 0
        // for ALM see https://unicode-explorer.com/c/061C
        // for ATHS (Arabic Thousands Separator) see https://unicode-explorer.com/c/066C
        // Here we use it without the thousands separator
        String ar = "\u061c\u002d\u0661\u0662\u0660\u0660\u0660";
        assertEquals(-12000, new NumberParser().strict().noTrailing().locale(localeArabic).parseInt(ar));
        assertEquals(-12000L, new NumberParser().strict().noTrailing().locale(localeArabic).parseLong(ar));
    }

    /**
     * Test with a few settings for the Arabic locale. Note that Arabic is written right-to-left, but the numbers in a Java
     * String are still formatted left-to-right. Note that the Locale for Arabic in jdk17 is different from jdk11. See
     * https://stackoverflow.com/questions/72738425/numberformating-issue-with-negative-numbers-in-rtl-locale-like-arabic for
     * more information. The jdk17 standard can be downloaded from https://unicode.org/Public/cldr/35.1/. The jdk11 standard can
     * be downloaded from https://unicode.org/Public/cldr/33/.
     */
    public void testNumberParserFloatArabicJdk11()
    {
        // Numbers 0 to 9 are: ٠١٢٣٤٥٦٧٨٩

        // @formatter:off
        TestCase[] testCasesStrict = new TestCase[]
                {
                    new TestCase("١٢٠٠٠٫١٢", 12000.12, ""),
                    new TestCase("١٢٬٠٠٠٫١٢", 12000.12, ""),
                    new TestCase(" ١٢٬٠٠٠٫١٢ ", 12000.12, ""),
                    new TestCase(" ١٢٬٠٠٠٫١٢ m/s ", 12000.12, " m/s "),
                    new TestCase(" ١٢٬٠٠٠٫١٢m/s", 12000.12, "m/s"),
                    new TestCase(" ١٢٬٠٠٠٫١٢ E+", 12000.12, " E+"),
                    new TestCase(" ١٢٬٠٠٠٫١٢E+", 12000.12, "E+"),
                    new TestCase("١٢٬٠", 120.0, ""),
                    
                    new TestCase("\u061c١٢٠٠٠٫١٢", -12000.12, ""),
                    new TestCase("\u061c١٢٬٠٠٠٫١٢", -12000.12, ""),
                    new TestCase(" \u061c١٢٬٠٠٠٫١٢ ", -12000.12, ""),
                    new TestCase(" \u061c١٢٬٠٠٠٫١٢ m/s ", -12000.12, " m/s "),
                    new TestCase(" \u061c١٢٬٠٠٠٫١٢m/s", -12000.12, "m/s"),
                    new TestCase(" \u061c١٢٬٠٠٠٫١٢ E+", -12000.12, " E+"),
                    new TestCase(" \u061c١٢٬٠٠٠٫١٢E+", -12000.12, "E+"),
                    new TestCase("\u061c١٢٬٠", -120.0, ""),
                };
            TestCase[] testCasesLenient = new TestCase[]
                {
                    new TestCase("+١٢٬٠٠٠٫١٢", 12000.12, ""),
                    new TestCase(" +١٢٬٠٠٠٫١٢ ", 12000.12, ""),
                    new TestCase("+١٢٬٠٠٠٫١٢m/s", 12000.12, "m/s"),
                    new TestCase(" +١٢٬٠٠٠٫١٢ m/s", 12000.12, " m/s"),
                    new TestCase(" +١٢e", 12.0, "e"),
                    new TestCase(" +١٢٬", 12.0, ""),
                    new TestCase(" +١٢٫", 12.0, ""),
                    new TestCase(" +١٢٬e", 12.0, "٬e"),
                    new TestCase(" +١٢٫E", 12.0, "E"),
                    new TestCase(" +١٢٫٠E", 12.0, "E"),
                    new TestCase(" +١٢٫٠erg", 12.0, "erg"),
                    new TestCase(" +١٢٫٠E-a", 12.0, "E-a"),

                    new TestCase("\u061c١٢٬٠٠٠٫١٢", -12000.12, ""),
                    new TestCase(" \u061c١٢٬٠٠٠٫١٢ ", -12000.12, ""),
                    new TestCase("\u061c١٢٬٠٠٠٫١٢m/s", -12000.12, "m/s"),
                    new TestCase(" \u061c١٢٬٠٠٠٫١٢ m/s", -12000.12, " m/s"),
                    new TestCase(" \u061c١٢e", -12.0, "e"),
                    new TestCase(" \u061c١٢٬", -12.0, ""),
                    new TestCase(" \u061c١٢٫", -12.0, ""),
                    new TestCase(" \u061c١٢٬e", -12.0, "٬e"),
                    new TestCase(" \u061c١٢٫E", -12.0, "E"),
                    new TestCase(" \u061c١٢٫٠E", -12.0, "E"),
                    new TestCase(" \u061c١٢٫٠erg", -12.0, "erg"),
                    new TestCase(" \u061c١٢٫٠E-a", -12.0, "E-a"),
                };
            TestCaseFail[] testCasesFail = new TestCaseFail[]
                {
                    new TestCaseFail("+١٢٬٠٠٠٫١٢", true, false),
                    new TestCaseFail(" +١٢٬٠٠٠٫١٢ ", true, false),
                    new TestCaseFail(" ١٢٬٠٠٠٫١٢E+٢٠ m/s", false, false), // E+20 m/s is the trail
                    new TestCaseFail(" +١٢٬٠٠٠٫١٢e+٢٠ m/s", true, false),
                    new TestCaseFail("١٢e", true, false),
                    new TestCaseFail("١٢٬٬", true, false),
                    new TestCaseFail("١٢٫٫", true, false),
                    new TestCaseFail("١٢٫٠٠٠٬1٠", true, false),
                    new TestCaseFail("١٢,٠E+٣", true, false),
                    new TestCaseFail("١٢٫٠E+٬٣", true, false),
                    new TestCaseFail("١٢٫٠E+٫٣", true, false),
                    new TestCaseFail("١٢٫٠E-٬٣", true, false),
                    new TestCaseFail("١٢٫٠E-٣٫", true, false),

                    new TestCaseFail("\u061c١٢e", true, false),
                    new TestCaseFail("\u061c١٢٬٬", true, false),
                    new TestCaseFail("\u061c١٢٫٫", true, false),
                    new TestCaseFail("\u061c١٢٫٠٠٠٬1٠", true, false),
                    new TestCaseFail("\u061c١٢,٠E+٣", true, false),
                    new TestCaseFail("\u061c١٢٫٠E+٬٣", true, false),
                    new TestCaseFail("\u061c١٢٫٠E+٫٣", true, false),
                    new TestCaseFail("\u061c١٢٫٠E-٬٣", true, false),
                    new TestCaseFail("\u061c١٢٫٠E-٣٫", true, false),
                };
        // @formatter:on

        Locale localeArabic = Locale.forLanguageTag("ar-DZ-u-nu-arab");
        for (TestCase test : testCasesStrict)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, true, localeArabic);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, true, localeArabic);
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, localeArabic);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, localeArabic);
        }
        for (TestCase test : testCasesLenient)
        {
            testDouble(test.text, test.expectedValue, test.expectedTrail, false, localeArabic);
            testFloat(test.text, (float) test.expectedValue, test.expectedTrail, false, localeArabic);
        }
        for (TestCaseFail test : testCasesFail)
        {
            testDoubleFail(test.text, test.strict, test.trailing, localeArabic);
            testFloatFail(test.text, test.strict, test.trailing, localeArabic);
        }

        // int and long
        assertEquals(12000, new NumberParser().strict().noTrailing().locale(localeArabic).parseInt("١٢٠٠٠"));
        assertEquals(12000L, new NumberParser().strict().noTrailing().locale(localeArabic).parseLong("١٢٠٠٠"));
        // the String -12000 is:
        // \u061c\u0661\u0662\u066c\u0660\u0660\u0660
        // ALM 1 2 ATHS 0 0 0
        // for ALM see https://unicode-explorer.com/c/061C
        // for ATHS (Arabic Thousands Separator) see https://unicode-explorer.com/c/066C
        // Here we use it without the thousands separator
        String ar = "\u061c\u0661\u0662\u0660\u0660\u0660";
        assertEquals(-12000, new NumberParser().strict().noTrailing().locale(localeArabic).parseInt(ar));
        assertEquals(-12000L, new NumberParser().strict().noTrailing().locale(localeArabic).parseLong(ar));
    }

    /**
     * Test with a few settings for the Arabic locale. Note that Arabic is written right-to-left, but the numbers in a Java
     * String are still formatted left-to-right. Note that the Locale for Arabic in jdk17 is different from jdk11. See
     * https://stackoverflow.com/questions/72738425/numberformating-issue-with-negative-numbers-in-rtl-locale-like-arabic for
     * more information. The jdk17 standard can be downloaded from https://unicode.org/Public/cldr/35.1/. The jdk11 standard can
     * be downloaded from https://unicode.org/Public/cldr/33/.
     */
    @Test
    public void testNumberParserFloatArabic()
    {
        if (Runtime.version().feature() < 13)
        {
            testNumberParserFloatArabicJdk11();
        }
        else
        {
            testNumberParserFloatArabicJdk17();
        }
    }

    /**
     * @param cp char at codepoint expressed as int
     * @return unicode
     */
    private static String getUnicodeCharacterOfChar(final int cp)
    {
        return String.format("\\u%04x", cp);
    }

    /**
     * Helper method to print the internal formatting of an Arab number in unicode.
     */
    public void arabicStringToHex()
    {
        String ar = NumberFormat.getInstance(Locale.forLanguageTag("ar-DZ-u-nu-arab")).format(-12000);
        String s = "";
        for (int i = 0; i < ar.length(); i++)
        {
            s += getUnicodeCharacterOfChar(ar.codePointAt(i));
        }
        System.out.println(s);
    }

    /**
     * Test a parsing testcase for Double values.
     * @param text String; the text to parse
     * @param expectedAnswer double; the expected numerical answer
     * @param expectedTrailing String the expected trailing info
     * @param strict boolean; whether the text String is strict or not
     * @param locale Locale; the locale to test for
     */
    public void testDouble(final String text, final double expectedAnswer, final String expectedTrailing, final boolean strict,
            final Locale locale)
    {
        boolean lenient = !strict;
        boolean[] testTrailing = expectedTrailing.length() == 0 ? new boolean[] {false, true} : new boolean[] {true};
        for (boolean trailing : testTrailing)
        {
            String error = String.format("parsing [%s], lenient=%b, trailing=%b, locale=%s", text, lenient, trailing, locale);
            try
            {
                // test without restrictions
                NumberParser np = new NumberParser(trailing, lenient, locale);
                double d = np.parseDouble(text);
                String trailingText = text.substring(np.getTrailingPosition());
                assertEquals(expectedAnswer, d, Math.abs(expectedAnswer / 1E6), error);
                assertEquals(expectedTrailing, trailingText, error);

                // test by setting a different locale
                Locale.setDefault(Locale.TRADITIONAL_CHINESE);
                np = new NumberParser(trailing, lenient, locale);
                d = np.parseDouble(text);
                trailingText = text.substring(np.getTrailingPosition());
                assertEquals(expectedAnswer, d, Math.abs(expectedAnswer / 1E6), error);
                assertEquals(expectedTrailing, trailingText, error);
                assertEquals(Locale.TRADITIONAL_CHINESE, Locale.getDefault(), error);

                // test by using a default locale
                Locale.setDefault(locale);
                np = new NumberParser(trailing, lenient);
                d = np.parseDouble(text);
                trailingText = text.substring(np.getTrailingPosition());
                assertEquals(expectedAnswer, d, Math.abs(expectedAnswer / 1E6), error);
                assertEquals(expectedTrailing, trailingText, error);

                // test chaining
                Locale.setDefault(Locale.TRADITIONAL_CHINESE);
                np = makeNumberParserChaining(lenient, trailing, locale);
                d = np.parseDouble(text);
                trailingText = text.substring(np.getTrailingPosition());
                assertEquals(expectedAnswer, d, Math.abs(expectedAnswer / 1E6), error);
                assertEquals(expectedTrailing, trailingText, error);
                assertEquals(Locale.TRADITIONAL_CHINESE, Locale.getDefault(), error);

                // test chaining with a default locale
                Locale.setDefault(locale);
                np = makeNumberParserChaining(lenient, trailing, null);
                d = np.parseDouble(text);
                trailingText = text.substring(np.getTrailingPosition());
                assertEquals(expectedAnswer, d, Math.abs(expectedAnswer / 1E6), error);
                assertEquals(expectedTrailing, trailingText, error);
            }
            catch (NumberFormatException nfe)
            {
                throw new NumberFormatException(nfe.getMessage() + ": " + error);
            }
        }
    }

    /**
     * Test a parsing testcase for Float values.
     * @param text String; the text to parse
     * @param expectedAnswer float; the expected numerical answer
     * @param expectedTrailing String the expected trailing info
     * @param strict boolean; whether the text String is strict or not
     * @param locale Locale; the locale to test for
     */
    public void testFloat(final String text, final float expectedAnswer, final String expectedTrailing, final boolean strict,
            final Locale locale)
    {
        boolean lenient = !strict;
        boolean[] testTrailing = expectedTrailing.length() == 0 ? new boolean[] {false, true} : new boolean[] {true};
        for (boolean trailing : testTrailing)
        {
            String error = String.format("parsing [%s], lenient=%b, trailing=%b, locale=%s", text, lenient, trailing, locale);
            try
            {
                // test without restrictions
                NumberParser np = new NumberParser(trailing, lenient, locale);
                float f = np.parseFloat(text);
                String trailingText = text.substring(np.getTrailingPosition());
                assertEquals(expectedAnswer, f, Math.abs(expectedAnswer / 1E6f), error);
                assertEquals(expectedTrailing, trailingText, error);

                // test by setting a different locale
                Locale.setDefault(Locale.TRADITIONAL_CHINESE);
                np = new NumberParser(trailing, lenient, locale);
                f = np.parseFloat(text);
                trailingText = text.substring(np.getTrailingPosition());
                assertEquals(expectedAnswer, f, Math.abs(expectedAnswer / 1E6f), error);
                assertEquals(expectedTrailing, trailingText, error);
                assertEquals(Locale.TRADITIONAL_CHINESE, Locale.getDefault(), error);

                // test by using a default locale
                Locale.setDefault(locale);
                np = new NumberParser(trailing, lenient);
                f = np.parseFloat(text);
                trailingText = text.substring(np.getTrailingPosition());
                assertEquals(expectedAnswer, f, Math.abs(expectedAnswer / 1E6f), error);
                assertEquals(expectedTrailing, trailingText, error);

                // test chaining
                Locale.setDefault(Locale.TRADITIONAL_CHINESE);
                np = makeNumberParserChaining(lenient, trailing, locale);
                f = np.parseFloat(text);
                trailingText = text.substring(np.getTrailingPosition());
                assertEquals(expectedAnswer, f, Math.abs(expectedAnswer / 1E6f), error);
                assertEquals(expectedTrailing, trailingText, error);
                assertEquals(Locale.TRADITIONAL_CHINESE, Locale.getDefault(), error);

                // test chaining with a default locale
                Locale.setDefault(locale);
                np = makeNumberParserChaining(lenient, trailing, null);
                f = np.parseFloat(text);
                trailingText = text.substring(np.getTrailingPosition());
                assertEquals(expectedAnswer, f, Math.abs(expectedAnswer / 1E6f), error);
                assertEquals(expectedTrailing, trailingText, error);
            }
            catch (NumberFormatException nfe)
            {
                throw new NumberFormatException(nfe.getMessage() + ": " + error);
            }
        }
    }

    /**
     * Test whether parsing of double fails as expected.
     * @param text String; the text to parse
     * @param strict boolean; whether to apply strict parsing or not
     * @param trailing boolean; whether to allow trailing or not
     * @param locale Locale; the locale to use
     */
    public void testDoubleFail(final String text, final boolean strict, final boolean trailing, final Locale locale)
    {
        String error = String.format("parsing [%s], strict=%b, trailing=%b, locale=%s", text, strict, trailing, locale);
        Try.testFail(() -> makeNumberParserChaining(!strict, trailing, locale).parseDouble(text), error,
                NumberFormatException.class);
    }

    /**
     * Test whether parsing of float fails as expected.
     * @param text String; the text to parse
     * @param strict boolean; whether to apply strict parsing or not
     * @param trailing boolean; whether to allow trailing or not
     * @param locale Locale; the locale to use
     */
    public void testFloatFail(final String text, final boolean strict, final boolean trailing, final Locale locale)
    {
        String error = String.format("parsing [%s], strict=%b, trailing=%b, locale=%s", text, strict, trailing, locale);
        Try.testFail(() -> makeNumberParserChaining(!strict, trailing, locale).parseFloat(text), error,
                NumberFormatException.class);
    }

    /**
     * Make a NumberParser through chaining with all possible combinations.
     * @param lenient boolean; lenient or strict
     * @param trailing boolean; trailing info on or off
     * @param locale Locale; when null, no locale is set (using Locale.getDefault())
     * @return NumberParser made through chaining
     */
    NumberParser makeNumberParserChaining(final boolean lenient, final boolean trailing, final Locale locale)
    {
        NumberParser np;
        if (lenient)
        {
            if (trailing)
            {
                if (locale == null)
                {
                    np = new NumberParser().lenient().trailing();
                }
                else
                {
                    np = new NumberParser().lenient().trailing().locale(locale);
                }
            }
            else
            {
                if (locale == null)
                {
                    np = new NumberParser().lenient().noTrailing();
                }
                else
                {
                    np = new NumberParser().lenient().noTrailing().locale(locale);
                }
            }
        }
        else
        {
            if (trailing)
            {
                if (locale == null)
                {
                    np = new NumberParser().strict().trailing();
                }
                else
                {
                    np = new NumberParser().strict().trailing().locale(locale);
                }
            }
            else
            {
                if (locale == null)
                {
                    np = new NumberParser().strict().noTrailing();
                }
                else
                {
                    np = new NumberParser().strict().noTrailing().locale(locale);
                }
            }
        }
        return np;
    }

    /** */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    static class TestCase
    {
        /** text to parse. */
        final String text;

        /** expected value. */
        final double expectedValue;

        /** expected trailing characters. */
        final String expectedTrail;

        /**
         * Make a test case.
         * @param text String text to parse
         * @param expectedValue double; expected value
         * @param expectedTrail String expected trailing characters
         */
        TestCase(final String text, final double expectedValue, final String expectedTrail)
        {
            super();
            this.text = text;
            this.expectedValue = expectedValue;
            this.expectedTrail = expectedTrail;
        }
    }

    /** */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    static class TestCaseFail
    {
        /** text to parse. */
        final String text;

        /** strict. */
        final boolean strict;

        /** trailing. */
        final boolean trailing;

        /**
         * Make a failed test case.
         * @param text String text to parse
         * @param strict boolean; strict or not
         * @param trailing boolean; trailing allowed or not
         */
        TestCaseFail(final String text, final boolean strict, final boolean trailing)
        {
            super();
            this.text = text;
            this.strict = strict;
            this.trailing = trailing;
        }
    }

}
