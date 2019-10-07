package dalserver.tap;

import dalserver.*;
import ca.nrc.cadc.tap.schema.ColumnDesc;
import ca.nrc.cadc.tap.schema.TableDesc;
import ca.nrc.cadc.tap.upload.datatype.ADQLDataType;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.apache.commons.cli.*;
import java.util.*;

/**
 * This class provides methods to create and populate a TAP_SCHEMA instance.
 * An input table or set of tables are read, automatically populating the
 * contents of the TAP_SCHEMA tables.  A command line application for
 * generating the TAP_SHEMA is also provided.
 *
 * Note, this is different from the OpenCADC TapSchema classes, that are
 * used to read the TAP schema at runtime to execute a query.
 *
 *   @author DTody
 *   Last Modified: Add debug logging. -TRH
 */
public class TapSchema {

    private static final Logger log = Logger.getLogger(TapSchema.class);
    private DataSource dataSource = null;

    // Define the standard TAP_SCHEMA schema and tables.
    private final static String TAP_SCHEMA = "tap_schema";
    private final static String SCHEMAS_TABLE = "schemas";
    private final static String TABLES_TABLE = "tables";
    private final static String COLUMNS_TABLE = "columns";
    private final static String KEYS_TABLE = "keys";
    private final static String KEY_COLUMNS_TABLE = "key_columns";

    private static ArrayList<String> tapSchemaTables;
    static {
	tapSchemaTables = new ArrayList<String>();
	tapSchemaTables.add(SCHEMAS_TABLE);
	tapSchemaTables.add(TABLES_TABLE);
	tapSchemaTables.add(COLUMNS_TABLE);
	tapSchemaTables.add(KEYS_TABLE);
	tapSchemaTables.add(KEY_COLUMNS_TABLE);
    }

    /** The DBMS schema to contain the TAP_SCHEMA tables. */
    private String tapSchemaName = TAP_SCHEMA;

    /** No-arg constructor.  */
    public void TapSchema() { }

    /**
     * Set the DataSource used both to read data tables and to create and
     * populate TAP_SCHEMA tables.
     *
     * @param ds		The DataSource to be used
     */
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    /**
     * Set the name of the database schema to be used for the TAP_SCHEMA
     * tables.  The default is the VO standard value "tap_schema", however
     * other non-standad schema names may be used.
     *
     * @param tapSchemaName	The name of the schema to be used
     */
    public void setTapSchema(String tapSchemaName) {
        this.tapSchemaName = tapSchemaName;
    }

    /**
     * Create a new, empty TAP_SCHEMA within the currently configured
     * DataSource.
     */
    public void createTapSchema() {
	DbmsTable dbms = new DbmsTable();
	dbms.setDataSource(this.dataSource);

	for (String tableName : tapSchemaTables) {
	    TableDesc td = new TableDesc(tapSchemaName, tableName, null, null);
	    ArrayList<ColumnDesc> cols = new ArrayList<ColumnDesc>();
	    ColumnDesc col;

	    if (tableName.equals(SCHEMAS_TABLE)) {
		td.setDescription("Schemas");
		col = new ColumnDesc(SCHEMAS_TABLE, "schema_name", "Schema name", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(SCHEMAS_TABLE, "description", "Schema description", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(SCHEMAS_TABLE, "utype", "UType", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);

	    } else if (tableName.equals(TABLES_TABLE)) {
		td.setDescription("Tables");
		col = new ColumnDesc(TABLES_TABLE, "schema_name", "Schema name", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(TABLES_TABLE, "table_name", "Table name", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(TABLES_TABLE, "table_type", "Table type", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(TABLES_TABLE, "description", "Table description", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(TABLES_TABLE, "utype", "UType", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);

	    } else if (tableName.equals(COLUMNS_TABLE)) {
		td.setDescription("Columns");
		col = new ColumnDesc(COLUMNS_TABLE, "table_name", "Table name", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(COLUMNS_TABLE, "column_name", "Column name", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(COLUMNS_TABLE, "description", "Column description", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(COLUMNS_TABLE, "unit", "Unit", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(COLUMNS_TABLE, "ucd", "UCD", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(COLUMNS_TABLE, "utype", "UType", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(COLUMNS_TABLE, "datatype", "Datatype", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(COLUMNS_TABLE, "size", "Size", null, null, null,
		    ADQLDataType.ADQL_INTEGER, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(COLUMNS_TABLE, "principal", "Principal Column", null, null, null,
		    ADQLDataType.ADQL_INTEGER, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(COLUMNS_TABLE, "indexed", "Column is indexed", null, null, null,
		    ADQLDataType.ADQL_INTEGER, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(COLUMNS_TABLE, "std", "Standard column", null, null, null,
		    ADQLDataType.ADQL_INTEGER, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(COLUMNS_TABLE, "id", "XML ID for VOTable", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, false, false, false);
		cols.add(col);

	    } else if (tableName.equals(KEYS_TABLE)) {
		td.setDescription("Keys");
		col = new ColumnDesc(KEYS_TABLE, "key_id", "Key identifier", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(KEYS_TABLE, "from_table", "Table where key is defined", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(KEYS_TABLE, "target_table", "Target table", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(KEYS_TABLE, "description", "Description", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(KEYS_TABLE, "utype", "UType", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);

	    } else if (tableName.equals(KEY_COLUMNS_TABLE)) {
		td.setDescription("Key Columns");
		col = new ColumnDesc(KEY_COLUMNS_TABLE, "key_id", "Key identifier", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(KEY_COLUMNS_TABLE, "from_column", "Column name in from_table", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
		col = new ColumnDesc(KEY_COLUMNS_TABLE, "target_column", "Column name in target_table", null, null, null,
		    ADQLDataType.ADQL_VARCHAR, null, true, false, true);
		cols.add(col);
	    }

	    td.setColumnDescs(cols);
	    dbms.createTable(tapSchemaName, tableName, td);
	}
    }

    /**
     * Add a list of tables into the active TAP_SCHEMA.
     *
     * @param	schemaName	The schema containing the tables
     * @param	tableSet	The list of tables to be added
     *
     * The given list of tables are added to the currently active
     * TAP_SCHEMA.  The listed tables must be present in the schema
     * schemaName.  Tables in multiple different schemas may be
     * added by making multiple calls.  The TAP schema must already
     * have been created, but need not be empty.
     */
    public void addTables(String schemaName, String tableSet[])
	throws SQLException {
        log.debug("(addTables): schemaName: " + schemaName);

	for (String tableName : tableSet) {
            log.debug("(addTables): tableName: " + tableName);
	    addTable(schemaName, tableName);
	}
    }

    /**
     * Add a single table into the active TAP_SCHEMA.
     *
     * @param	schemaName	The schema containing the tables
     * @param	tableName	The name of the table to be added
     *
     * The given table is added to the currently active TAP_SCHEMA.
     * The table to be added may belong to any schema (including the
     * TAP_SCHEMA itself) accessible via the currently active DataSource.
     * The TAP schema must already have been created, but need not be empty.
     */
    public void addTable(String schemaName, String tableName)
	throws SQLException {

	addTable(schemaName, tableName, null, null);
    }

    /**
     * Add a single table into the active TAP_SCHEMA.
     *
     * @param	schemaName	The schema containing the tables
     * @param	tableName	The name of the table to be added
     * @param	desc	        A brief description of the table
     * @param	utype		The VO Utype for the table
     *
     * The given table is added to the currently active TAP_SCHEMA.
     * The table to be added may belong to any schema (including the
     * TAP_SCHEMA itself) accessible via the currently active DataSource.
     * The TAP schema must already have been created, but need not be empty.
     *
     * A table may be updated after being added by repeating the addTable
     * (or addTableSet).  This will add any new columns that have been added
     * to the table since it was originally added to the TAP_SCHEMA.  Metadata
     * for existing columns however, is not modified, to avoid losing any
     * custom edits made externally.  In general, the TAP_SCHEMA tables may
     * be manually edited to add additional schema metadata, without losing
     * these edits if new tables or table columns are added to the schema.
     */
    public void addTable(String schemaName, String tableName,
	String desc, String utype) throws SQLException {

	// Get the active database context.
	DbmsTable dbms = new DbmsTable();
	dbms.setDataSource(this.dataSource);
	ArrayList<ArrayList<Object>> rows = null;
	ArrayList<Object> row;
	ResultSet rs;

	// Get a table descriptor for the referenced table.
	TableDesc tab = dbms.getTableDesc(schemaName, tableName, desc, utype);
	String dbTableName = dbms.getDbTableName(schemaName, tableName);
	String where;
	int count;

	// Add schemaName to TAP_SCHEMA.schemas if absent.
	where = "schema_name = '" + schemaName + "'";
	count = dbms.queryRows(tapSchemaName, SCHEMAS_TABLE, where);
	if (count == 0) {
	    rows = new ArrayList<ArrayList<Object>>();
	    row = new ArrayList<Object>();

	    row.add(schemaName);	// schema_name
	    row.add(null);		// description
	    row.add(null);		// utype
	    rows.add(row);

	    dbms.insertData(tapSchemaName, SCHEMAS_TABLE, rows);
	}

	// Add tableName to TAP_SCHEMA.tables if absent.
	where =
	    "schema_name = '" + schemaName + "' and " +
	    "table_name = '" + dbTableName + "'";
	count = dbms.queryRows(tapSchemaName, TABLES_TABLE, where);
	if (count == 0) {
	    rows = new ArrayList<ArrayList<Object>>();
	    row = new ArrayList<Object>();

	    row.add(schemaName);	// schema_name
	    row.add(dbTableName);	// table_name
	    row.add("table");		// table_type
	    row.add(null);		// description
	    row.add(null);		// utype
	    rows.add(row);		// table_name

	    dbms.insertData(tapSchemaName, TABLES_TABLE, rows);
	}

	// Add each column in the table to TAP_SCHEMA.columns if absent.
	// if the column is already present in the schema it is left as-is,
	// ensuring that any custom edits (eg to add VO or other metadata) are
	// preserved.  When a column is initially entered it does not have
	// anything other than JDBC metadata (no VO metadata) hence those
	// attributes are nulled out below.  They can be manually added later.

	rows = new ArrayList<ArrayList<Object>>();

	for (ColumnDesc col : tab.getColumnDescs()) {
	    // String tabName = col.getTableName();
	    String tabName = dbTableName;	// Use fully qualified table name
	    String colName = col.getColumnName();
	    String datatype = col.getDatatype();
	    int size = col.getSize();

	    where = "table_name = '" + tabName + "' and " +
		"column_name = '" + colName + "'";
	    count = dbms.queryRows(tapSchemaName, COLUMNS_TABLE, where);
	    if (count == 0) {
		row = new ArrayList<Object>();

		row.add(tabName);	// table_name
		row.add(colName);	// column_name
		row.add(null);		// description
		row.add(null);		// unit
		row.add(null);		// ucd
		row.add(null);		// utype
		row.add(datatype);	// datatype
		row.add(size);		// size
		row.add(1);		// principal
		row.add(0);		// indexed
		row.add(1);		// std
		row.add(null);		// id

		rows.add(row);
	    }
	}

	// Insert any new columns into TAP_SCHEMA.columns.
	if (rows != null && rows.size() > 0)
	    dbms.insertData(tapSchemaName, COLUMNS_TABLE, rows);

	// TAP_SCHEMA.keys is created, but will require manual editing.
	// TAP_SCHEMA.key_columns is created, but will require manual editing.
    }


    /**
     * TAP_SCHEMA administration utility.  Run this from the Java command line
     * to create a new TAP_SCHEMA and add or update the metadata for tables
     * described in the schema.
     *
     *   java TapSchema
     *	  -d			The DataSource to be used
     *	  -c			Create a new empty TAP_SCHEMA
     *	  -s			Schema containing the data tables
     *	  -a table 		Add a table to the TAP_SCHEMA
     *	  -t table [table...] 	Add a tableSet to the TAP_SCHEMA
     *
     * All reference tables must be present in the given schema, but multiple
     * invocations may be made to index tables from multiple schemas.  If a
     * table is already present in the TAP schema, any new columns will be
     * found and added to the schema.
     *
     * A DataSource is used to connect to the DBMS.  The DataSource can be
     * obtained via JNDI lookup (when running a context such as Tomcat) or
     * can be created from JDBC connection parameters read from a file (when
     * running Java from the command line).  Which type of DataSource to use
     * is determined by the syntax of the "datasource" parameter.  For a
     * JNDI lookup merely specify the JNDI name of the object, e.g.,
     * "jdbc/tapuser".  To dynamically create a DataSource from connection
     * parameters stored in a Java properties file, use {@literal file:<path>}
     */
    public static void main(String args[]) {

	final String DEF_DATASOURCE = "jdbc/tapadmin";
	TapSchema ts = new TapSchema();
        log.debug("(main): DEF_DATASOURCE: " + DEF_DATASOURCE);

	// Create the command line parser.
	CommandLineParser parser = new DefaultParser();
	CommandLine cli = null;

	// Create the Options.
	Options options = new Options();
	options.addOption(Option.builder()
	    .longOpt("help")
	    .desc("Print help")
	    .build());
	options.addOption(Option.builder("a")
	    .longOpt("add")
	    .desc("Add a table or tables to the TAP schema")
	    .hasArgs()
	    .argName("tables")
	    .build());
	options.addOption( "c", "create", false, "Create a new empty TAP_SCHEMA");
	options.addOption( "d", "datasource", true, "The DataSource to be used");
	options.addOption( "s", "schema", true, "Schema containing the data tables");

	// Parse the command line arguments.
	try {
	    cli = parser.parse(options, args);
	} catch(ParseException ex) {
	    System.out.println("Argument error:" + ex.getMessage());
	    System.exit(1);
	}

	if (cli.hasOption("help")) {
	    String header = "Create or update a TAP_SCHEMA";
	    String footer = null;
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp("TapSchema", header, options, footer, true);
	    System.exit(0);
	}

	// Set the DataSource to be used.
	try {
	    String dsName = cli.getOptionValue("datasource", DEF_DATASOURCE);
            log.debug("(main): dsName: " + dsName);
	    DbmsDataSourceFactory dsFactory = new DbmsDataSourceFactory();
	    DataSource ds = dsFactory.getDataSource(dsName);
	    ts.setDataSource(ds);
	} catch (DalServerException ex) {
	    System.out.println("Cannot get DataSource:" + ex.getMessage());
	    System.exit(2);
	}

	try {
	    // Create a new TAP_SCHEMA.
	    if (cli.hasOption("create"))
		ts.createTapSchema();

	    // Get the name of the schema containing the data tables.
	    String schemaName = cli.getOptionValue("schema");
            log.debug("(main): schemaName: " + schemaName);

	    // Add one or more tables to the Tap schema.
	    String tables[] = cli.getOptionValues("add");
	    if (tables != null) {
		if (schemaName == null) {
		    System.out.println("A valid schema name must be specified");
		    System.exit(3);
		}
		ts.addTables(schemaName, tables);
	    }

	} catch (SQLException ex) {
	    System.out.println("Database error:" + ex.getMessage());
	    System.exit(4);
	}
    }
}
