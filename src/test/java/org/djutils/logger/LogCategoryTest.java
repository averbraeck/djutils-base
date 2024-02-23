package org.djutils.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * LogCategoryTest.java.
 * <p>
 * Copyright (c) 2019-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class LogCategoryTest
{
    /**
     * Test the LogCategory.
     */
    @Test
    public void testLogCategory()
    {
        LogCategory lc1a = new LogCategory("LC1");
        assertNotNull(lc1a);
        LogCategory lc1b = new LogCategory("LC1");
        assertEquals(lc1a, lc1b);
        assertEquals(lc1a, lc1a);
        LogCategory lcnull = new LogCategory(null);
        assertNotNull(lcnull);
        assertNotEquals(lcnull, lc1b);
        assertNotEquals(lc1a, lcnull);
        assertEquals(lcnull, lcnull);
        // random factor in hashCode for null or "" object
        assertNotEquals(new LogCategory(""), new LogCategory(""));
        assertNotEquals(lcnull, new LogCategory(null));
        assertNotEquals(lcnull, new LogCategory(""));
        assertNotEquals(lc1a, new Object());
        assertNotEquals(lc1a, null);
        assertTrue(lc1a.toString().contains("LC1"));
    }

}
