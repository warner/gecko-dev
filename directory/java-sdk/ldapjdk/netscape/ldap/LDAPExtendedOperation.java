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
package netscape.ldap;

/**
 * Version 3 of the LDAP protocol include the means to define additional
 * operations (&quot;extended operations&quot;) beyond the standard LDAP
 * operations. An LDAP v3 client can send an extended operation request,
 * identifying the operation by its unique object ID (OID).  The server
 * receives the request and if OID corresponds to an operation supported
 * by the server, the server procoess the request and sends an extended
 * operation response back to the client.
 * <P>
 *
 * Objects of this class can be used to represent extended operation
 * requests (sent by your client) or extended operation responses
 * (returned by an LDAP v3 server).
 * <P>
 *
 * To determine which extended operations are supported by a server,
 * you need to search for the root DSE (DSA-specific entry, where DSA is
 * another term for &quot;LDAP server&quot;) and find the values of the
 * <CODE>supportedExtension</CODE> attribute.  This attribute contains the
 * object IDs (OIDs) of the extended operations supported by this server.
 * <P>
 *
 * The following section of code demonstrates how to get the list
 * of the extended operations supported by an LDAP server.
 * <P>
 *
 * <PRE>
 * public static void main( String[] args )
 * {
 *   LDAPConnection ld = new LDAPConnection();
 *   try {
 *     String MY_HOST = "localhost";
 *     int MY_PORT = 389;
 *     ld.connect( MY_HOST, MY_PORT );
 *     try {
 *       ld.authenticate( 3, "cn=Directory Manager", "23skidoo" );
 *     } catch( LDAPException e ) {
 *       System.out.println( "LDAP server does not support v3." );
 *       ld.disconnect();
 *       System.exit(1);
 *     }
 *
 *     String MY_FILT = "(objectclass=*)";
 *     String MY_BASE = "";
 *     String getAttrs[] = { "supportedExtension" };
 *     LDAPSearchResults res = ld.search( MY_BASE,
 *       LDAPConnection.SCOPE_BASE, MY_FILT, getAttrs, false );
 *
 *     while ( res.hasMoreElements() ) {
 *       LDAPEntry findEntry = (LDAPEntry)res.nextElement();
 *       LDAPAttributeSet findAttrs = findEntry.getAttributeSet();
 *       Enumeration enumAttrs = findAttrs.getAttributes();
 *
 *         while ( enumAttrs.hasMoreElements() ) {
 *           LDAPAttribute anAttr = (LDAPAttribute)enumAttrs.nextElement();
 *           String attrName = anAttr.getName();
 *           System.out.println( attrName );
 *           Enumeration enumVals = anAttr.getStringValues();
 *
 *           while ( enumVals.hasMoreElements() ) {
 *             String aVal = ( String )enumVals.nextElement();
 *             System.out.println( "\t" + aVal );
 *           }
 *         }
 *      }
 *   }
 *   catch( LDAPException e ) {
 *     System.out.println( "Error: " + e.toString() );
 *   }
 *   try {
 *     ld.disconnect();
 *   }
 *   catch( LDAPException e ) {
 *     System.exit(1);
 *   }
 *   System.exit(0);
 * }
 * </PRE>
 * <P>
 *
 * If you compile and run this example against an LDAP server that
 * supports v3 of the protocol, you might receive the following results:
 * <P>
 *
 * <PRE>
 * supportedextension
 *   1.2.3.4
 * </PRE>
 * <P>
 *
 * For more information on LDAP controls, see the Internet-Draft on
 * the LDAP v3 protocol. (Note that this internet draft is still a
 * work in progress.  You can find the latest draft at the <A
 * HREF="http://www.ietf.cnri.reston.va.us/html.charters/asid-charter.html"
 * TARGET="_blank">ASID home page</A>.
 * <P>
 *
 * @version 1.0
 * @see netscape.ldap.LDAPConnection#extendedOperation(netscape.ldap.LDAPExtendedOperation)
 * @see netscape.ldap.LDAPConnection#search(java.lang.String, int, java.lang.String, java.lang.String[], boolean)
 *
 */
public class LDAPExtendedOperation {

    /**
     * Construct an object
     * @param oid Identifier for the particular operation.
     * @param vals Operation-specific data.
     */
    public LDAPExtendedOperation( String oid, byte[] vals ) {
        m_oid = oid;
        m_vals = vals;
    }

    /**
     * Get the identifier for this operation.
     * @return oid Identifier for the particular operation.
     */
    public String getID() {
        return m_oid;
    }

    /**
     * Get the data for this operation.
     * @return vals Operation-specific data.
     */
    public byte[] getValue() {
        return m_vals;
    }

    private String m_oid;
    private byte[] m_vals;
}
