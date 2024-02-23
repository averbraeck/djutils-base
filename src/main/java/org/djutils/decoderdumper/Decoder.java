package org.djutils.decoderdumper;

import java.io.IOException;

/**
 * Decoder interface.
 * <p>
 * Copyright (c) 2013-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://djutils.org/docs/current/djutils/licenses.html">DJUTILS License</a>.
 * <p>
 * @version $Revision$, $LastChangedDate$, by $Author$, initial version Jan 3, 2019 <br>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="https://www.tudelft.nl/staff/p.knoppers/">Peter Knoppers</a>
 */
public interface Decoder
{
    /**
     * Retrieve the current result of this Decoder. Decoders that handle multiple byte input may be somewhere within a token.
     * Such Decoders should report a partial result. If no data has been added, the result must be the empty string.
     * @return String; the current result of this Decoder
     */
    String getResult();

    /**
     * Retrieve the maximum width (in characters) of results that this Decoder can return (all shorter results will be padded to
     * this width with spaces, unless this is the last active Decoder).
     * @return int; the maximum width (in characters) of results that this Decoder can return
     */
    int getMaximumWidth();

    /**
     * Decode one (more) byte. This method must return true when a line becomes full due to this call, otherwise this method
     * must return false.
     * @param address int; the address that corresponds with the byte
     * @param theByte byte; the byte
     * @return boolean; true if an output line has been completed by this call; false if at least one more byte can be appended
     *         to the local accumulator before the current output line is full
     * @throws IOException when the output device throws this exception
     */
    boolean append(int address, byte theByte) throws IOException;

    /**
     * If the result of this Decoder should not be used to compare output lines for suppressing identical lines, this method
     * should return true; otherwise it should return false.
     * @return boolean; true if the result of this Decoder should be ignored in testing for identical output lines; otherwise
     *         false
     */
    boolean ignoreForIdenticalOutputCheck();

}
