/**
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
************************************************************************
*/
package ca.nrc.cadc.gms;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import ca.nrc.cadc.util.StringBuilderWriter;

/**
 *
 * @author jburke
 */
public class UserWriter
{
    public UserWriter() {}

    /**
     * Write a User to a StringBuilder.
     */
    public static void write(User user, StringBuilder builder)
        throws IOException
    {
        write(user, new StringBuilderWriter(builder));
    }

    /**
     * Write a User to an OutputStream.
     *
     * @param user User to write.
     * @param out OutputStream to write to.
     * @throws IOException if the writer fails to write.
     */
    public static void write(User user, OutputStream out)
        throws IOException
    {
        OutputStreamWriter outWriter;
        try
        {
            outWriter = new OutputStreamWriter(out, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
        write(user, new BufferedWriter(outWriter));
    }

    /**
     * Write a User to a Writer.
     *
     * @param user User to write.
     * @param writer Writer to write to.
     * @throws IOException if the writer fails to write.
     */
    public static void write(User user, Writer writer)
        throws IOException
    {
        // write out the Document
        write(getUserElement(user), writer);
    }

    /**
     * Build the member Element of a User.
     *
     * @param user User.
     * @return member Element.
     */
    public static Element getUserElement(User user)
    {
        // Create the user element Element.
        Element userElement = new Element("member");
        userElement.setAttribute("dn", user.getID().getName());

        // Add the username Element.
        for( ElemProperty property : user.getProperties() )
        {
            userElement.addContent(PropertyWriter.write(property));
        }
        
        if( user.getGMSMemberships() != null )
        {
            Element membershipElem = new Element("membershipGroups");
            for( Group group : user.getGMSMemberships() )
            {
                membershipElem.addContent(GroupWriter.getGroupElement(group));
            }
            userElement.addContent(membershipElem);
        }
        
        return userElement;
    }

    /**
     * Build an element of a user but with DN info only.
     * @param userDN
     * @return element 
     */
    public static Element getUserElement(String userDN)
    {
        Element userElement = new Element("member");
        userElement.setAttribute("dn", userDN);
        return userElement;
    }
    
    /**
     * Write to root Element to a writer.
     *
     * @param root Root Element to write.
     * @param writer Writer to write to.
     * @throws IOException if the writer fails to write.
     */
    private static void write(Element root, Writer writer)
        throws IOException
    {
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        outputter.output(new Document(root), writer);
    }

}
