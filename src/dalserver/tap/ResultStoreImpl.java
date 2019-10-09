package dalserver.tap;

import dalserver.*;

import ca.nrc.cadc.dali.tables.TableWriter;
import ca.nrc.cadc.dali.tables.votable.VOTableWriter;
import ca.nrc.cadc.tap.ResultStore;
import ca.nrc.cadc.uws.Job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Basic ResultStore implementation usable by the cadcTAP QueryRunner for async
 * result storage. This implementation simply writes files to a local directory
 * and then returns a URL to that file. The filesystem-to-URL mapping is
 * configurable via two parameters passed via the service configuration.
 *
 * <ul>
 * <li>baseStorageDir=/path/to/storage               // directory to store result files
 * <li>resultUrl=http://hostname/path/to/storage/{0}   // URL to fetch stored result file
 * </ul>
 *
 *   @author DTody (based upon the OpenCADC version by pdowler)
 *   Last Modified: Rename resultDir/resultUrl config params. Redo getDestfile method.
 *                  Redo getURL method to allow filename substitution in resultUrl string.
 */
public class ResultStoreImpl implements ResultStore {
    private static final Logger log = Logger.getLogger(ResultStoreImpl.class);

    /** Directory where result files will be stored. */
    private static final String RESULT_DIR_KEY = "resultDir";

    /** Fileserver URL for retrieval of stored result files. */
    private static final String RESULT_URL_KEY = "resultUrl";

    private Job job;
    private DalContext dalContext;
    private ParamSet params;
    private String contentType;
    private String filename;
    private String resultDir;
    private String resultUrl;

    /** No-arg constructor. */
    public ResultStoreImpl() { }

    /**
     * Store a query resultSet formatted as a file by the given TableWriter.
     *
     * @param	rs		Query JDBC resultSet
     * @param	writer		The TableWriter instance to be used.
     */
    public URL put(ResultSet rs, TableWriter<ResultSet> writer)
        throws IOException {

        return (put(rs, writer, null));
    }

    /**
     * Store at most maxRows of a query resultSet, formatted as a file by the
     * given TableWriter.
     *
     * @param	rs		Query JDBC resultSet
     * @param	writer		The TableWriter instance to be used.
     * @param	maxRows		The maximum number of rows to be output
     */
    public URL put(ResultSet rs, TableWriter<ResultSet> writer, Integer maxRows)
        throws IOException {

        Long num = null;
        if (maxRows != null)
            num = new Long(maxRows.intValue());

        File dest = getDestFile(filename);
        URL ret = getURL(filename);
        FileOutputStream ostream = null;
        try {
            ostream = new FileOutputStream(dest);
            writer.write(rs, ostream, num);
        } finally {
            if (ostream != null)
                ostream.close();
        }
        return (ret);
    }

    /**
     * Format an output an error result as a VOTable.
     *
     * @param	t		Error condition to be output
     * @param	writer		VOTable writer
     */
    public URL put(Throwable t, VOTableWriter writer)
	throws IOException {

        File dest = getDestFile(filename);
        URL ret = getURL(filename);
        FileOutputStream ostream = null;
        try {
            ostream = new FileOutputStream(dest);
            writer.write(t, ostream);
        } finally {
            if (ostream != null)
                ostream.close();
        }

        return (ret);
    }


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
    public void setJob (Job job) throws RuntimeException {
        this.job = job;

	// Get the DalServer context.
	this.dalContext = (DalContext) job.appData;
	this.params = dalContext.pset;

	// Get the ResultStore storage management parameters.
	this.resultDir = params.getValue(RESULT_DIR_KEY);
	this.resultUrl = params.getValue(RESULT_URL_KEY);

        if (resultDir == null || resultUrl == null) {
            String errMsg = MessageFormat.format(
                "(ResultStoreImpl): Configuration missing values for {0} ({1}) and/or {2} ({3})",
                RESULT_DIR_KEY, resultDir, RESULT_URL_KEY, resultUrl);
          log.error(errMsg);
          throw new RuntimeException(errMsg);
        }
    }


    /**
     * Set the HTTP contentType for the file to be created.
     *
     * @param	contentType	HTTP contentType value
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Set the base filename for the file to be created.
     *
     * @param	filename	The base filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }


    /**
     * Get a File instance for the file to be written to the configured base directory.
     *
     * @param	filename	the name of the file to be written within the base directory.
     */
    private File getDestFile (String filename) {
      File dir = new File(resultDir);
      if ((dir == null) || !dir.exists() || !dir.isDirectory() || !dir.canRead()) {
        String errMsg = MessageFormat.format(
          "(ResultStoreImpl.getDestFile): Configured base directory {0} does not exist or cannot be written to.", resultDir);
        throw new RuntimeException(errMsg);
      }
      return new File(dir, filename);
    }


    /**
     * Return a URL reference to the given file using the configured base URL. The
     * base URL must have a <tt>java.text.MessageFormat</tt> (zero-index) placeholder
     * somewhere within the URL. For examples:
     *
     * <ul>
     *   <li> http://localhost:8080/dalserver/fetchResult/{0}
     *   <li> http://localhost:8080/dalserver/fetchResult?result={0}
     * </ul>
     *
     * @param	filename	The name of the saved file whose URL is desired.
     */
    private URL getURL (String filename) {
      String fileURL = MessageFormat.format(resultUrl, filename);
      try {
        return (new URL(fileURL));
      } catch(MalformedURLException ex) {
        String errMsg = MessageFormat.format(
          "Unable to create URL from configured resultUrl {0} and given filename {1}",
          resultUrl, filename);
        throw new RuntimeException(errMsg, ex);
      }
    }

}
