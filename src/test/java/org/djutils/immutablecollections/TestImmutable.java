package org.djutils.immutablecollections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test the enum Immutable.
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class TestImmutable
{
    /**
     * Test the methods of the enum Immutable.
     */
    @Test
    public void testImmutable()
    {
        assertTrue(Immutable.WRAP.isWrap(), "isWrap");
        assertTrue(Immutable.COPY.isCopy(), "isCopy");
        assertFalse(Immutable.COPY.isWrap(), "isWrap");
        assertFalse(Immutable.WRAP.isCopy(), "isCopy");
        // That was easy.
    }

}
