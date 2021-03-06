//
// Copyright 2002-2011 - Universite de Strasbourg / Centre National de la
// Recherche Scientifique
// ------
//
// SAVOT SAX Engine
//
// Author:  Andre Schaaff
// Address: Centre de Donnees astronomiques de Strasbourg
//          11 rue de l'Universite
//          67000 STRASBOURG
//          FRANCE
// Email:   question@simbad.u-strasbg.fr
//
// -------
//
// In accordance with the international conventions about intellectual
// property rights this software and associated documentation files
// (the "Software") is protected. The rightholder authorizes :
// the reproduction and representation as a private copy or for educational
// and research purposes outside any lucrative use,
// subject to the following conditions:
//
// The above copyright notice shall be included.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON INFRINGEMENT,
// LOSS OF DATA, LOSS OF PROFIT, LOSS OF BARGAIN OR IMPOSSIBILITY
// TO USE SUCH SOFWARE. IN NO EVENT SHALL THE RIGHTHOLDER BE LIABLE
// FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
// For any other exploitation contact the rightholder.
//
//                        -----------
//
// Conformement aux conventions internationales relatives aux droits de
// propriete intellectuelle ce logiciel et sa documentation sont proteges.
// Le titulaire des droits autorise :
// la reproduction et la representation a titre de copie privee ou des fins
// d'enseignement et de recherche et en dehors de toute utilisation lucrative.
// Cette autorisation est faite sous les conditions suivantes :
//
// La mention du copyright portee ci-dessus devra etre clairement indiquee.
//
// LE LOGICIEL EST LIVRE "EN L'ETAT", SANS GARANTIE D'AUCUNE SORTE.
// LE TITULAIRE DES DROITS NE SAURAIT, EN AUCUN CAS ETRE TENU CONTRACTUELLEMENT
// OU DELICTUELLEMENT POUR RESPONSABLE DES DOMMAGES DIRECTS OU INDIRECTS
// (Y COMPRIS ET A TITRE PUREMENT ILLUSTRATIF ET NON LIMITATIF,
// LA PRIVATION DE JOUISSANCE DU LOGICIEL, LA PERTE DE DONNEES,
// LE MANQUE A GAGNER OU AUGMENTATION DE COUTS ET DEPENSES, LES PERTES
// D'EXPLOITATION,LES PERTES DE MARCHES OU TOUTES ACTIONS EN CONTREFACON)
// POUVANT RESULTER DE L'UTILISATION, DE LA MAUVAISE UTILISATION
// OU DE L'IMPOSSIBILITE D'UTILISER LE LOGICIEL, ALORS MEME
// QU'IL AURAIT ETE AVISE DE LA POSSIBILITE DE SURVENANCE DE TELS DOMMAGES.
//
// Pour toute autre utilisation contactez le titulaire des droits.

package cds.savot.sax;

// java
import java.io.*;
import java.net.URL;
import java.util.Vector;

import org.kxml2.io.*;
import org.xmlpull.v1.*;

/**
 * <p>
 * It has been tested with kXML Pull parser implementation
 * </p>
 * 
 * @author Andre Schaaff
 * @version 3.0 (kickoff 31 May 02)
 */
public class SavotSAXEngine implements cds.savot.common.Markups {
    public SavotSAXEngine() {
	try {
	    jbInit();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    // use for debug
    private boolean trace = false;

    // needed for sequential parsing
    protected XmlPullParser parser = null;

    // SAVOT SAX consumer
    SavotSAXConsumer consumer;

    // father of a markup
    Vector<String> father = new Vector<String>();

    /**
     * Constructor
     * 
     * @param consumer
     *            SavotSAXConsumer
     * @param parser
     *            XmlPullParser
     * @param file
     *            a file to parse
     * @param debug
     *            boolean
     */
    public SavotSAXEngine(SavotSAXConsumer consumer, XmlPullParser parser,
	    String file, boolean debug) {

	try {
	    this.parser = parser;
	    this.consumer = consumer;
	    enableDebug(debug);

	    // set the input of the parser
	    FileInputStream inStream = new FileInputStream(new File(file));
	    BufferedInputStream dataBuffInStream = new BufferedInputStream(
		    inStream);

	    parser.setInput(dataBuffInStream, "UTF-8");

	    // parse the stream
	    parse(parser);

	} catch (IOException e) {
	    System.err.println("Exception SavotSAXEngine : " + e);
	} catch (Exception f) {
	    System.err.println("Exception SavotSAXEngine : " + f);
	}
    }

    /**
     * Constructor
     * 
     * @param consumer
     *            SavotSAXConsumer
     * @param parser
     *            XmlPullParser
     * @param url
     *            url to parse
     * @param enc
     *            encoding (example : UTF-8)
     * @param debug
     *            boolean
     */
    public SavotSAXEngine(SavotSAXConsumer consumer, XmlPullParser parser,
	    URL url, String enc, boolean debug) {

	try {
	    this.parser = parser;
	    this.consumer = consumer;
	    enableDebug(debug);
	    // set the input of the parser (with the given encoding)
	    parser.setInput(new DataInputStream(url.openStream()), enc);

	    // parse the stream
	    parse(parser);

	} catch (IOException e) {
	    System.err.println("Exception SavotSAXEngine : " + e);
	} catch (Exception f) {
	    System.err.println("Exception SavotSAXEngine : " + f);
	}
    }

    /**
     * Constructor
     * 
     * @param consumer
     *            SavotSAXConsumer
     * @param parser
     *            XmlPullParser
     * @param instream
     *            stream to parse
     * @param enc
     *            encoding (example : UTF-8)
     * @param debug
     *            boolean
     */
    public SavotSAXEngine(SavotSAXConsumer consumer, XmlPullParser parser,
	    InputStream instream, String enc, boolean debug) {
	// public SavotSAXEngine(XmlPullParser parser, InputStream instream, int
	// mode, String enc) {
	try {
	    this.parser = parser;
	    this.consumer = consumer;
	    enableDebug(debug);

	    // DataInputStream dataInStream = new DataInputStream(instream);
	    BufferedInputStream dataBuffInStream = new BufferedInputStream(
		    instream);

	    // set the input of the parser (with the given encoding)
	    // parser.setInput(new DataInputStream(instream), enc);
	    parser.setInput(dataBuffInStream, enc);

	    // parser the stream
	    parse(parser);

	} catch (IOException e) {
	    System.err.println("Exception SavotSAXEngine : " + e);
	} catch (Exception f) {
	    System.err.println("Exception SavotSAXEngine : " + f);
	}
    }

    /**
     * Parsing engine
     * 
     * @param parser
     *            an XML pull parser (example : kXML)
     *            
     * @throws IOException
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    public void parse(XmlPullParser parser) throws IOException {

	// String name = new String();
	String currentMarkup = "XML";

	try {

	    // envent type
	    int eventType = parser.getEventType();
	    father.addElement((parser.getName()));

	    // while the end of the document is not reach
	    while (eventType != XmlPullParser.END_DOCUMENT) {
		// treatment depending on event type
		switch (eventType) {
		// if a start tag is reach
		case KXmlParser.START_TAG:
		    try {
			// the name of the current tag
			currentMarkup = parser.getName();
			father.addElement((parser.getName()));

			// trace mode
			if (trace)
			    System.err.println("Name ---> " + parser.getName());

			if (currentMarkup != null) {

			    // VOTABLE
			    if (currentMarkup.equalsIgnoreCase(VOTABLE)) {

				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, VERSION) != null) {
					attributes.addElement(VERSION);
					attributes.addElement(parser
						.getAttributeValue(null,
							VERSION));
				    }
				    if (parser.getAttributeValue(null, ID) != null) {
					attributes.addElement(ID);
					attributes.addElement(parser
						.getAttributeValue(null, ID));
				    }
				    consumer.startVotable(attributes);
				}

				// trace mode
				if (trace)
				    System.err.println("VOTABLE begin");
			    } else // DESCRIPTION
			    if (currentMarkup.equalsIgnoreCase(DESCRIPTION)) {
				consumer.startDescription();

				// trace mode
				if (trace)
				    System.err.println("DESCRIPTION begin");
			    } // RESOURCE
			    else if (currentMarkup.equalsIgnoreCase(RESOURCE)) {
				Vector attributes = new Vector();

				if (parser.getAttributeCount() != 0) {

				    if (parser.getAttributeValue(null, NAME) != null) {
					attributes.addElement(NAME);
					attributes.addElement(parser
						.getAttributeValue(null, NAME));
				    }

				    if (parser.getAttributeValue(null, TYPE) != null) {
					attributes.addElement(TYPE);
					attributes.addElement(parser
						.getAttributeValue(null, TYPE));
				    }

				    // new since VOTable 1.1
				    if (parser.getAttributeValue(null, UTYPE) != null) {
					attributes.addElement(UTYPE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, UTYPE));
				    }

				    if (parser.getAttributeValue(null, ID) != null) {
					attributes.addElement(ID);
					attributes.addElement(parser
						.getAttributeValue(null, ID));
				    }
				}
				consumer.startResource(attributes);
				// trace mode
				if (trace)
				    System.err.println("RESOURCE");
			    }
			    // TABLE
			    else if (currentMarkup.equalsIgnoreCase(TABLE)) {
				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {

				    if (parser.getAttributeValue(null, NAME) != null) {
					attributes.addElement(NAME);
					attributes.addElement(parser
						.getAttributeValue(null, NAME));
				    }

				    // new since VOTable 1.1
				    if (parser.getAttributeValue(null, UCD) != null) {
					attributes.addElement(UCD);
					attributes.addElement(parser
						.getAttributeValue(null, UCD));
				    }

				    // new since VOTable 1.1
				    if (parser.getAttributeValue(null, UTYPE) != null) {
					attributes.addElement(UTYPE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, UTYPE));
				    }

				    if (parser.getAttributeValue(null, REF) != null) {
					attributes.addElement(REF);
					attributes.addElement(parser
						.getAttributeValue(null, REF));
				    }

				    if (parser.getAttributeValue(null, ID) != null) {
					attributes.addElement(ID);
					attributes.addElement(parser
						.getAttributeValue(null, ID));
				    }

				    // new since VOTable 1.1
				    if (parser.getAttributeValue(null, NROWS) != null) {
					attributes.addElement(NROWS);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, NROWS));
				    }
				}
				consumer.startTable(attributes);

				// trace mode
				if (trace)
				    System.err.println("TABLE begin");
			    } // FIELD
			    else if (currentMarkup.equalsIgnoreCase(FIELD)) {
				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {

				    if (parser.getAttributeValue(null, UNIT) != null) {
					attributes.addElement(UNIT);
					attributes.addElement(parser
						.getAttributeValue(null, UNIT));
				    }

				    if (parser
					    .getAttributeValue(null, DATATYPE) != null) {
					attributes.addElement(DATATYPE);
					attributes.addElement(parser
						.getAttributeValue(null,
							DATATYPE));
				    }

				    if (parser.getAttributeValue(null,
					    PRECISION) != null) {
					attributes.addElement(PRECISION);
					attributes.addElement(parser
						.getAttributeValue(null,
							PRECISION));
				    }

				    if (parser.getAttributeValue(null, WIDTH) != null) {
					attributes.addElement(WIDTH);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, WIDTH));
				    }
				    
				    // since VOTable 1.2
				    if (parser.getAttributeValue(null, XTYPE) != null) {
					attributes.addElement(XTYPE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, XTYPE));
				    }
				    
				    if (parser.getAttributeValue(null, REF) != null) {
					attributes.addElement(REF);
					attributes.addElement(parser
						.getAttributeValue(null, REF));
				    }

				    if (parser.getAttributeValue(null, NAME) != null) {
					attributes.addElement(NAME);
					attributes.addElement(parser
						.getAttributeValue(null, NAME));
				    }

				    if (parser.getAttributeValue(null, UCD) != null) {
					attributes.addElement(UCD);
					attributes.addElement(parser
						.getAttributeValue(null, UCD));
				    }

				    if (parser.getAttributeValue(null,
					    ARRAYSIZE) != null) {
					attributes.addElement(ARRAYSIZE);
					attributes.addElement(parser
						.getAttributeValue(null,
							ARRAYSIZE));
				    }

				    if (parser.getAttributeValue(null, TYPE) != null) { // deprecated
											// since
											// VOTable
											// 1.1
					attributes.addElement(TYPE);
					attributes.addElement(parser
						.getAttributeValue(null, TYPE));
				    }

				    if (parser.getAttributeValue(null, UTYPE) != null) {
					attributes.addElement(UTYPE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, UTYPE));
				    }

				    if (parser.getAttributeValue(null, ID) != null) {
					attributes.addElement(ID);
					attributes.addElement(parser
						.getAttributeValue(null, ID));
				    }
				}
				consumer.startField(attributes);

				// trace mode
				if (trace)
				    System.err.println("FIELD begin");
			    } // FIELDREF
			    else if (currentMarkup.equalsIgnoreCase(FIELDREF)) {
				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, REF) != null)
					attributes.addElement(REF);
				    attributes.addElement(parser
					    .getAttributeValue(null, REF));
				}
				consumer.startFieldref(attributes);

				// trace mode
				if (trace)
				    System.err.println("FIELDREF begin");
			    } // VALUES
			    else if (currentMarkup.equalsIgnoreCase(VALUES)) {
				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, TYPE) != null) {
					attributes.addElement(TYPE);
					attributes.addElement(parser
						.getAttributeValue(null, TYPE));
				    }

				    if (parser.getAttributeValue(null, NULL) != null) {
					attributes.addElement(NULL);
					attributes.addElement(parser
						.getAttributeValue(null, NULL));
				    }

				    if (parser.getAttributeValue(null, INVALID) != null) { // deprecated
											   // since
											   // VOTable
											   // 1.1
					attributes.addElement(INVALID);
					attributes.addElement(parser
						.getAttributeValue(null,
							INVALID));
				    }

				    if (parser.getAttributeValue(null, REF) != null) {
					attributes.addElement(REF);
					attributes.addElement(parser
						.getAttributeValue(null, REF));
				    }

				    if (parser.getAttributeValue(null, ID) != null) {
					attributes.addElement(ID);
					attributes.addElement(parser
						.getAttributeValue(null, ID));
				    }
				}
				consumer.startValues(attributes);

				// trace mode
				if (trace)
				    System.err.println("VALUES begin");
			    } // STREAM
			    else if (currentMarkup.equalsIgnoreCase(STREAM)) {
				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {

				    if (parser.getAttributeValue(null, TYPE) != null) {
					attributes.addElement(TYPE);
					attributes.addElement(parser
						.getAttributeValue(null, TYPE));
				    }
				    if (parser.getAttributeValue(null, HREF) != null) {
					attributes.addElement(HREF);
					attributes.addElement(parser
						.getAttributeValue(null, HREF));
				    }
				    if (parser.getAttributeValue(null, ACTUATE) != null) {
					attributes.addElement(ACTUATE);
					attributes.addElement(parser
						.getAttributeValue(null,
							ACTUATE));
				    }
				    if (parser
					    .getAttributeValue(null, ENCODING) != null) {
					attributes.addElement(ENCODING);
					attributes.addElement(parser
						.getAttributeValue(null,
							ENCODING));
				    }
				    if (parser.getAttributeValue(null, EXPIRES) != null) {
					attributes.addElement(EXPIRES);
					attributes.addElement(parser
						.getAttributeValue(null,
							EXPIRES));
				    }
				    if (parser.getAttributeValue(null, RIGHTS) != null) {
					attributes.addElement(RIGHTS);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, RIGHTS));
				    }
				}
				consumer.startStream(attributes);

				// trace mode
				if (trace)
				    System.err.println("STREAM begin");
			    } // TR
			    else if (currentMarkup.equalsIgnoreCase(TR)) {
				consumer.startTR();

				// trace mode
				if (trace)
				    System.err.println("TR begin");
			    } // TD
			    else if (currentMarkup.equalsIgnoreCase(TD)) {
				Vector attributes = new Vector();

				if (parser.getAttributeCount() != 0) {
				    if (parser
					    .getAttributeValue(null, ENCODING) != null) {
					attributes.addElement(ENCODING);
					attributes.addElement(parser
						.getAttributeValue(null,
							ENCODING));
				    }
				}
				consumer.startTD(attributes);

				// trace mode
				if (trace)
				    System.err.println("TD begin");
			    } // DATA
			    else if (currentMarkup.equalsIgnoreCase(DATA)) {
				consumer.startData();

				// trace mode
				if (trace)
				    System.err.println("DATA begin");
			    } // BINARY
			    else if (currentMarkup.equalsIgnoreCase(BINARY)) {
				consumer.startBinary();

				// trace mode
				if (trace)
				    System.err.println("BINARY begin");
			    } // FITS
			    else if (currentMarkup.equalsIgnoreCase(FITS)) {
				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, EXTNUM) != null) {
					attributes.addElement(EXTNUM);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, EXTNUM));
				    }
				}
				consumer.startFits(attributes);

				// trace mode
				if (trace)
				    System.err.println("FITS begin");
			    } // TABLEDATA
			    else if (currentMarkup.equalsIgnoreCase(TABLEDATA)) {
				consumer.startTableData();

				// trace mode
				if (trace)
				    System.err.println("TABLEDATA begin");
			    } // PARAM
			    else if (currentMarkup.equalsIgnoreCase(PARAM)) {
				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, UNIT) != null) {
					attributes.addElement(UNIT);
					attributes.addElement(parser
						.getAttributeValue(null, UNIT));
				    }
				    if (parser
					    .getAttributeValue(null, DATATYPE) != null) {
					attributes.addElement(DATATYPE);
					attributes.addElement(parser
						.getAttributeValue(null,
							DATATYPE));
				    }
				    if (parser.getAttributeValue(null,
					    PRECISION) != null) {
					attributes.addElement(PRECISION);
					attributes.addElement(parser
						.getAttributeValue(null,
							PRECISION));
				    }
				    if (parser.getAttributeValue(null, WIDTH) != null) {
					attributes.addElement(WIDTH);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, WIDTH));
				    }
				    // since VOTable 1.2
				    if (parser.getAttributeValue(null, XTYPE) != null) {
					attributes.addElement(XTYPE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, XTYPE));
				    }
				    if (parser.getAttributeValue(null, REF) != null) {
					attributes.addElement(REF);
					attributes.addElement(parser
						.getAttributeValue(null, REF));
				    }
				    if (parser.getAttributeValue(null, NAME) != null) {
					attributes.addElement(NAME);
					attributes.addElement(parser
						.getAttributeValue(null, NAME));
				    }
				    if (parser.getAttributeValue(null, UCD) != null) {
					attributes.addElement(UCD);
					attributes.addElement(parser
						.getAttributeValue(null, UCD));
				    }
				    if (parser.getAttributeValue(null, UTYPE) != null) { // new
											 // since
											 // VOTable
											 // 1.1
					attributes.addElement(UTYPE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, UTYPE));
				    }
				    if (parser.getAttributeValue(null, VALUE) != null) {
					attributes.addElement(VALUE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, VALUE));
				    }
				    if (parser.getAttributeValue(null,
					    ARRAYSIZE) != null) {
					attributes.addElement(ARRAYSIZE);
					attributes.addElement(parser
						.getAttributeValue(null,
							ARRAYSIZE));
				    }
				    if (parser.getAttributeValue(null, ID) != null) {
					attributes.addElement(ID);
					attributes.addElement(parser
						.getAttributeValue(null, ID));
				    }
				}
				consumer.startParam(attributes);

				// trace mode
				if (trace)
				    System.err.println("PARAM begin");
			    } // PARAMREF
			    else if (currentMarkup.equalsIgnoreCase(PARAMREF)) { // new
										 // since
										 // VOTable
										 // 1.1
				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, REF) != null) {
					attributes.addElement(REF);
					attributes.addElement(parser
						.getAttributeValue(null, REF));
				    }
				}
				consumer.startParamRef(attributes);

				// trace mode
				if (trace)
				    System.err.println("PARAMref begin");
			    }
			    // LINK
			    else if (currentMarkup.equalsIgnoreCase(LINK)) {
				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null,
					    CONTENTROLE) != null) {
					attributes.addElement(CONTENTROLE);
					attributes.addElement(parser
						.getAttributeValue(null,
							CONTENTROLE));
				    }
				    if (parser.getAttributeValue(null,
					    CONTENTTYPE) != null) {
					attributes.addElement(CONTENTTYPE);
					attributes.addElement(parser
						.getAttributeValue(null,
							CONTENTTYPE));
				    }
				    if (parser.getAttributeValue(null, TITLE) != null) {
					attributes.addElement(TITLE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, TITLE));
				    }
				    if (parser.getAttributeValue(null, VALUE) != null) {
					attributes.addElement(VALUE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, VALUE));
				    }
				    if (parser.getAttributeValue(null, HREF) != null) {
					attributes.addElement(HREF);
					attributes.addElement(parser
						.getAttributeValue(null, HREF));
				    }
				    if (parser.getAttributeValue(null, GREF) != null) { // deprecated
											// since
											// VOTable
											// 1.1
					attributes.addElement(GREF);
					attributes.addElement(parser
						.getAttributeValue(null, GREF));
				    }
				    if (parser.getAttributeValue(null, ACTION) != null) {
					attributes.addElement(ACTION);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, ACTION));
				    }
				    if (parser.getAttributeValue(null, ID) != null) {
					attributes.addElement(ID);
					attributes.addElement(parser
						.getAttributeValue(null, ID));
				    }
				}
				consumer.startLink(attributes);

				if (trace)
				    System.err.println("LINK begin");
			    }
			    // INFO
			    else if (currentMarkup.equalsIgnoreCase(INFO)) {
				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, NAME) != null) {
					attributes.addElement(NAME);
					attributes.addElement(parser
						.getAttributeValue(null, NAME));
				    }
				    if (parser.getAttributeValue(null, VALUE) != null) {
					attributes.addElement(VALUE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, VALUE));
				    }
				    if (parser.getAttributeValue(null, ID) != null) {
					attributes.addElement(ID);
					attributes.addElement(parser
						.getAttributeValue(null, ID));
				    }
				    // since VOTable 1.2
				    if (parser.getAttributeValue(null, XTYPE) != null) {
					attributes.addElement(XTYPE);
					attributes.addElement(parser
						.getAttributeValue(null, XTYPE));
				    }
				    // since VOTable 1.2
				    if (parser.getAttributeValue(null, REF) != null) {
					attributes.addElement(REF);
					attributes.addElement(parser
						.getAttributeValue(null, REF));
				    }
				    // since VOTable 1.2
				    if (parser.getAttributeValue(null, UNIT) != null) {
					attributes.addElement(UNIT);
					attributes.addElement(parser
						.getAttributeValue(null, UNIT));
				    }
				    // since VOTable 1.2
				    if (parser.getAttributeValue(null, UCD) != null) {
					attributes.addElement(UCD);
					attributes.addElement(parser
						.getAttributeValue(null, UCD));
				    }
				    // since VOTable 1.2
				    if (parser.getAttributeValue(null, UTYPE) != null) {
					attributes.addElement(UTYPE);
					attributes.addElement(parser
						.getAttributeValue(null, UTYPE));
				    }
				}
				consumer.startInfo(attributes);

				if (trace)
				    System.err.println("INFO begin");
			    } // MIN
			    else if (currentMarkup.equalsIgnoreCase(MIN)) {
				Vector attributes = new Vector();
				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, VALUE) != null) {
					attributes.addElement(VALUE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, VALUE));
				    }
				    if (parser.getAttributeValue(null,
					    INCLUSIVE) != null) {
					attributes.addElement(INCLUSIVE);
					attributes.addElement(parser
						.getAttributeValue(null,
							INCLUSIVE));
				    }
				}
				consumer.startMin(attributes);

				// mode trace
				if (trace)
				    System.err.println("MIN begin");
			    }
			    // MAX
			    else if (currentMarkup.equalsIgnoreCase(MAX)) {
				Vector attributes = new Vector();

				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, VALUE) != null) {
					attributes.addElement(VALUE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, VALUE));
				    }
				    if (parser.getAttributeValue(null,
					    INCLUSIVE) != null) {
					attributes.addElement(INCLUSIVE);
					attributes.addElement(parser
						.getAttributeValue(null,
							INCLUSIVE));
				    }
				}
				consumer.startMax(attributes);

				// mode trace
				if (trace)
				    System.err.println("MAX begin ");
			    }
			    // OPTION
			    else if (currentMarkup.equalsIgnoreCase(OPTION)) {
				Vector attributes = new Vector();

				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, NAME) != null) {
					attributes.addElement(NAME);
					attributes.addElement(parser
						.getAttributeValue(null, NAME));
				    }
				    if (parser.getAttributeValue(null, VALUE) != null) {
					attributes.addElement(VALUE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, VALUE));
				    }
				}
				consumer.startOption(attributes);

				// mode trace
				if (trace)
				    System.err
					    .println("OPTION begin - not included");
			    } // GROUP new 1.1
			    else if (currentMarkup.equalsIgnoreCase(GROUP)) {
				Vector attributes = new Vector();

				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, REF) != null) {
					attributes.addElement(REF);
					attributes.addElement(parser
						.getAttributeValue(null, REF));
				    }
				    if (parser.getAttributeValue(null, NAME) != null) {
					attributes.addElement(NAME);
					attributes.addElement(parser
						.getAttributeValue(null, NAME));
				    }
				    if (parser.getAttributeValue(null, UCD) != null) {
					attributes.addElement(UCD);
					attributes.addElement(parser
						.getAttributeValue(null, UCD));
				    }
				    if (parser.getAttributeValue(null, UTYPE) != null) {
					attributes.addElement(UTYPE);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, UTYPE));
				    }
				    if (parser.getAttributeValue(null, ID) != null) {
					attributes.addElement(ID);
					attributes.addElement(parser
						.getAttributeValue(null, ID));
				    }
				}
				consumer.startGroup(attributes);

				// mode trace
				if (trace)
				    System.err
					    .println("GROUP begin - not included");
			    } // COOSYS - deprecated since VOTable 1.2
			    else if (currentMarkup.equalsIgnoreCase(COOSYS)) {
				Vector attributes = new Vector();

				if (parser.getAttributeCount() != 0) {
				    if (parser.getAttributeValue(null, EQUINOX) != null) {
					attributes.addElement(EQUINOX);
					attributes.addElement(parser
						.getAttributeValue(null,
							EQUINOX));
				    }
				    if (parser.getAttributeValue(null, EPOCH) != null) {
					attributes.addElement(EPOCH);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, EPOCH));
				    }
				    if (parser.getAttributeValue(null, SYSTEM) != null) {
					attributes.addElement(SYSTEM);
					attributes
						.addElement(parser
							.getAttributeValue(
								null, SYSTEM));
				    }
				    if (parser.getAttributeValue(null, ID) != null) {
					attributes.addElement(ID);
					attributes.addElement(parser
						.getAttributeValue(null, ID));
				    }
				}
				consumer.startCoosys(attributes);

				// mode trace
				if (trace)
				    System.err.println("COOSYS begin");
			    }
			    // DEFINITIONS - deprecated since VOTable 1.1
			    else if (currentMarkup
				    .equalsIgnoreCase(DEFINITIONS)) {
				consumer.startDefinitions();

				// mode trace
				if (trace)
				    System.err.println("DEFINITIONS begin");
			    } else
				System.err.println("VOTable markup error : "
					+ currentMarkup + " at line "
					+ parser.getLineNumber());
			}
			// currentMarkup = name;
		    } catch (Exception e) {
			System.err.println("Exception START_TAG : " + e
				+ " at line " + parser.getLineNumber());
		    }
		    break;

		// if an end tag is reach
		case KXmlParser.END_TAG:
		    try {

			if (trace)
			    System.err.println("End ---> " + currentMarkup);

			// DESCRIPTION
			if (currentMarkup.equalsIgnoreCase(DESCRIPTION)) {
			    consumer.endDescription();

			    // trace mode
			    if (trace)
				System.err.println("DESCRIPTION");
			} // TABLE
			else if (currentMarkup.equalsIgnoreCase(TABLE)) {
			    consumer.endTable();

			    // trace mode
			    if (trace)
				System.err.println("TABLE");
			} // FIELD
			else if (currentMarkup.equalsIgnoreCase(FIELD)) {
			    consumer.endField();

			    // trace mode
			    if (trace)
				System.err.println("FIELD");
			} // FIELDref
			else if (currentMarkup.equalsIgnoreCase(FIELDREF)) {
			    consumer.endFieldref();

			    // trace mode
			    if (trace)
				System.err.println("FIELDRef");
			} // TR
			else if (currentMarkup.equalsIgnoreCase(TR)) {
			    consumer.endTR();

			    // trace mode
			    if (trace)
				System.err.println("TR");
			} // DATA
			else if (currentMarkup.equalsIgnoreCase(DATA)) {
			    consumer.endData();

			    // trace mode
			    if (trace)
				System.err.println("DATA");
			} // TD
			else if (currentMarkup.equalsIgnoreCase(TD)) {
			    consumer.endTD();

			    // trace mode
			    if (trace)
				System.err.println("TD");
			} // RESOURCE
			else if (currentMarkup.equalsIgnoreCase(RESOURCE)) {
			    consumer.endResource();

			    // trace mode
			    if (trace)
				System.err.println("RESOURCE");
			} // OPTION
			else if (currentMarkup.equalsIgnoreCase(OPTION)) {
			    consumer.endOption();

			    // trace mode
			    if (trace)
				System.err.println("OPTION");
			} // GROUP
			else if (currentMarkup.equalsIgnoreCase(GROUP)) {
			    consumer.endGroup();

			    // trace mode
			    if (trace)
				System.err.println("GROUP");
			}
			// TABLEDATA
			else if (currentMarkup.equalsIgnoreCase(TABLEDATA)) {
			    consumer.endTableData();

			    // trace mode
			    if (trace)
				System.err.println("TABLEDATA");
			}
			// COOSYS - deprecated since VOTable 1.2
			else if (currentMarkup.equalsIgnoreCase(COOSYS)) {
			    consumer.endCoosys();

			    // trace mode
			    if (trace)
				System.err.println("COOSYS");
			}
			// PARAM
			else if (currentMarkup.equalsIgnoreCase(PARAM)) {
			    consumer.endParam();

			    // trace mode
			    if (trace)
				System.err.println("PARAM");
			} else if (currentMarkup.equalsIgnoreCase(PARAMREF)) {
			    consumer.endParamRef();

			    // trace mode
			    if (trace)
				System.err.println("PARAMRef");
			}
			// LINK
			else if (currentMarkup.equalsIgnoreCase(LINK)) {
			    consumer.endLink();

			    // trace mode
			    if (trace)
				System.err.println("LINK");
			} // VALUES
			else if (currentMarkup.equalsIgnoreCase(VALUES)) {
			    consumer.endValues();

			    // trace mode
			    if (trace)
				System.err.println("VALUES");
			} // MIN
			else if (currentMarkup.equalsIgnoreCase(MIN)) {
			    consumer.endMin();

			    // trace mode
			    if (trace)
				System.err.println("MIN");
			} // MAX
			else if (currentMarkup.equalsIgnoreCase(MAX)) {
			    consumer.endMax();

			    // trace mode
			    if (trace)
				System.err.println("MAX");
			}
			// STREAM
			else if (currentMarkup.equalsIgnoreCase(STREAM)) {
			    consumer.endStream();

			    // trace mode
			    if (trace)
				System.err.println("STREAM");
			}
			// BINARY
			else if (currentMarkup.equalsIgnoreCase(BINARY)) {
			    consumer.endBinary();

			    // trace mode
			    if (trace)
				System.err.println("BINARY");
			}
			// FITS
			else if (currentMarkup.equalsIgnoreCase(FITS)) {
			    consumer.endFits();

			    // trace mode
			    if (trace)
				System.err.println("FITS");
			}
			// INFO
			else if (currentMarkup.equalsIgnoreCase(INFO)) {
			    consumer.endInfo();

			    // trace mode
			    if (trace)
				System.err.println("INFO");
			}
			// DEFINITIONS - deprecated since VOTable 1.1
			else if (currentMarkup.equalsIgnoreCase(DEFINITIONS)) {
			    consumer.endDefinitions();

			    // trace mode
			    if (trace)
				System.err.println("DEFINITIONS");
			}
			// VOTABLE
			else if (currentMarkup.equalsIgnoreCase(VOTABLE)) {
			    consumer.endVotable();

			    // trace mode
			    if (trace)
				System.err.println("VOTABLE");
			} else
			    System.err.println("VOTable markup error : "
				    + currentMarkup + " at line "
				    + parser.getLineNumber());

			father.removeElementAt(father.size() - 1);
			currentMarkup = (String) father
				.elementAt(father.size() - 1);

		    } catch (Exception e) {
			System.err.println("Exception END_TAG : " + e
				+ " at line " + parser.getLineNumber());
		    }
		    break;

		case KXmlParser.END_DOCUMENT:
		    try {
			consumer.endDocument();

			// trace mode
			if (trace)
			    System.err.println("Document end reached!");
		    } catch (Exception e) {
			System.err.println("Exception END_DOCUMENT : " + e
				+ " at line " + parser.getLineNumber());
		    }
		    break;

		case KXmlParser.TEXT:
		    try {
			// TD
			if (currentMarkup.equalsIgnoreCase(TD)) {
			    consumer.textTD((parser.getText()).trim());

			    // trace mode
			    if (trace)
				System.err.println("TD : "
					+ (parser.getText()).trim());
			} else // STREAM
			if (currentMarkup.equalsIgnoreCase(STREAM)) {
			    consumer.textStream((parser.getText()).trim());

			    // trace mode
			    if (trace)
				System.err.println("STREAM : "
					+ (parser.getText()).trim());
			} else // DESCRIPTION
			if (currentMarkup.equalsIgnoreCase(DESCRIPTION)) {
			    consumer.textDescription((parser.getText()).trim());

			    // trace mode
			    if (trace)
				System.err.println("DESCRIPTION : "
					+ (parser.getText()).trim());
			} else // MIN
			if (currentMarkup.equalsIgnoreCase(MIN)) {
			    consumer.textMin((parser.getText()).trim());

			    // trace mode
			    if (trace)
				System.err.println("MIN : "
					+ (parser.getText()).trim());
			} else // MAX
			if (currentMarkup.equalsIgnoreCase(MAX)) {
			    consumer.textMax((parser.getText()).trim());

			    // trace mode
			    if (trace)
				System.err.println("MAX : "
					+ (parser.getText()).trim());
			} else // COOSYS - deprecated since VOTable 1.2
			if (currentMarkup.equalsIgnoreCase(COOSYS)) {
			    consumer.textCoosys((parser.getText()).trim());

			    // trace mode
			    if (trace)
				System.err.println("COOSYS : "
					+ (parser.getText()).trim());
			} else // LINK
			if (currentMarkup.equalsIgnoreCase(LINK)) {
			    consumer.textLink((parser.getText()).trim());

			    // trace mode
			    if (trace)
				System.err.println("LINK : "
					+ (parser.getText()).trim());
			} else // OPTION
			if (currentMarkup.equalsIgnoreCase(OPTION)) {
			    consumer.textOption((parser.getText()).trim());

			    // trace mode
			    if (trace)
				System.err.println("OPTION : "
					+ (parser.getText()).trim());
			} else // GROUP
			if (currentMarkup.equalsIgnoreCase(GROUP)) {
			    consumer.textGroup((parser.getText()).trim());

			    // trace mode
			    if (trace)
				System.err.println("GROUP : "
					+ (parser.getText()).trim());
			} else // INFO
			if (currentMarkup.equalsIgnoreCase(INFO)) {
			    consumer.textInfo((parser.getText()).trim());

			    // trace mode
			    if (trace)
				System.err.println("INFO : "
					+ (parser.getText()).trim());
			}
		    } catch (Exception e) {
			System.err.println("Exception TEXT : " + e
				+ " at line " + parser.getLineNumber());
		    }
		    break;

		case KXmlParser.START_DOCUMENT:
		    try {
			consumer.startDocument();

			// trace mode
			if (trace)
			    System.err.println("Document start reached!");
		    } catch (Exception e) {
			System.err.println("Exception START_DOCUMENT : " + e
				+ " at line " + parser.getLineNumber());
		    }
		    break;

		default:
		    if (trace)
			System.err
				.println(" ignoring some other (legacy) event at line : "
					+ parser.getLineNumber());
		}

		// new values
		eventType = parser.next();
	    }
	} catch (Exception f) {
	    System.err.println("Exception parse : " + f + " at line "
		    + parser.getLineNumber());
	}
	try {
	    consumer.endDocument();

	    // trace mode
	    if (trace)
		System.err.println("Document end reached!");
	} catch (Exception e) {
	    System.err.println("Exception END_DOCUMENT : " + e + " at line "
		    + parser.getLineNumber());
	}
    }

    /**
     * Enable debug mode
     * 
     * @param debug
     *            boolean
     */
    public void enableDebug(boolean debug) {
	trace = debug;
    }

    private void jbInit() throws Exception {
    }
}
