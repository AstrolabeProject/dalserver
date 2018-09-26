/*
 * TapParamSet.java
 * $ID*
 */

package dalserver.tap;

import dalserver.*;
import java.io.*;
import java.util.*;

/**
 * Construct an initial default parameter set containing the parameters
 * for Table Access Protocol (TAP) service.  A list of the currently defined
 * parameters, including their name, type, and description, can be found
 * in <a href="doc-files/tap-params.html">this table</a>.
 *
 * This TAP implemention extends the standard to support output to a server
 * DBMS table (given sufficient permission).  If the OUTPUT parameter has a
 * value it is taken to be the name of the output table to be created to
 * store the query response table.  The service configuration determines
 * the default output destination and permissions.
 * 
 * @version	1.0, 7-Apr-2015
 * @author	Doug Tody
 */
public class TapParamSet extends ParamSet implements Iterable<Param> {

    /** Create an initial default TAP parameter set. */
    public TapParamSet() throws DalServerException {
	// Shorthand for param type and level.
	final EnumSet<ParamType> STR = EnumSet.of(ParamType.STRING);
	final EnumSet<ParamType> BOO = EnumSet.of(ParamType.BOOLEAN);
	final EnumSet<ParamType> INT = EnumSet.of(ParamType.INTEGER);
	final EnumSet<ParamType> FLO = EnumSet.of(ParamType.FLOAT);
	final EnumSet<ParamType> ISO = EnumSet.of(ParamType.ISODATE);
	final EnumSet<ParamType> RFO = EnumSet.of(ParamType.FLOAT, ParamType.RANGELIST, ParamType.ORDERED);
	final EnumSet<ParamType> RFU = EnumSet.of(ParamType.FLOAT, ParamType.RANGELIST);
	final EnumSet<ParamType> RSO = EnumSet.of(ParamType.STRING, ParamType.RANGELIST, ParamType.ORDERED);
	final EnumSet<ParamType> RSU = EnumSet.of(ParamType.STRING, ParamType.RANGELIST);
	final EnumSet<ParamType> RDO = EnumSet.of(ParamType.ISODATE, ParamType.RANGELIST, ParamType.ORDERED);
	final EnumSet<ParamType> RDU = EnumSet.of(ParamType.ISODATE, ParamType.RANGELIST);
	final EnumSet<ParamType> RLO = EnumSet.of(ParamType.RANGELIST, ParamType.ORDERED);

	// Define all core parameters defined by the TAP standard.
	// This would be more flexibly done by reading an external
	// schema, but a wired in approach is simpler for now.

	// General protocol-level parameters.
	this.addParam(new Param("VERSION",     STR, "1.0", "TAP protocol version"));
	this.addParam(new Param("REQUEST",     STR, "Operation to be performed"));

	// Parameters for the TAP query operation (implicit).
	this.addParam(new Param("QUERY",       STR, "Query expression"));
	this.addParam(new Param("LANG",        STR, "Query language"));
	this.addParam(new Param("FORMAT",      STR, "Desired output data format"));
	this.addParam(new Param("UPLOAD",      STR, "Table or tables to upload"));
	this.addParam(new Param("MAXREC",      INT, "Maximum number of output records"));
	this.addParam(new Param("TIMEOUT",     INT, "Requested query timeout (sec)"));
	this.addParam(new Param("RUNID",       STR, "Runtime job ID string"));

	// Define any service-defined extension parameters here.
	// Client-defined parameters can only be specified at runtime.

	this.addParam(new Param("OUTPUT",      STR, "Output table name"));

	// Mark these as service-defined extensions as they are not in the TAP standard.
	this.getParam("OUTPUT").setLevel(ParamLevel.EXTENSION);
    }

    // Exercise the parameter mechanism.
    public static void main (String[] args) {
	if (args.length == 0 || args[0].equals("test")) {
	    try {
		// Create a new, default TAP queryData parameter set.
		TapParamSet p = new TapParamSet();

		// Set some typical parameter values.
		p.setValue("QUERY", "select * from test");

		// Print out the edited TAP parameter set.
		System.out.println ("TAP: " + p);

	    } catch (DalServerException ex) {
		System.out.println ("DalServerException");
	    }

	} else if (args[0].equals("doc")) {
	    // Generate an HTML version of a TAP parameter set.
	    // Place the generated file into the dalserver/doc-files directory
	    // to have it included in the Javadoc.

	    String fname = "tap-params.html";
	    Object last = null;

	    try {
		// Create an TAP parameter set.
		TapParamSet tap = new TapParamSet();

		// Output the parameter set in HTML format.
		PrintWriter out = new PrintWriter(new FileWriter(fname));
		out.println("<HTML><HEAD>");
		out.println("<TITLE>TAP Parameters</TITLE>");
		out.println("</HEAD><BODY>");
		out.println("<TABLE width=700 align=center>");
		out.println("<TR><TD " +
		    "colspan=3 align=center bgcolor=\"LightGray\">" +
		    "TAP Parameters" + "</TD></TR>");

		for (Iterator i = tap.iterator();  i.hasNext();  ) {
		    Map.Entry me = (Map.Entry) i.next();
		    Object obj = (Object) me.getValue();
		    if (obj == last)
			continue;
		    Param o = (Param)obj;

		    out.println("<TR><TD>" + o.getName() + 
			"</TD><TD>" + o.getType() + 
			"</TD><TD>" + o.getDescription() + 
			"</TD></TR>");

		    last = obj;
		}

		out.println("</TABLE>");
		out.println("</BODY></HTML>");
		out.close();

	    } catch (DalServerException ex) {
		System.out.println ("cannot create TAP parameter set");
	    } catch (IOException ex) {
		System.out.println ("cannot write file " + fname);
	    }
	}
    }
}
