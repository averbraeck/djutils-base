package org.djutils.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Testing of Identifiable and Describable interfaces.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://djutils.org" target="_blank"> https://djutils.org</a>. The DJUTILS project is
 * distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://djutils.org/docs/license.html" target="_blank"> https://djutils.org/docs/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class IdentifiableTest
{

    /** Class that implements Identifiable. */
    class I implements Identifiable
    {
        /** the id. */
        private String id;

        /**
         * Construct I.
         * @param id the id
         */
        I(final String id)
        {
            this.id = id;
        }

        @Override
        public String getId()
        {
            return this.id;
        }
    }

    /** Class that implements Describable. */
    class D implements Describable
    {
        /** the id. */
        private String id;

        /** the description. */
        private String description;

        /**
         * Construct D.
         * @param id the id
         * @param description the description
         */
        D(final String id, final String description)
        {
            this.id = id;
            this.description = description;
        }

        @Override
        public String getId()
        {
            return this.id;
        }

        /** {@inheritDoc} */
        @Override
        public String getDescription()
        {
            return this.description;
        }
    }

    /** Test the Identifiable and Describable. */
    @Test
    public void testIdentifiable()
    {
        String id = "the_id";
        var vi = new I(id);
        assertEquals(id, vi.getId());
        vi = new I(null);
        assertNull(vi.getId());

        String desc = "the_description";
        var vd = new D(id, desc);
        assertEquals(id, vd.getId());
        assertEquals(desc, vd.getDescription());
        vd = new D(null, null);
        assertNull(vd.getId());
        assertNull(vd.getDescription());
    }
}
