/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c) 2010.                            (c) 2010.
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
*  $Revision: 5 $
*
************************************************************************
*/

package ca.nrc.cadc.reg.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;

import ca.nrc.cadc.util.MultiValuedProperties;
import java.io.File;


/**
 * A very simple caching IVOA Registry client. All the lookups done by this client use a properties
 * file named RegistryClient.properties found via the classpath.
 * </p><p>
 * Note for developers: You can set a system property to force this class to replace the hostname
 * in the resuting URL with the canonical hostname of the local host. This is useful for testing:
 * </p>
 * <pre>
 * ca.nrc.cadc.reg.client.RegistryClient.local=true
 * </pre>
 * </p><p>
 * Note for developers: You can set a system property to force this class to replace the hostname
 * in the resuting URL with an arbitrary hostname. This is useful for testing a specific remote server:
 * </p>
 * <pre>
 * ca.nrc.cadc.reg.client.RegistryClient.host=www.example.com
 * </pre>
 *
 * @author pdowler
 */
public class RegistryClient
{
    private static Logger log = Logger.getLogger(RegistryClient.class);

    private static final String CACHE_FILENAME = RegistryClient.class.getSimpleName() + ".properties";
    private static final String LOCAL_PROPERTY = RegistryClient.class.getName() + ".local";
    private static final String HOST_PROPERTY = RegistryClient.class.getName() + ".host";
    private static final String SHORT_HOST_PROPERTY = RegistryClient.class.getName() + ".shortHostname";
    
    private URL url;
    private MultiValuedProperties mvp;
    
    private String hostname;
    private String shortHostname;

    /**
     * Constructor. Uses a properties file called RegistryClient.properties found in the classpath.
     */
    public RegistryClient()
    {
        try
        {
            File conf = new File(System.getProperty("user.home") + "/config", CACHE_FILENAME);
            URL furl;
            if (conf.exists())
                furl = new URL("file://" + conf.getAbsolutePath());
            else
                furl = RegistryClient.class.getResource("/"+CACHE_FILENAME);

            init(furl, false);
        }
        catch(Exception ex)
        {
            throw new RuntimeException("failed to find URL to " + CACHE_FILENAME, ex);
        }
    }

    /**
     * Constructor. Uses a properties file loaded from the specified url.
     * @param url
     */
    public RegistryClient(URL url)
    {
        init(url, false);
    }

    private void init(URL url, boolean unused)
    {
        this.url = url;
        try
        {
            String localP = System.getProperty(LOCAL_PROPERTY);
            String hostP = System.getProperty(HOST_PROPERTY);
            String shortHostP = System.getProperty(SHORT_HOST_PROPERTY);
            
            log.debug("    local: " + localP);
            log.debug("     host: " + hostP);
            log.debug("shortHost: " + shortHostP);
            if ( "true".equals(localP) )
            {
                log.debug(LOCAL_PROPERTY + " is set, assuming localhost runs the service");
                this.hostname = InetAddress.getLocalHost().getCanonicalHostName();
            }
            else if (shortHostP != null)
            {
                shortHostP = shortHostP.trim();
                if (shortHostP.length() > 0)
                {
                    this.shortHostname = shortHostP;
                }
            }
            else if (hostP != null)
            {
                hostP = hostP.trim();
                if (hostP.length() > 0)
                    this.hostname = hostP;
            }
        }
        catch(UnknownHostException ex)
        {
            log.warn("failed to find localhost name via name resolution (" + ex.toString() + "): using localhost");
            this.hostname = "localhost";
        }
    }
    /**
     * Find the service URL for the service registered under the specified identifier. The
     * identifier must be an IVOA identifier (e.g. with URI scheme os "ivo"). If the service
     * has more than one service URL, one is chosen.
     *
     * @param serviceID
     * @throws MalformedURLException if underlying properties file contains malformed URL
     * @return base URL
     */
    public URL getServiceURL(URI serviceID)
        throws MalformedURLException
    {
        return getServiceURL(serviceID, null, null);
    }

    /**
     * Find the service URL for the service registered under the specified identifier and
     * using the specified protocol. The identifier must be an IVOA identifier
     * (e.g. with URI scheme os "ivo"). The protocol argument may be null, in which case this
     * method behaves exactly like getServiceURL(URI).
     *
     * @param serviceID the identifier of the service
     * @param protocol the desired protocol or null if any will do
     * @throws MalformedURLException if underlying properties file contains malformed URL
     * @return base URL or null if a matching service (and protocol) was not found
     */
    public URL getServiceURL(URI serviceID, String protocol)
         throws MalformedURLException
    {
        return getServiceURL(serviceID, protocol, null);
    }
    
    /**
     * Find the service URL for the service registered under the specified identifier and
     * using the specified protocol. The identifier must be an IVOA identifier
     * (e.g. with URI scheme os "ivo"). The protocol argument may be null, in which case this
     * method behaves exactly like getServiceURL(URI).
     *
     * @param serviceID the identifier of the service
     * @param protocol the desired protocol or null if any will do
     * @param path a resource path to append to the base url (null to get the base url)
     * @throws MalformedURLException if underlying properties file contains malformed URL
     * @return base URL or null if a matching service (and protocol) was not found
     */
    public URL getServiceURL(URI serviceID, String protocol, String path)
        throws MalformedURLException
    {
        init();
        log.debug("getServiceURL: " + serviceID + "," + protocol + "," + path);

        //List<URL> urls = lookup.get(serviceID);
        List<String> urls = mvp.getProperty(serviceID.toString());
        if (urls == null || urls.isEmpty() )
        {
            return null; // no matching serviceURI
        }
        // default: first one in list
        String surl = urls.get(0);

        if (protocol != null)
        {
            for (String u : urls)
            {
                if ( u.startsWith(protocol + "://") )
                    surl = u;
            }
            if ( !surl.startsWith(protocol + "://") )
            {
                return null; // no matching protocol
            }
        }

        StringBuilder sb = new StringBuilder();

        if (hostname != null || shortHostname != null)
        {
            URL ret = new URL(surl);
            sb.append(ret.getProtocol());
            sb.append("://");
            if (shortHostname != null)
            {
                String hname = shortHostname;
                String fqhn = ret.getHost();
                int i = fqhn.indexOf('.');
                if (i > 0)
                {
                    String domain = fqhn.substring(i);
                    hname += domain;
                }
                sb.append(hname);
            }
            else
            {
                sb.append(hostname);
            }
            int p = ret.getPort();
            if (p > 0 && p != ret.getDefaultPort())
            {
                sb.append(":");
                sb.append(p);
            }
            sb.append(ret.getPath());
        }
        else
            sb.append(surl);

        if (path != null)
            sb.append(path);

        return new URL(sb.toString());
    }

    private void init()
    {
        if (mvp != null)
            return;

        InputStream istream = null;
        try
        {
            // find the cache resource from the url
            if (url == null)
                throw new RuntimeException("failed to find cache resource.");

            // read the properties
            log.debug("init: reading config from " + url);
            istream = url.openStream();
            this.mvp = new MultiValuedProperties();
            mvp.load(istream);

            if (log.isDebugEnabled())
            {
                for (String k : mvp.keySet())
                {
                    List<String> values = mvp.getProperty(k);
                    for (String v : values)
                    {
                        log.debug(k + " = " + v);
                    }
                }
            }
        }
        catch(IOException ex)
        {
            throw new RuntimeException("failed to load resource: " + CACHE_FILENAME, ex);
        }
        finally
        {
            if (istream != null)
                try { istream.close(); }
                catch(Throwable t) 
                { 
                    log.warn("failed to close " + url, t); 
                }
        }
    }
}
