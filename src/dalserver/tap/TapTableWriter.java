/*
 * TapTableWriter.java
 * $ID*
 */
package dalserver.tap;

import dalserver.*;
import ca.nrc.cadc.dali.tables.TableData;
import ca.nrc.cadc.dali.tables.ascii.AsciiTableWriter;
import ca.nrc.cadc.dali.tables.votable.VOTableDocument;
import ca.nrc.cadc.dali.tables.votable.VOTableField;
import ca.nrc.cadc.dali.tables.votable.VOTableInfo;
import ca.nrc.cadc.dali.tables.votable.VOTableParam;
import ca.nrc.cadc.dali.tables.votable.VOTableReader;
import ca.nrc.cadc.dali.tables.votable.VOTableResource;
import ca.nrc.cadc.dali.tables.votable.VOTableTable;
import ca.nrc.cadc.dali.tables.votable.VOTableWriter;
import ca.nrc.cadc.dali.util.Format;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.tap.schema.ParamDesc;
import ca.nrc.cadc.tap.writer.ResultSetTableData;
import ca.nrc.cadc.tap.writer.format.FormatFactory;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.ParameterUtil;

import org.apache.log4j.Logger;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.TreeMap;
import javax.sql.DataSource;
import java.sql.*;

/**
 * DALServer implementation of the OpenCADC/TAP TableWriter interface.  This
 * implementation uses the DALServer parameter mechanism for configuration
 * parameters, and the DALServer RequestResponse, serializer, and table
 * configuration mechanisms for table output.
 *
 * @author DTody  8-Apr-2015 (based in part on the OpenCADC version)
 */
public class TapTableWriter implements ca.nrc.cadc.tap.TableWriter {

    private static final Logger log = Logger.getLogger(TapTableWriter.class);
    private static final String FORMAT = "FORMAT";
    private static final String OUTPUT = "OUTPUT";

    // Datasource used for create/RW access by the client.
    private static final String USER_DATASOURCE = "userDataSource";

    // Datasource used to add user tables to the TAP_SCHEMA.
    private static final String ADMIN_DATASOURCE = "adminDataSource";

    // Constants
    public static final String CSV = "csv";
    public static final String FITS = "fits";
    public static final String HTML = "html";
    public static final String ASCII = "ascii";
    public static final String TEXT = "text";
    public static final String TSV = "tsv";
    public static final String VOTABLE = "votable";
    public static final String RSS = "rss";

    // Content-type signatures for HTTP output.
    private static final String APPLICATION_FITS = "application/fits";
    private static final String APPLICATION_VOTABLE_XML = "application/x-votable+xml";
    private static final String APPLICATION_RSS = "application/rss+xml";
    private static final String TEXT_XML_VOTABLE = "text/xml;content=x-votable"; // the SIAv1 mimetype
    private static final String TEXT_CSV = "text/csv";
    private static final String TEXT_HTML = "text/html";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String TEXT_TAB_SEPARATED_VALUES = "text/tab-separated-values";
    private static final String TEXT_XML = "text/xml";

    private static final Map<String,String> knownFormats = new TreeMap<String,String>();
    static {
        knownFormats.put(APPLICATION_VOTABLE_XML, VOTABLE);
        knownFormats.put(TEXT_XML, VOTABLE);
        knownFormats.put(TEXT_XML_VOTABLE, VOTABLE);
        knownFormats.put(TEXT_CSV, CSV);
        knownFormats.put(TEXT_TAB_SEPARATED_VALUES, TSV);
        knownFormats.put(VOTABLE, VOTABLE);
        knownFormats.put(CSV, CSV);
        knownFormats.put(TSV, TSV);
        knownFormats.put(TEXT, TEXT);
        knownFormats.put(ASCII, ASCII);
        knownFormats.put(TEXT_HTML, HTML);
        knownFormats.put(TEXT_PLAIN, TEXT);
        knownFormats.put(TEXT_PLAIN, ASCII);
        knownFormats.put(APPLICATION_FITS, FITS);
        //knownFormats.put(RSS, RSS);
        //knownFormats.put(APPLICATION_RSS, RSS);
    }

    // Class instance variables.
    private Job job;				// UWS Job descriptor
    private DalContext dalContext;		// DALServer context
    private ParamSet params;			// TAP parameter set
    private RequestResponse response;		// Output table object
    private String queryInfo;			// Not currently used
    private String contentType;			// ContentType for HTTP
    private String extension;			// File extension for output

    // A formatFactory returns a Format instance given an ADQL or VOTable
    // datatype.  Each Format object contains methods to either parse or
    // format (as a string) a data value.

    private FormatFactory formatFactory;

    // A List of table output fields deriving from the SELECT clause of the
    // query.  Each field is described by a parameter description instance
    // (ParamDesc) containing the VOTable metadata for the field.

    private List<ParamDesc> selectList;

    // We don't actually use the OpenCADC DALI tableWriters, but need to
    // match their signature for compatibility.
 
    private ca.nrc.cadc.dali.tables.TableWriter<VOTableDocument> tableWriter;


    /** No-arg constructor. */
    public void TapTableWriter() { }

    /**
     * Set a reference to the UWS Job context.
     *
     * @param	job		Job instance
     *
     * The Job context is the primary mechanism for communicating context
     * information to TAP class code.  In particular, a reference to the
     * DalContext for the query and service instance is stored in the Job
     * descriptor, making it possible for DALServer TAP plugins to retrieve
     * the DALServer context.
     */
    public void setJob(Job job) {
        this.job = job;
	this.dalContext = (DalContext) job.appData;
	this.params = dalContext.pset;
	this.response = dalContext.response;

	// *** MJF 092915
	// Enforce the MAXREC size in the response.
	// *** MJF 092915
	int maxrec = Integer.parseInt(this.params.getValue("maxrec"));
        this.response.setMaxrec(maxrec);

        initFormat();
    }

    /**
     * Set the list of output fields defined by the SELECT clause of the
     * executed ADQL query (or equivalent for other query languages).
     *
     * @param	selectList	A List of ParamDesc instances
     */
    public void setSelectList(List<ParamDesc> selectList) {
        this.selectList = selectList;
    }
    
    /**
     * Set the queryInfo string, describing the current query.
     *
     * @param	queryInfo	Query description string.
     */
    public void setQueryInfo(String queryInfo) {
        this.queryInfo = queryInfo;
    }

    /**
     * Return the HTTP ContentType for the current query response.
     */
    @Override
    public String getContentType() {
        return contentType;
    }

    /**
     * Return the file extension for the current query response.
     */
    @Override
    public String getExtension() {
        return extension;
    }
    
    /**
     * Process the FORMAT parameter and initialize the output subsystem.
     */
    private void initFormat() {
        String format = ParameterUtil.findParameterValue(FORMAT, job.getParameterList());
        if (format == null)
            format = VOTABLE;
        
	System.out.println ("initFormat: format = " + format);
	System.out.println ("initFormat: " + knownFormats);
        String type = knownFormats.get(format.toLowerCase());
        if (type == null)
	    System.out.println ("initFormat type is NULL ");
	else
	    System.out.println ("initFormat type is " + type);
        if (type == null)
            throw new UnsupportedOperationException("unknown format: " + format);

        if (type.equals(VOTABLE) && format.equals(VOTABLE))
            format = APPLICATION_VOTABLE_XML;
        
        // Create the table writer.
        // Note: This needs to be done before the write method is called so the contentType
        // can be determined from the table writer.

	if (type.equals(VOTABLE))
	    tableWriter = new VOTableWriter(format);
	if (type.equals(CSV))
	    tableWriter = new AsciiTableWriter(AsciiTableWriter.ContentType.CSV);
	if (type.equals(TSV) || type.equals(TEXT) || type.equals(ASCII))
	    tableWriter = new AsciiTableWriter(AsciiTableWriter.ContentType.TSV);

	if (tableWriter == null) {
	    // Legal format but we don't have a table writer for it
	    throw new UnsupportedOperationException("unsupported format: " + type);
	}

	this.contentType = tableWriter.getContentType();
	this.extension = tableWriter.getExtension();
    }

    /**
     * Set the FormatFactory implementation to be used to parse or format
     * ADQL and VOTable datatypes.
     *
     * @param	formatFactory		FormatFactory implementation
     *
     * A FormatFactory, given a datatype ID as input, returns a Format
     * instance containing methods used to parse or format the given
     * datatype.
     */
    public void setFormatFactory(FormatFactory formatFactory) {
        this.formatFactory = formatFactory;
    }
    
    @Override
    public void setFormatFactory(ca.nrc.cadc.dali.util.FormatFactory formatFactory)
    {
        throw new UnsupportedOperationException("Use custom tap format factory implementation class");
    }

    /**
     * Serialize a query resultSet and stream it to the given output stream.
     *
     * @param	rs			ResultSet to be processed
     * @param	out			OutputStream instance
     */
    @Override
    public void write(ResultSet rs, OutputStream out) throws IOException {
        this.write(rs, out, null);
    }

    /**
     * Serialize a query resultSet and stream at most MAXREC records to the
     * given output stream.
     *
     * @param	rs			ResultSet to be processed
     * @param	out			OutputStream instance
     * @param	maxrec			Maximum records out
     */
    @Override
    public void write(ResultSet rs, OutputStream out, Long maxrec)
	throws IOException {

	// Write to an output DBMS table instead of the output stream?
	String output = params.getValue(OUTPUT);
	String dbTableName = null;
	if (output != null)
	    dbTableName = output;

	// Stream the table to the outputStream.
	if (dbTableName == null) {
	    try {
		// Serialize the resultSet as a VOTable instance.
		saveToVotable(rs, response);

		// Output the table in the desired format.
		String format = params.getValue(FORMAT, VOTABLE);

		if (format != null) {
		    if (format.equalsIgnoreCase(HTML)) 
			response.writeHTML(out);
		    else if (format.equalsIgnoreCase(TEXT)) 
			response.writeTsv(out);
		    else if (format.equalsIgnoreCase(ASCII)) 
			response.writeTsv(out);
		    else if (format.equalsIgnoreCase(TSV)) 
			response.writeTsv(out);
		    else if (format.equalsIgnoreCase(CSV)) 
			response.writeCsv(out);
		    else
			response.writeVOTable(out);
		}
	    } catch (DalServerException ex) {
		throw new IOException(ex.getMessage());
	    }

	    return;
	}

	// Save the resultSet to a DBMS table.
	String dsName = params.getValue(USER_DATASOURCE);
	DbmsDataSourceFactory dsFactory = new DbmsDataSourceFactory();
	DbmsTable dbms = new DbmsTable();
	DataSource ds = null;

	// Set the DataSource to be used.
	try {
	    ds = dsFactory.getDataSource(dsName);
	    if (ds == null)
		throw new DalServerException("invalid DataSource: " + dsName);
	    dbms.setDataSource(ds);
	} catch (DalServerException ex) {
	    throw new IOException(ex.getMessage());
	}

	// A fully qualified table name is required (schema.tableName).
	String schemaName, tableName;
	String names[] = dbTableName.split("\\.");

	if (names.length == 3) {
	    schemaName = names[1];
	    tableName = names[2];
	} else if (names.length == 2) {
	    schemaName = names[0];
	    tableName = names[1];
	} else {
	    throw new IOException(
	    "A valid fully-qualified output table name is required");
	}

	// Save the resultSet to a new table in the given dataSource.
	if (rs != null)
	    dbms.writeData(rs, schemaName, tableName, null, null);
	else
	    throw new IOException("Null ResultSet cannot be saved");

	// Add the new table to the TAP_SCHEMA for the given dataSource.
	// This requires that a valid TAP admin dataSource be defined for
	// the service instance.  If not, this is harmless and the new
	// table is not added to the TAP schema.

	try {
	    TapSchema ts = new TapSchema();
	    String tdsName = params.getValue(ADMIN_DATASOURCE);
	    if (tdsName != null) {
		try {
		    DataSource tds = dsFactory.getDataSource(tdsName);
		    ts.setDataSource(tds);
		} catch (DalServerException ignore) {
		    ts = null;
		}
	    }
	    if (ts != null)
		ts.addTable(schemaName, tableName);

	} catch (SQLException ex) {
	    throw new IOException(
	    "Failed to add table to TAP_SCHEMA: " + ex.getMessage());
	}

	// Write a status VOTable to the outputStream.
	RequestResponse r = response;
	String key;

	r.setDescription("DALServer TAP Query");
	r.setType("results");
	r.addInfo(key="QUERY_STATUS", new TableInfo(key, "OK"));
	r.addInfo(key="QUERY", new TableInfo(key, queryInfo));
	r.addInfo(key="OUTPUT", new TableInfo(key, dbTableName));
	r.echoParamInfos(params);

	int nrows = dbms.getRowCount();
	r.addInfo(key="TableRows",
	    new TableInfo(key, new Integer(nrows).toString()));

	// Output the response table.
	try {
	    response.writeVOTable(out);
	} catch (DalServerException ex) {
	    throw new IOException(ex.getMessage());
	}
    }

    /**
     * Serialize a query resultSet and write it to the given character stream.
     *
     * @param	rs			ResultSet to be processed
     * @param	out			Writer instance
     */
    @Override
    public void write(ResultSet rs, Writer out) throws IOException {
        this.write(rs, out, null);
    }

    /**
     * Serialize a query resultSet and write at most MAXREC records to the
     * given character stream.
     *
     * @param	rs			ResultSet to be processed
     * @param	out			Writer instance
     * @param	maxrec			Maximum number of records to be output
     *
     * If maxrec=null there is no fixed limit on the size of the query
     * response.
     */
    @Override
    public void write(ResultSet rs, Writer out, Long maxrec)
	throws IOException {

	// Output via a Writer class not supported by Savot.
	// We could add support for the non-VOTable formats, but it is not
	// clear that this is even used anywhere in OpenCADC TAP.

	throw new IOException("TableWriter: unimplemented method");

    }

    /**
     * Serialize a query resultSet as a VOTable object in memory.
     *
     * @param	rs		ResultSet to be processed
     * @param	r		RequestResponse (votable) object
     */
    public void saveToVotable(ResultSet rs, RequestResponse r)
	throws DalServerException {

        if (rs != null && log.isDebugEnabled()) {
            try {
		log.debug("resultSet column count: " +
		    rs.getMetaData().getColumnCount());
	    } catch (Exception oops) {
		log.error("failed to check resultset column count", oops);
	    }
	}

	// Set global metadata.
	r.setDescription("DALServer TAP Query");
	r.setType("results");

	// The following indicates the query executed successfully.  If an
	// exception occurs the output we generate here will never be returned
	// anyway, so OK is always appropriate here.

	String key;
	r.addInfo(key="QUERY_STATUS", new TableInfo(key, "OK"));
	r.addInfo(key="QUERY", new TableInfo(key, queryInfo));
	r.echoParamInfos(params);

	// Define the fields of the output query result table.  Verify
	// that the named positional query fields exist if specified.
	// The optional ConfigTable facility may be used to customize
	// the output if desired, by omitting specified fields, or adding
	// additional VO metadata (UCD, unit, etc.) to selected fields.
	// For cone search ConfigTable cannot be used to add fields as
	// we are limited to what is in the table being queried.
        
	ConfigTable conf = new ConfigTable(params, null);
	ArrayList<String> fields = new ArrayList<String>();
	int nFields = 0;

        // Get the formats based on the selectList.
        List<Format<Object>> formats = formatFactory.getFormats(selectList);
	int listIndex = 0;

        for (ParamDesc paramDesc : selectList) {
            VOTableField f = createVOTableField(paramDesc);
	    String colName = f.getName();
	    String groupId = null;

	    // Create a formatter for the field.
            Format<Object> format = formats.get(listIndex);
            f.setFormat(format);

	    // Add the field to the DALServer output table.
	    Integer size = f.getArraysize();
	    String s_size = (size != null) ? size.toString() : null;
	    TableField field = new TableField(colName, f.id, groupId,
		f.getDatatype(), s_size, f.unit, f.utype, f.ucd, f.description);
	    field.setXtype(f.xtype);

	    // UTYPE needs to be set here due to an issue in addField.
	    // RequestResponse, being data model oriented, indexes fields by
	    // ID (the DM shortname) and UType instead of the NAME attribute;
	    // NAME is data and can be anything.  So we need to set one of
	    // these to a non-null value here.

	    String colId = field.getId();
	    if (colId == null || colId.length() == 0)
		field.setId(colName);
	    field.setIndex(nFields++);

	    conf.addField(r, field);
	    fields.add(colName);
	}

	// Process the resultSet and add rows to the output table.
	// If table configuration is active it is applied here.
	// Note rs=null if MAXREC=0.

	if (rs != null) {
	    try {
		while (rs.next()) {
		    r.addRow();
		    for (String fieldName : fields)
			r.setValue(fieldName, rs.getString(fieldName));
		}
	    } catch (SQLException ex) {
		throw new DalServerException(ex.getMessage());
	    } catch (DalOverflowException ex) {
		// Add overflow handling here; exit loop for now.
	    }
	}

	// Show the number of table rows in the response header.
	r.addInfo(key="TableRows",
	    new TableInfo(key, new Integer(r.size()).toString()));
    }

    /**
     * Create an OpenCADC VOTableField instance from the given parameter
     * descriptor.
     *
     * @param	paramDesc		Parameter descriptor
     *
     * The OpenCADC parameter descriptor handles VOTable (xtype) and ADQL
     * types and returns VOTable metadata for a given parameter.
     */
    protected VOTableField createVOTableField(ParamDesc paramDesc) {
        if (paramDesc != null) {
            String name = getParamName(paramDesc);
            String datatype = getDatatype(paramDesc);

            VOTableField newField = new VOTableField(name, datatype);

            setSize(paramDesc, newField);

            if (paramDesc.id != null)
                newField.id = paramDesc.id; // an XML id

            if (paramDesc.columnDesc != null)
                newField.utype = paramDesc.columnDesc.utype;
            else
                newField.utype = paramDesc.utype;

            newField.ucd = paramDesc.ucd;
            newField.unit = paramDesc.unit;

            if (paramDesc.datatype != null && paramDesc.datatype.startsWith("adql:"))
                newField.xtype = paramDesc.datatype;

            newField.description = paramDesc.description;

            return newField;
        }

        return (null);
    }

    /**
     * Get the parameter name from a parameter descriptor.
     *
     * @param paramDesc			Parameter descriptor
     *
     * The alias as defined in the SELECT clause is used if provided, followed
     * by the column name if defined, else null is returned.
     */
    private String getParamName(ParamDesc paramDesc) {
        String name = paramDesc.name;
        String alias = paramDesc.alias;

        if (alias != null) {
            // Strip off double-quotes used for an alias with spaces or dots in it
            if (alias.charAt(0) == '"' && alias.charAt(alias.length()-1) == '"')
                alias = alias.substring(1, alias.length() - 1);
            return alias;
        } else if (name != null)
            return name;

        return (null);
    }

    /**
     * Get the VOTable datatype for the given parameter descriptor.
     *
     * @param paramDesc			Parameter descriptor
     *
     * The datatype as specified in the parameter descriptor may specify
     * either an ADQL or VOTable datatype, either of which is mapped to to
     * the VOTable datatype.  The ADQL datatypes may include complex types
     * such as regions.
     */
    private String getDatatype(ParamDesc paramDesc) {
        String datatype = paramDesc.datatype;

        if (datatype == null)
            return null;

        if (datatype.equals("adql:SMALLINT")) {
            return "short";
        } else if (datatype.equals("adql:INTEGER")) {
            return "int";
        } else if (datatype.equals("adql:BIGINT")) {
            return "long";
        } else if (datatype.equals("adql:REAL") || datatype.equals("adql:FLOAT")) {
            return "float";
        } else if (datatype.equals("adql:DOUBLE") ) {
            return "double";
        } else if (datatype.equals("adql:VARBINARY")) {
            return "unsignedByte";
        } else if (datatype.equals("adql:CHAR")) {
            return "char";
        } else if (datatype.equals("adql:VARCHAR")) {
            return "char";
        } else if (datatype.equals("adql:BINARY")) {
            return "unsignedByte";
        } else if (datatype.equals("adql:BLOB")) {
            return "unsignedByte";
        } else if (datatype.equals("adql:CLOB")) {
            return "char";
        } else if (datatype.equals("adql:TIMESTAMP")) {
            return "char";
        } else if (datatype.equals("adql:POINT")) {
            return "char";
        } else if (datatype.equals("adql:CIRCLE")) {
            return "char";
        } else if (datatype.equals("adql:POLYGON")) {
            return "char";
        } else if (datatype.equals("adql:REGION")) {
            return "char";
        } else if (datatype.equals("votable:double")) {
	    // here we support votable datatypes used directly in the tap_schema,
	    // which are normally only needed if the DB has arrays of primitive types
	    // as adql types cover all the other scenarios
            return "double";
        } else if (datatype.equals("votable:int")) {
            return "int";
        } else if (datatype.equals("votable:float")) {
            return "float";
        } else if (datatype.equals("votable:long")) {
            return "long";
        } else if (datatype.equals("votable:boolean")) {
            return "boolean";
        } else if (datatype.equals("votable:short")) {
            return "short";
        } else if (datatype.equals("votable:char")) {
            return "char";
        } else if (datatype.equals("votable:char*")) {
            return "char";
        }

        return (null);
    }

    /**
     * Set the size attribute of a VOTable FIELD given a parameter descriptor.
     *
     * @param paramDesc			Parameter descriptor
     * @param field			VOTableField instance
     *
     * Given either an ADQL or VOTable field specification in the parameter
     * descriptor, set the VOTable attributes (variable size true/false,
     * arraysize) for the given field.
     */
    private void setSize(ParamDesc paramDesc, VOTableField field) {
        String datatype = paramDesc.datatype;
        Integer size = paramDesc.size;

        if (datatype == null)
            return;

        if (datatype.equals("adql:VARBINARY")) {
            field.setVariableSize(true);
        } else if (datatype.equals("adql:CHAR")) {
            field.setVariableSize(true);
            if (size != null)
                field.setArraysize(size);
        } else if (datatype.equals("adql:VARCHAR")) {
            field.setVariableSize(true);
            if (size != null)
                field.setArraysize(size);
        } else if (datatype.equals("adql:BINARY")) {
            if (size == null)
                field.setVariableSize(true);
            else
                field.setArraysize(size);
        } else if (datatype.equals("adql:BLOB")) {
            if (size == null)
                field.setVariableSize(true);
            else
                field.setArraysize(size);
        } else if (datatype.equals("adql:CLOB")) {
            if (size == null)
                field.setVariableSize(true);
            else
                field.setArraysize(size);
        } else if (datatype.equals("adql:TIMESTAMP")) {
            field.setVariableSize(true);
            if (size != null)
                field.setArraysize(size);
        } else if (datatype.equals("adql:POINT")) {
            field.setVariableSize(true);
        } else if (datatype.equals("adql:CIRCLE")) {
            field.setVariableSize(true);
        } else if (datatype.equals("adql:POLYGON")) {
            field.setVariableSize(true);
        } else if (datatype.equals("adql:REGION")) {
            field.setVariableSize(true);
        } else if (datatype.equals("votable:double")) {
	    // Here we support votable datatypes used directly in the tap_schema,
	    // which are normally only needed if the DB has arrays of primitive types
	    // as adql types cover all the other scenarios
            if (size != null)
                field.setArraysize(size);
        } else if (datatype.equals("votable:int")) {
            if (size != null)
                field.setArraysize(size);
        } else if (datatype.equals("votable:float")) {
            if (size != null)
                field.setArraysize(size);
        } else if (datatype.equals("votable:long")) {
            if (size != null)
                field.setArraysize(size);
        } else if (datatype.equals("votable:boolean")) {
            if (size != null)
                field.setArraysize(size);
        } else if (datatype.equals("votable:short")) {
            if (size != null)
                field.setArraysize(size);
        } else if (datatype.equals("votable:char")) {
            if (size != null)
                field.setArraysize(size);
        } else if (datatype.equals("votable:char*")) {
            field.setVariableSize(true);
        }
    }
}
