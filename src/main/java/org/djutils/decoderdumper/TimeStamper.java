package org.djutils.decoderdumper;

import java.io.IOException;

/**
 * Decoder that returns a time stamp in milliseconds that applies to the time of dump of the first byte on the output line.
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @version $Revision$, $LastChangedDate$, by $Author$, initial version Jan 3, 2019 <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public class TimeStamper implements Decoder
{
    /** Result of next call to getResult. */
    private String result = "";

    @Override
    public String getResult()
    {
        String retVal = this.result;
        this.result = "";
        return retVal;
    }

    @Override
    public int getMaximumWidth()
    {
        return 14;
    }

    @Override
    public boolean append(final int address, final byte theByte) throws IOException
    {
        if (this.result.length() == 0)
        {
            long now = System.currentTimeMillis();
            this.result = String.format("%10d.%03d ", now / 1000, now % 1000);
        }
        return false;
    }

    @Override
    public boolean ignoreForIdenticalOutputCheck()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "TimeStamper [result=" + this.result + "]";
    }

}
