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
package netscape.ldap.client.opers;

import java.util.*;
import netscape.ldap.client.*;
import netscape.ldap.ber.stream.*;
import java.io.*;
import java.net.*;

/**
 * This class implements the bind request. This object is
 * sent to the ldap server.
 * <pre>
 * BindRequest ::= [APPLICATION 0] SEQUENCE {
 *   version INTEGER(1..127) ,
 *   name LDAPDN,
 *   authentication CHOICE {
 *     simple [0] OCTET STRING,
 *     krbv42LDAP [1] OCTET STRING,
 *     krbv42DSA [2] OCTET STRING
 *   }
 * }
 * </pre>
 *
 * Note that LDAPv3 bind reuqest is structured as follows:
 * <pre>
 * BindRequest ::= [APPLICATION 0] SEQUENCE {
 *   version INTEGER (1..127)
 *   name LDAPDN,
 *   authentication AuthenticationChoice
 * }
 * AuthenticationChoice ::= CHOICE {
 *   simple [0] OCTET STRING,
 *          -- 1 and 2 reserved
 *   sasl [3] SaslCredentials
 * }
 * SaslCredentials ::= SEQUENCE {
 *   mechanism LDAPString,
 *   credentials OCTET STRING
 * }
 * </pre>
 *
 * @version 1.0
 */
public class JDAPBindRequest implements JDAPProtocolOp {
    /**
     * Internal variables
     */
    protected int m_version;
    protected String m_name = null;
    protected String m_password = null;
    protected String m_mechanism = null;
    protected byte m_credentials[] = null;

    /**
     * Constructs anonymous or simple bind request.
     * @param version version
     * @param name distinguished name
     * @param password password
     */
    public JDAPBindRequest(int version, String name, String password) {
        m_version = version;
        m_name = name;
        m_password = password;
    }

    /**
     * Constructs a LDAP v3.0 SaslCredentials bind request.
     * @param version version
     * @param name distinguished name
     * @param mechanism mechanism (must not be null)
     * @param credentials credientials
     */
    public JDAPBindRequest(int version, String name, String mechanism,
      byte credentials[]) {
        m_version = version;
        m_name = name;
        m_mechanism = mechanism;
        m_credentials = credentials;
    }

    /**
     * Retrieves the protocol operation type.
     * @return protocol type
     */
    public int getType() {
        return JDAPProtocolOp.BIND_REQUEST;
    }

    /**
     * Retrieves the ber representation of the request.
     * @return ber representation
     */
    public BERElement getBERElement() {
        /* anonymous bind
         * [*] umich-ldap-v3.3:
         *     0x60 0x07        (implicit [Application 0] Sequence)
         *     0x02 0x01 0x02   (Integer)
         *     0x04 0x00        (OctetString)
         *     0x80 0x00        (implicit OctetString)
         * [*] zoomit server v1.0:
         *     0x60 0x0b
         *     0x30 0x09        (sequece)
         *     0x02 0x01 0x02
         *     0x04 0x00
         *     0xa0 0x02         (explicit tag)
         *     0x04 0x00
         * simple bind with "cn=root,o=ncware,c=ca", "password"
         * [*] umich-ldap-v3.3:
         *     0x60 0x24        ([APPLICATION 0])
         *     0x02 0x01 0x02   (version - Integer)
         *     0x04 0x15 c n = r o o t , o = n c w a r e ,
         *               c = c a
         *     0x80 0x08 p a s s w o r d
         */
        BERSequence seq = new BERSequence();
        seq.addElement(new BERInteger(m_version));
        seq.addElement(new BEROctetString(m_name));
        BERTag auth = null;
        if (m_mechanism == null) {
            auth = new BERTag(BERTag.CONTEXT, new BEROctetString(m_password), true);
        } else {
            BERSequence sasl = new BERSequence();
            sasl.addElement(new BEROctetString(m_mechanism));
            if (m_credentials == null) {
                sasl.addElement(new BEROctetString((byte[])null));
            } else {
                sasl.addElement(new BEROctetString(m_credentials, 0,
                  m_credentials.length));
            }
            auth = new BERTag(BERTag.SASLCONTEXT|3,  /* SaslCredentials */
              sasl, true);
        }
        seq.addElement(auth);
        BERTag element = new BERTag(BERTag.APPLICATION|BERTag.CONSTRUCTED|0,
          seq, true);
        return element;
    }

    /**
     * Retrieves the string representation of the request parameters.
     * @return string representation parameters
     */
    public String getParamString() {
        return "{version=" + m_version + ", name=" + m_name +
          ", authentication=" + m_password + "}";
    }

    /**
     * Retrieves the string representation of the request.
     * @return string representation
     */
    public String toString() {
        return "BindRequest " + getParamString();
    }
}
