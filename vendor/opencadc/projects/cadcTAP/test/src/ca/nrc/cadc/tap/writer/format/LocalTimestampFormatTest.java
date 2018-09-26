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

package ca.nrc.cadc.tap.writer.format;

import java.text.DateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.tap.writer.format.LocalTimestampFormat;
import ca.nrc.cadc.util.Log4jInit;

/**
 *
 * @author jburke
 */
public class LocalTimestampFormatTest
{
    private static final Logger LOG = Logger.getLogger(LocalTimestampFormatTest.class);
    static
    {
        Log4jInit.setLevel("ca", Level.INFO);
    }
    private static final String DATE_TIME = "2009-01-02T03:04:05.678";
    private static DateFormat formatter;

    public LocalTimestampFormatTest() { }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        formatter = DateUtil.getDateFormat(DateUtil.ISO8601_DATE_FORMAT_MSLOCAL, DateUtil.LOCAL);
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp() { }

    @After
    public void tearDown() { }

    @Test
    public void testFormatValue() throws Exception
    {
        LOG.debug("testFormat");

        LocalTimestampFormat instance = new LocalTimestampFormat();

        Date date = formatter.parse(DATE_TIME);
        Object object;
        String result;

        object = date;
        result = instance.format(object);
        Assert.assertEquals(DATE_TIME, result);

        object = new java.sql.Date(date.getTime());
        result = instance.format(object);
        Assert.assertEquals(DATE_TIME, result);

        object = new java.sql.Timestamp(date.getTime());
        result = instance.format(object);
        Assert.assertEquals(DATE_TIME, result);

        LOG.info("testFormat passed");
    }

    @Test
    public void testFormatNull() throws Exception
    {
        LOG.debug("testFormat");

        LocalTimestampFormat instance = new LocalTimestampFormat();

        Object object = null;
        String expResult = "";
        String result = instance.format(object);
        Assert.assertEquals(expResult, result);

        LOG.info("testFormat passed");
    }
}