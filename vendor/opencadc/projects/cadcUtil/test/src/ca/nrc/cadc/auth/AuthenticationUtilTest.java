/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2009.                            (c) 2009.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 *  $Revision: 4 $
 *
 ************************************************************************
 */

package ca.nrc.cadc.auth;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.security.Principal;
import javax.security.auth.x500.X500Principal;
import java.security.PrivilegedAction;
import java.security.cert.X509Certificate;
import java.util.Collection;
import javax.security.auth.Subject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import ca.nrc.cadc.util.Log4jInit;
import java.security.PrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


/**
 * Tests the methods in AuthenticationUtil
 * 
 * @author majorb
 *
 */
public class AuthenticationUtilTest
{
    
    private static Logger log = Logger.getLogger(AuthenticationUtilTest.class);
    
    @BeforeClass
    public static void beforeClass()
    {
        Log4jInit.setLevel("ca.nrc.cadc.auth", Level.INFO);
    }
    
    @Test
    public void testHttpEqualsTrue()
    {
        String[][] testSet = new String[][]
              {
                  {"cadcregtest1", "cadcregtest1"} // same value
              };
        for (String[] userIdPair : testSet)
        {
            Principal p1 = new HttpPrincipal(userIdPair[0]);
            Principal p2 = new HttpPrincipal(userIdPair[1]);
            assertTrue(String.format("Should be equal: [%s] and: [%s]", userIdPair[0], userIdPair[1]),
                    AuthenticationUtil.equals(p1, p2));
            assertTrue(String.format("Should be equal: [%s] and: [%s]", userIdPair[1], userIdPair[0]),
                    AuthenticationUtil.equals(p2, p1));
        }
    }
    
    @Test
    public void testHttpEqualsFalse()
    {
        String[][] testSet = new String[][]
        {
            {"cadcregtest1", "cadcregtest2"}, // different values
            {"cadcregtest1", "CADCREGTEST11"} // case should be sensitive
        };
        for (String[] userIdPair : testSet)
        {
            Principal p1 = new HttpPrincipal(userIdPair[0]);
            Principal p2 = new HttpPrincipal(userIdPair[1]);
            assertFalse(String.format("Should be unequal: [%s] and: [%s]", userIdPair[0], userIdPair[1]),
                    AuthenticationUtil.equals(p1, p2));
            assertFalse(String.format("Should be unequal: [%s] and: [%s]", userIdPair[1], userIdPair[0]),
                    AuthenticationUtil.equals(p2, p1));
        }
    }
 
    @Test
    public void testX500EqualsTrue()
    {
        String[][] testSet = new String[][]
            {
                {"cn=cadc regtest1 10577,ou=cadc,o=hia", "cn=cadc regtest1 10577,ou=cadc,o=hia"},    // same value
                {"cn=cadc regtest1 10577,ou=cadc,o=hia", "ou=cadc,o=hia,cn=cadc regtest1 10577"},    // mixed elements
                {"cn=cadc regtest1 10577,ou=cadc,o=hia", "OU=CADC,O=HIA,CN=CADC REGTEST1 10577"},    // upper case
                {"cn=cadc regtest1 10577,ou=cadc,o=hia", "ou=cadc, o=hia, cn=cadc regtest1 10577"},  // mid-dn spaces
                {"cn=cadc regtest1 10577,cn=x123,cn=p,ou=cadc,o=hia", "cn=p,cn=x123,cn=cadc regtest1 10577,ou=cadc,o=hia"},    // multiple rdns
                {"cn=cadc regtest1 10577,cn=a,ou=cadc,o=hia", "cn=a,cn=cadc regtest1 10577,ou=cadc,o=hia"},  // multiple rdn values
            };
        for (String[] dnPair : testSet)
        {
            Principal p1 = new X500Principal(dnPair[0]);
            Principal p2 = new X500Principal(dnPair[1]);
            assertTrue(String.format("Should be equal: [%s] and: [%s]", dnPair[0], dnPair[1]),
                    AuthenticationUtil.equals(p1, p2));
            assertTrue(String.format("Should be equal: [%s] and: [%s]", dnPair[1], dnPair[0]),
                    AuthenticationUtil.equals(p2, p1));
        }
    }
    
    @Test
    public void testX500EqualsFalse()
    {
        String[][] testSet = new String[][]
            {
                {"cn=cadc regtest1 10577,ou=cadc,o=hia", "cn=cadc regtest2 10577,ou=cadc,o=hia"}
            };
        for (String[] dnPair : testSet)
        {
            Principal p1 = new X500Principal(dnPair[0]);
            Principal p2 = new X500Principal(dnPair[1]);
            assertFalse(String.format("Should be unequal: [%s] and: [%s]", dnPair[0], dnPair[1]),
                    AuthenticationUtil.equals(p1, p2));
            assertFalse(String.format("Should be unequal: [%s] and: [%s]", dnPair[1], dnPair[0]),
                    AuthenticationUtil.equals(p2, p1));
        }
    }
    
    @Test
    public void testEqualsMixedPrincipalTypes()
    {
        String[][] testSet = new String[][]
        {
            {"cn=cadc regtest1 10577,ou=cadc,o=hia", "cadcregtest1"}
        };
        for (String[] namePair : testSet)
        {
            Principal p1 = new X500Principal(namePair[0]);
            Principal p2 = new HttpPrincipal(namePair[1]);
            assertFalse(String.format("Should be unequal: [%s] and: [%s]", namePair[1], namePair[0]),
                    AuthenticationUtil.equals(p1, p2));
            assertFalse(String.format("Should be unequal: [%s] and: [%s]", namePair[0], namePair[1]),
                    AuthenticationUtil.equals(p2, p1));
        }
    }
    
    private void testCanonicalConversion(String expected, String[] conversions)
    {
        for (String toBeConverted : conversions)
        {
            try
            {
                // convert the string
                String converted = AuthenticationUtil.canonizeDistinguishedName(toBeConverted);
                assertEquals("[" + toBeConverted + "] should be coverted to expected.",
                        expected, converted);
                
                try
                {
                    // convert again to ensure no loss of data
                    String convertedAgain = AuthenticationUtil.canonizeDistinguishedName(converted);
                    assertEquals("[" + toBeConverted + "] should be coverted (second time) to expected.",
                            converted, convertedAgain);
                }
                catch (IllegalArgumentException e)
                {
                    assertTrue("Converting [" + toBeConverted + "] threw IllegalArguementException on second conversion.",
                            false);
                }
            }
            catch (IllegalArgumentException e)
            {
                assertTrue("Converting [" + toBeConverted + "] threw IllegalArguementException on first conversion.",
                        false);
            }
        }
    }
    
    @Test
    public void testCanonicalConversionSuccess()
    {
        String expected = null;
        String[] conversions = null;
        
        // Proxy type DN conversions
        expected = "cn=cadc regtest1 10577,ou=cadc,o=hia";
        conversions = new String[]
            {
                "cn=cadc regtest1 10577,ou=cadc,o=hia",     // same value
                "CN=CADC REGTEST1 10577,OU=CADC,O=HIA",     // all upper case
                "cN=cadc REGtest1 10577,ou=CADC,O=HiA",     // mixed case
                "cn=cadc regtest1 10577, ou=cadc, o=hia",   // one space between elements
                "cn=cadc regtest1 10577,  ou=cadc,  o=hia", // two spaces between elements
                "Cn=cadc regtest1 10577,  ou=cADc,  O=hiA", // two spaces between elements, mixed case
                " cn=cadc regtest1 10577,ou=cadc,o=hia ",   // leading/trailing spaces
                "o=hia,ou=cadc,cn=cadc regtest1 10577",     // reverse element order
                "O=hiA,ou=cadc,cN=cadc regTEST1 10577",     // reverse element order, mixed case
                "o=hia,  ou=cadc,  cn=cadc regtest1 10577", // reverse element order, two spaces between elements
                "ou=cadc,cn=cadc regtest1 10577,o=hia",     // mixed order
                "ou=cadc,cN=caDC regtest1 10577,O=HIA",     // mixed order, mixed case
                "/cn=cadc regtest1 10577/ou=cadc/o=hia",    // leading slash separator
                "/o=hia/ou=cadc/cn=cadc regtest1 10577",    // leading slash separator reverse order
                "cn=cadc regtest1 10577/ou=cadc/o=hia",     // slash separator
                "o=hia/ou=cadc/cn=cadc regtest1 10577",     // slash separator reverse order
            };
        testCanonicalConversion(expected, conversions);
        
        // User type DN conversions
        expected = "cn=brian major,ou=hia.nrc.ca,o=grid,c=ca";
        conversions = new String[]
            {
                "cn=brian major,ou=hia.nrc.ca,o=grid,c=ca"
            };
        testCanonicalConversion(expected, conversions);
        
        // DN with mutiples of RDNs
        expected = "cn=a,cn=b,cn=c,ou=hia.nrc.ca,o=grid,c=ca";
        conversions = new String[]
             {
                 "cn=a,cn=b,cn=c,ou=hia.nrc.ca,o=grid,c=ca",
                 "cn=b,cn=c,cn=a,ou=hia.nrc.ca,o=grid,c=ca",
                 "cn=c,cn=a,cn=b,ou=hia.nrc.ca,o=grid,c=ca",
                 "cn=c,cn=b,cn=a,ou=hia.nrc.ca,o=grid,c=ca",
                 "ou=hia.nrc.ca,o=grid,c=ca,cn=c,cn=b,cn=a",
             };
         testCanonicalConversion(expected, conversions);
        
        // DN with comma in element
        expected = "cn=brian\\, major,ou=hia.nrc.ca,o=grid,c=ca";
        conversions = new String[]
             {
                 "cn=brian\\, major,ou=hia.nrc.ca,o=grid,c=ca"
             };
         testCanonicalConversion(expected, conversions);
        
        // DN with equals sign in element
        expected = "cn=brian\\=major,ou=hia.nrc.ca,o=grid,c=ca";
        conversions = new String[]
             {
                 "cn=brian\\=major,ou=hia.nrc.ca,o=grid,c=ca"
             };
         testCanonicalConversion(expected, conversions);
         
         // DN with double quote in element
         expected = "cn=brian\"major,ou=hia.nrc.ca,o=grid,c=ca";
         conversions = new String[]
              {
                  "cn=brian\"major,ou=hia.nrc.ca,o=grid,c=ca"
              };
         testCanonicalConversion(expected, conversions);
          
         // DN with single quote in element
         expected = "cn=brian'major,ou=hia.nrc.ca,o=grid,c=ca";
         conversions = new String[]
              {
                  "cn=brian'major,ou=hia.nrc.ca,o=grid,c=ca"
              };
         testCanonicalConversion(expected, conversions);
    
         // DN with accented letters
         expected = "cn=séverin gaudet,ou=hia.nrc.ca,o=grid,c=ca";
         conversions = new String[]
              {
                  "cn=Séverin Gaudet,ou=hia.nrc.ca,o=grid,c=ca"
              };
         testCanonicalConversion(expected, conversions);

    }
    
    @Test
    public void testCanonicalConversionFailure()
    {
        String[] conversions = null;
        
        conversions = new String[]
            {
                "cn=cadc regtest1 10577,ou=cadc,o=hia,z=a",  // unrecognized element z at end
                "cn=cadc regtest1 10577,z=a,ou=cadc,o=hia",  // unrecognized element z in middle
                "z=a,cn=cadc regtest1 10577,ou=cadc,o=hia",  // unrecognized element z in front
                "foo=cadc regtest1 10577,bar=cadc,o=hia,z=a",  // multiple unrecognized elements
            };
        
        for (String toBeConverted : conversions)
        {
            try
            {
                AuthenticationUtil.canonizeDistinguishedName(toBeConverted);
                assertTrue("[" + toBeConverted + "] should have thrown IllegalArgumentException", false);
            }
            catch (IllegalArgumentException e)
            {
                // expected
                log.debug(e);
            }
        }
        
    }

    @Test
    public void testGetSubjectFromHttpServletRequest_Anon()
    {
        log.debug("testGetSubjectFromHttpServletRequest_Anon - START");
        try
        {
            final HttpServletRequest mockRequest =
                createMock(HttpServletRequest.class);

            expect(mockRequest.getRemoteUser()).andReturn(null).atLeastOnce();
            expect(mockRequest.getCookies()).andReturn(null).atLeastOnce();
            expect(mockRequest.getAttribute(
                    "javax.servlet.request.X509Certificate")).andReturn(null).atLeastOnce();

            replay(mockRequest);
            final Subject subject1 = AuthenticationUtil.getSubject(mockRequest);

            assertEquals(0, subject1.getPrincipals().size());
            assertEquals(0, subject1.getPublicCredentials().size());
            assertEquals(0, subject1.getPrivateCredentials().size());

            verify(mockRequest);
        }
        catch(Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            fail("unexpected exception: " + unexpected);
        }
        finally
        {
            log.debug("testGetSubjectFromHttpServletRequest_anon - DONE");
        }
    }

    @Test
    public void testGetSubjectFromHttpServletRequest_HttpPrincipal()
    {
        log.debug("testGetSubjectFromHttpServletRequest_HttpPrincipal - START");
        try
        {
            final HttpServletRequest mockRequest =
                createMock(HttpServletRequest.class);

            expect(mockRequest.getRemoteUser()).andReturn("foo").atLeastOnce();
            expect(mockRequest.getCookies()).andReturn(null).atLeastOnce();
            expect(mockRequest.getAttribute(
                    "javax.servlet.request.X509Certificate")).andReturn(null).atLeastOnce();

            replay(mockRequest);
            final Subject subject1 = AuthenticationUtil.getSubject(mockRequest);

            assertEquals(1, subject1.getPrincipals().size());
            Principal p = subject1.getPrincipals().iterator().next();
            assertTrue(p instanceof HttpPrincipal);
            HttpPrincipal hp = (HttpPrincipal) p;
            assertEquals("foo", p.getName());
            assertEquals(0, subject1.getPublicCredentials().size());
            assertEquals(0, subject1.getPrivateCredentials().size());

            verify(mockRequest);
        }
        catch(Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            fail("unexpected exception: " + unexpected);
        }
        finally
        {
            log.debug("testGetSubjectFromHttpServletRequest_HttpPrincipal - DONE");
        }
    }

    //@Test
    public void testGetSubjectFromHttpServletRequest_X500Principal()
    {
        log.debug("testGetSubjectFromHttpServletRequest_X500Principal - START");
        try
        {
            final HttpServletRequest mockRequest =
                createMock(HttpServletRequest.class);

            String dn = "";

            Calendar notAfterCal = Calendar.getInstance();
            notAfterCal.set(1977, Calendar.NOVEMBER, 25, 3, 21, 0);
            notAfterCal.set(Calendar.MILLISECOND, 0);
            Date notAfterDate = notAfterCal.getTime();

            X500Principal subjectX500Principal = new X500Principal("CN=CN1,O=O1");
            X500Principal issuerX500Principal = new X500Principal("CN=CN2,O=O2");
            X509Certificate mockCertificate = createMock(X509Certificate.class);
            X509Certificate[] ca = new X509Certificate[] { mockCertificate };

            expect(mockRequest.getRemoteUser()).andReturn(null).atLeastOnce();
            expect(mockRequest.getCookies()).andReturn(null).atLeastOnce();
            expect(mockRequest.getAttribute(
                    "javax.servlet.request.X509Certificate")).andReturn(ca).atLeastOnce();
            expect(mockCertificate.getNotAfter()).andReturn(notAfterDate).once();
            expect(mockCertificate.getSubjectX500Principal()).
                andReturn(subjectX500Principal).once();
            expect(mockCertificate.getIssuerX500Principal()).andReturn(
                issuerX500Principal).once();

            replay(mockRequest);
            final Subject subject1 = AuthenticationUtil.getSubject(mockRequest);

            assertEquals(1, subject1.getPrincipals().size());
            Principal p = subject1.getPrincipals().iterator().next();
            assertTrue(p instanceof X500Principal);
            X500Principal xp = (X500Principal) p;
            assertEquals(subjectX500Principal, xp);
            assertEquals(0, subject1.getPublicCredentials().size());
            assertEquals(0, subject1.getPrivateCredentials().size());

            verify(mockRequest);
        }
        catch(Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            fail("unexpected exception: " + unexpected);
        }
        finally
        {
            log.debug("testGetSubjectFromHttpServletRequest_X500Principal - DONE");
        }
    }

    @Test
    public void getCurrentSubject() throws Exception
    {
        final Subject subject = new Subject();
        final HttpPrincipal principal = new HttpPrincipal("CADCtest");

        subject.getPrincipals().add(principal);

        Subject.doAs(subject, new PrivilegedAction<Void>()
        {
            @Override
            public Void run()
            {
                final Subject currentSubject =
                        AuthenticationUtil.getCurrentSubject();

                assertEquals("Wrong Subject found.", "CADCtest",
                             currentSubject.getPrincipals(
                                     HttpPrincipal.class).toArray(
                                     new HttpPrincipal[1])[0].getName());

                return null;
            }
        });
    }

    @Test
    public void testGetSubjectFromHttpServletRequest_CookiePrincipal() throws Exception
    {
        log.debug("testGetSubjectFromHttpServletRequest_CookiePrincipal - START");

        try
        {
            final Cookie[] cookies = new Cookie[]
                    {
                            new Cookie("SOMECOOKIE", "SOMEVALUE"),
                            new Cookie(SSOCookieManager.DEFAULT_SSO_COOKIE_NAME,
//                                       "sessionID=AAABBB")
                                       "AAABBB")
                    };
            final HttpServletRequest mockRequest =
                    createMock(HttpServletRequest.class);

            expect(mockRequest.getRemoteUser()).andReturn(null).once();
            expect(mockRequest.getCookies()).andReturn(cookies).once();
            expect(mockRequest.getAttribute(
                    "javax.servlet.request.X509Certificate")).andReturn(null).atLeastOnce();

            replay(mockRequest);
            final Subject subject1 = AuthenticationUtil.getSubject(mockRequest);

            assertEquals(1, subject1.getPrincipals().size());
            Principal p = subject1.getPrincipals().iterator().next();
            assertTrue(p instanceof CookiePrincipal);
            CookiePrincipal cp = (CookiePrincipal) p;
            assertEquals("AAABBB", new String(cp.getSessionId()));
            assertEquals(0, subject1.getPublicCredentials().size());
            assertEquals(0, subject1.getPrivateCredentials().size());

            verify(mockRequest);
        }
        catch(Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            fail("unexpected exception: " + unexpected);
        }
        finally
        {
            log.debug("testGetSubjectFromHttpServletRequest_CookiePrincipal - DONE");
        }
    }

    //@Test
    public void testGetSubjectCertKey()
    {
        try
        {
            X509Certificate[] certs = null;
            PrivateKey pk = null;

            // TODO: add some valid input
            Subject s;

            s = AuthenticationUtil.getSubject(certs, pk);

             // TODO: verify output
        }
        catch(Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            fail("unexpected exception: " + unexpected);
        }
        finally
        {
            log.debug("testGetSubjectCertKey - DONE");
        }
    }

    // NOTE: for deprecated methods we currently just verify that the methods are still there
    
    //@Test
    public void testGetSubject_Deprecated()
    {
        try
        {
            String remoteUser = null;
            Collection<X509Certificate> certs = null;
            X509CertificateChain chain = null;

            Subject s;

            s = AuthenticationUtil.getSubject(remoteUser, certs);
            s = AuthenticationUtil.getSubject(remoteUser, chain);
        }
        catch(Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            fail("unexpected exception: " + unexpected);
        }
        finally
        {
            log.debug("testGetSubject_Deprecated - DONE");
        }
    }

    @Test
    public void groupByPrincipalType() throws Exception
    {
        final Subject subject = new Subject();
        final HttpPrincipal p1 = new HttpPrincipal("CADCtest");
        final HttpPrincipal p2 = new HttpPrincipal("USER1");
        final X500Principal p4 =
                new X500Principal("cn=cadctest_636,ou=cadc,o=hia,c=ca");

        subject.getPrincipals().add(p1);
        subject.getPrincipals().add(p2);
        subject.getPrincipals().add(p4);

        Subject.doAs(subject, new PrivilegedAction<Void>()
        {
            @Override
            public Void run()
            {
                final Map<Class<Principal>, Collection<String>> groups =
                        AuthenticationUtil.groupPrincipalsByType();

                assertEquals("Should have two HttpPrincipals.", 2,
                             groups.get(HttpPrincipal.class).size());
                assertEquals("Should have one X500Principals.", 1,
                             groups.get(X500Principal.class).size());

                return null;
            }
        });
    }
}
