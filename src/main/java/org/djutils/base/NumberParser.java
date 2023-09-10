package org.djutils.base;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.djutils.exceptions.Throw;

/**
 * NumberParser is a class that can parse a number in a strict or lenient way, and dependent on locale. It also provides help
 * for numbers that have trailing information in the String, such as a unit. The class has been defined to use two ways of
 * defining a parser: The first is a classical manner with a constructor that defines the settings: <br>
 * 
 * <pre>
 *   NumberParser np = new NumberParser(true, true);
 *   String text = "+1.127E3 m/s";
 *   double d = np.parseDouble(text);
 *   String unit = text.substring(np.getTrailingPosition()).trim();
 * </pre>
 * 
 * or, for a simple lenient setting without trailing information: <br>
 * 
 * <pre>
 *   double d = new NumberParser().parseDouble(text);
 * </pre>
 * 
 * Alternatively, chaining can be used: <br>
 * 
 * <pre>
 *   double d = new NumberParser().lenient().locale(Locale.US).noTrailing().parseDouble(text);
 * </pre>
 * 
 * An instantiated NumberParser can be used multiple times, but the class is not thread-safe.
 * <p>
 * Information on how Java handles Locales from version 11 onward can be found at
 * <a href= "https://www.oracle.com/java/technologies/javase/jdk11-suported-locales.html">
 * https://www.oracle.com/java/technologies/javase/jdk11-suported-locales.html</a>.
 * </p>
 * <p>
 * Copyright (c) 2023-2023 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class NumberParser
{
    /** whether we use lenient parsing according to the locale. */
    private boolean lenient;

    /** whether we allow trailing information in the string. */
    private boolean trailing;

    /** the Locale to use. */
    private Locale locale;

    /** the position where the parsing 'stopped', i.e., the first character of trailing information. */
    private int trailingPosition = 0;

    /** static cache for DecimalFormatSymbols. */
    private static Map<Locale, DecimalFormatSymbols> decimalFormatSymbolMap = new LinkedHashMap<>();

    /**
     * Create a new NumberParser, with settings for lenient parsing, whether or not to allow trailing information, and the
     * Locale to use.
     * @param trailing boolean; whether trailing information is accepted
     * @param lenient boolean; when false, strict parsing according to the Locale will be performed; when true, certain
     *            violations will be accepted
     * @param locale Locale; the locale to use for parsing
     * @throws NullPointerException when locale is null
     */
    public NumberParser(final boolean trailing, final boolean lenient, final Locale locale)
    {
        Throw.whenNull(locale, "locale cannot be null");
        this.trailing = trailing;
        this.lenient = lenient;
        this.locale = locale;
    }

    /**
     * Create a new NumberParser, with settings for lenient parsing, whether or not to allow trailing information, and the
     * current Locale.
     * @param trailing boolean; whether trailing information is accepted
     * @param lenient boolean; when false, strict parsing according to the Locale will be performed; when true, certain
     *            violations will be accepted
     * @throws NullPointerException when locale is null
     */
    public NumberParser(final boolean trailing, final boolean lenient)
    {
        this(trailing, lenient, Locale.getDefault());
    }

    /**
     * Create a new NumberParser with lenient parsing and using the current Locale, with a setting whether or not to allow
     * trailing information.
     * @param trailing boolean; whether trailing information is accepted
     */
    public NumberParser(final boolean trailing)
    {
        this(trailing, true, Locale.getDefault());
    }

    /**
     * Create a new NumberParser with lenient parsing, not allowing for trailing information, and using the current Locale.
     */
    public NumberParser()
    {
        this(false, true, Locale.getDefault());
    }

    /**
     * Set the parser to strict parsing. This method is included for chaining, so the following statement can be executed:
     * 
     * <pre>
     * new NumberParser().strict().noTrailing().locale(Locale.US).parseDouble(text);
     * </pre>
     * 
     * @return the current NumberParser for chaining
     */
    public NumberParser strict()
    {
        this.lenient = false;
        return this;
    }

    /**
     * Set the parser to lenient parsing. This method is included for chaining, so the following statement can be executed:
     * 
     * <pre>
     * new NumberParser().lenient().noTrailing().locale(Locale.US).parseDouble(text);
     * </pre>
     * 
     * @return the current NumberParser for chaining
     */
    public NumberParser lenient()
    {
        this.lenient = true;
        return this;
    }

    /**
     * Set the parser to allow for trailing characters when parsing. This method is included for chaining, so the following
     * statement can be executed:
     * 
     * <pre>
     * new NumberParser().lenient().trailing().locale(Locale.US).parseDouble(text);
     * </pre>
     * 
     * @return the current NumberParser for chaining
     */
    public NumberParser trailing()
    {
        this.trailing = true;
        return this;
    }

    /**
     * Set the parser to not allow for trailing characters when parsing. This method is included for chaining, so the following
     * statement can be executed:
     * 
     * <pre>
     * new NumberParser().lenient().noTrailing().locale(Locale.US).parseDouble(text);
     * </pre>
     * 
     * @return the current NumberParser for chaining
     */
    public NumberParser noTrailing()
    {
        this.trailing = false;
        return this;
    }

    /**
     * Set the locale for the parser to use. This method is included for chaining, so the following statement can be executed:
     * 
     * <pre>
     * new NumberParser().lenient().trailing().locale(Locale.US).parseDouble(text);
     * </pre>
     * 
     * @param newLocale Locale; the new Locale to use
     * @return the current NumberParser for chaining
     */
    public NumberParser locale(final Locale newLocale)
    {
        Throw.whenNull(newLocale, "locale cannot be null");
        this.locale = newLocale;
        return this;
    }

    /**
     * Parse a String and return a Number value. Independent whether lenient is true or false, leading and trailing white space
     * will be ignored in the provided text.
     * @param text String; the text to parse
     * @param integerOnly boolean; whether to parse an integer or a floating point value
     * @return Number; the parsed number as part of the text
     * @throws NumberFormatException when the text could not be parsed given the flags
     */
    private Number parse(final String text, final boolean integerOnly)
    {
        Throw.whenNull(text, "Cannot parse value from null string");
        Throw.whenNull(this.locale, "Cannot parse value when Locale is null");
        String cleanLeft = text.stripLeading();
        String clean = cleanLeft.stripTrailing();
        if (!decimalFormatSymbolMap.containsKey(this.locale))
        {
            decimalFormatSymbolMap.put(this.locale, new DecimalFormatSymbols(this.locale));
        }
        boolean removedPlusAfteExponent = false;
        DecimalFormatSymbols dfs = decimalFormatSymbolMap.get(this.locale);
        if (this.lenient)
        {
            // remove a possible starting '+' sign
            if (clean.startsWith("+"))
            {
                clean = clean.substring(1);
                cleanLeft = cleanLeft.substring(1);
            }
            // strip all the grouping separator signs
            char groupingSeparator = dfs.getGroupingSeparator();
            clean = clean.replaceAll("[" + groupingSeparator + "]", "");
            // replace an exponent separator in the wrong case
            String exponentSeparator = dfs.getExponentSeparator();
            clean = clean.replace(exponentSeparator.toLowerCase(), exponentSeparator);
            clean = clean.replace(exponentSeparator.toUpperCase(), exponentSeparator);
            // strip the '+' after the exponent separator, such as 1.23x10^+4 or 1.23E+4
            if (clean.contains(exponentSeparator + "+"))
            {
                clean = clean.replace(exponentSeparator + "+", exponentSeparator);
                removedPlusAfteExponent = true;
            }
        }
        Throw.when(clean.isEmpty(), NumberFormatException.class, "Cannot parse a value from an empty string");
        NumberFormat nf = NumberFormat.getNumberInstance(this.locale);
        nf.setParseIntegerOnly(integerOnly);
        ParsePosition parsePosition = new ParsePosition(0);
        Number number = nf.parse(clean, parsePosition);
        if (parsePosition.getIndex() == 0)
        {
            // parsing did not begin, no number
            throw new NumberFormatException("cannot parse");
        }
        else if (parsePosition.getIndex() != clean.length())
        {
            // parsing did not end at the end of the String
            if (this.trailing)
            {
                if (this.lenient)
                {
                    this.trailingPosition = 0;
                    int index = 0;
                    boolean removedPlusAfterExponentInNumber = removedPlusAfteExponent
                            && clean.substring(0, parsePosition.getIndex() - 1).contains(dfs.getExponentSeparator());
                    while (index < parsePosition.getIndex())
                    {
                        if (cleanLeft.charAt(index) == dfs.getGroupingSeparator())
                        {
                            this.trailingPosition++;
                        }
                        this.trailingPosition++;
                        index++;
                    }
                    if (removedPlusAfterExponentInNumber)
                    {
                        this.trailingPosition++;
                    }
                    this.trailingPosition += text.length() - cleanLeft.length();
                }
                else
                {
                    this.trailingPosition = parsePosition.getIndex() + text.length() - cleanLeft.length();
                }
                return number.doubleValue();
            }
            throw new NumberFormatException("trailing characters");
        }
        else
        {
            this.trailingPosition = text.length();
            return number.doubleValue();
        }
    }

    /**
     * Parse a String and return a double value. Independent whether lenient is true or false, leading and trailing white space
     * will be ignored in the provided text.
     * @param text String; the text to parse
     * @return double; the double number as part of the text
     * @throws NumberFormatException when the text could not be parsed given the flags
     */
    public double parseDouble(final String text)
    {
        return parse(text, false).doubleValue();
    }

    /**
     * Parse a String and return a float value. Independent whether lenient is true or false, leading and trailing white space
     * will be ignored in the provided text.
     * @param text String; the text to parse
     * @return float; the float number as part of the text
     * @throws NumberFormatException when the text could not be parsed given the flags
     */
    public float parseFloat(final String text)
    {
        return parse(text, false).floatValue();
    }

    /**
     * Parse a String and return an int value. Independent whether lenient is true or false, leading and trailing white space
     * will be ignored in the provided text.
     * @param text String; the text to parse
     * @return int; the int number as part of the text
     * @throws NumberFormatException when the text could not be parsed given the flags
     */
    public int parseInt(final String text)
    {
        return parse(text, true).intValue();
    }

    /**
     * Parse a String and return a long value. Independent whether lenient is true or false, leading and trailing white space
     * will be ignored in the provided text.
     * @param text String; the text to parse
     * @return long; the long number as part of the text
     * @throws NumberFormatException when the text could not be parsed given the flags
     */
    public long parseLong(final String text)
    {
        return parse(text, true).longValue();
    }

    /**
     * Return the position in the original String of the first character after the parsing of the number stopped. This means
     * that the trailing String can be retrieved using: <br>
     * 
     * <pre>
     * NumberParser np = new NumberParser();
     * double d = np.parseDouble("12.0 m/s");
     * String unit = text.substring(np.getTrailingPosition()).trim();
     * </pre>
     * 
     * The substring starting with the trailing position returns leading and trailing spaces.
     * @return int; the trailing position that denotes the first character after the parsing of the number stopped
     */
    public int getTrailingPosition()
    {
        return this.trailingPosition;
    }

}
