/* -*- Mode: C++; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * The contents of this file are subject to the Netscape Public License
 * Version 1.0 (the "NPL"); you may not use this file except in
 * compliance with the NPL.  You may obtain a copy of the NPL at
 * http://www.mozilla.org/NPL/
 *
 * Software distributed under the NPL is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the NPL
 * for the specific language governing rights and limitations under the
 * NPL.
 *
 * The Initial Developer of this code under the NPL is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1998 Netscape Communications Corporation.  All Rights
 * Reserved.
 */
package netscape.ldap.ber.stream;

import java.util.*;
import java.io.*;

/**
 * This class is for the Set object. A set object can contains
 * a set of BER elements.
 *
 * @version 1.0
 * @seeAlso CCITT X.209
 */
public class BERSet extends BERConstruct {
    /**
     * Constructs a set element.
     * @exception failed to construct
     */
    public BERSet() throws IOException {
    }

    /**
     * Constructs a set element with the input stream.
     * @param decoder decoder for application specific ber
     * @param stream input stream
     * @param bytes_read array of 1 int; value incremented by number
     *        of bytes read from stream.
     * @exception IOException failed to construct
     */
    public BERSet(BERTagDecoder decoder, InputStream stream,
        int[] bytes_read) throws IOException {
        super(decoder, stream,bytes_read);
    }

    /**
     * Sends the BER encoding directly to stream.
     * @param stream output stream
     * @exception IOException failed to construct
     */
    public void write(OutputStream stream) throws IOException {
        super.write(stream);
    }

    /**
     * Gets the element type.
     * @param element type
     */
    public int getType() {
        return BERElement.SET;
    }

    /**
     * Gets the string representation.
     * @return string representation of tag
     */
    public String toString() {
        String elements = "";
        for (int i = 0; i < super.size(); i++) {
            if (i != 0)
                elements = elements + ", ";
            elements = elements + ((BERElement)super.elementAt(i)).toString();
        }
        return "Set {" + elements + "}";
    }
}
