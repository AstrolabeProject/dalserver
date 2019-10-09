package dalserver.tap;

import dalserver.*;

import java.io.*;
import java.net.*;
import java.text.MessageFormat;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;

/**
 * Servlet fetches asynchronous TAP results from the DALserver.
 * The current ResultStore implementation just writes the results to a local VOTable file.
 * So, this servlet just loads them from the local disk and returns them.
 *
 *   @version	1.0, 10/5/2019
 *   @author	Tom Hicks
 *   Last Modified: Successfully fetch result file. Rename resultDir/resultUrl config params.
 *                  Add more checking. Use MessageFormat for messages. Cleanups.
 */
public class ResultFetchServlet extends HttpServlet {
  private static final Logger log = Logger.getLogger(ResultFetchServlet.class);

  /** Directory where result files will be stored. */
  private static final String RESULT_DIR_KEY = "resultDir";

  /** Fileserver URL for retrieval of stored result files. */
  private static final String RESULT_URL_KEY = "resultUrl";

  /** Name of servlet parameter whose value is the result file to fetch from disk. */
  private static final String RESULT_PARAM = "result";

  /** Size of file copy buffers. */
  private static final int BUFSIZE = 8192;

  /** The disk location where result files have been stored. */
  private String resultDir = null;

  /** The base URL for calling this servlet. */
  private String resultUrl = null;

  /** The DALServer runtime context for a request. */
  protected DalContext dalContext = null;


  /** Servlet startup and initialization. */
  public void init (ServletConfig config) throws ServletException {
    super.init(config);
  }

  /** Servlet shutdown. */
  public void destroy () { }


  /** Return a brief description of the service.  */
  public String getServletInfo () {
    return ("Fetch Asynchronous TAP results previously stored by the DALServer");
  }


  /** Handle a GET request. */
  public void doGet (HttpServletRequest servletRequest, HttpServletResponse servletResponse)
    throws IOException, ServletException
  {
    boolean error = false;
    String operation = null;
    ParamSet params = new ParamSet();
    RequestResponse response = new RequestResponse();

    try {
      ServletContext servletContext = getServletContext();
      ServletConfig servletConfig = getServletConfig();

      this.dalContext = new DalContext((ParamSet)params, response,
                                       servletRequest, servletContext, servletConfig);

      // get the ResultStore storage management parameters
      this.resultDir = params.getValue(RESULT_DIR_KEY);
      this.resultUrl = params.getValue(RESULT_URL_KEY);

      if (resultDir == null || resultUrl == null) {
        String errMsg = MessageFormat.format(
          "(ResultFetchServlet): Configuration missing values for {0} ({1}) and/or {2} ({3})",
          RESULT_DIR_KEY, resultDir, RESULT_URL_KEY, resultUrl);
        log.error(errMsg);
        throw new DalServerException(errMsg);
      }

      String resultFilename = servletRequest.getParameter(RESULT_PARAM);
      if (resultFilename == null) {
        String errMsg = MessageFormat.format(
          "Missing request parameter {0}, specifying which result file to fetch.", RESULT_PARAM);
        throw new DalServerException(errMsg);
      }

      // try to find the specified result file on the disk
      File resultFile = getResultFile(resultFilename);

      // set servlet response parameters
      servletResponse.setContentType("text/xml;x-votable");

      // stream the file back to the caller
      InputStream inStream = new FileInputStream(resultFile);
      if (inStream != null) {
        ServletOutputStream outStream = servletResponse.getOutputStream();
        byte[] buffer = new byte[BUFSIZE];
        int bytesRead = -1;
        while ((bytesRead = inStream.read(buffer)) > 0) {
          outStream.write(buffer, 0, bytesRead);
        }
        inStream.close();
        outStream.close();
      }
    }
    catch (DalServerException dsx) {
      errorResponse(params, servletResponse, dsx);
    }
  }


  /** Handle a POST request. This just hands off to GET. */
  public void doPost (HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    doGet(request, response);               // all requests handled by GET
  }


  // ---------- Private Methods -------------------

  /** Get a File instance for the result file to be returned. */
  private File getResultFile (String filename) throws DalServerException {
    File dir = new File(resultDir);
    if ((dir == null) || !dir.exists() || !dir.isDirectory() || !dir.canRead()) {
      String errMsg = MessageFormat.format(
        "(ResultFetchServlet): Unable to find or read the configured results directory {0}", resultDir);
      throw new DalServerException(errMsg);
    }

    File resultFile = new File(dir, filename);
    if ( (resultFile == null) || !resultFile.exists() ||
         !resultFile.isFile() || !resultFile.canRead() ) {
      String errMsg = MessageFormat.format(
        "(ResultFetchServlet): Unable to find or read the specified results file {0} from directory {1}", filename, resultDir);
      throw new DalServerException(errMsg);
    }

    return resultFile;
  }


  /**
   * Handle an exception, returning an error response to the client.
   * This version returns the error in a VOTable. If any further errors occur while
   * returning the error response, a <tt>ServletException</tt> is returned instead.
   *
   * @param	params		The input service parameter set.
   *
   * @param	servletResponse	Servlet response channel, which will be
   *				reset to ensure that the output stream is
   *				correctly setup for the error response.
   *
   * @param	ex		The exception which triggered this error response.
   */
  @SuppressWarnings("unchecked")
  private boolean errorResponse (ParamSet params,
                                 HttpServletResponse servletResponse, Exception ex)
    throws ServletException
  {
    ServletOutputStream outStream = null;
    RequestResponse reqResp = null;
    TableInfo tableInfo = null;

    try {
      String key = "QUERY_STATUS";
      // Set up a DALserver request-response object with QUERY_STATUS=ERROR. */
      reqResp = new RequestResponse();
      reqResp.setType("results");
      tableInfo = new TableInfo(key, "ERROR");
      if (ex.getMessage() != null)
        tableInfo.setContent(ex.getMessage());
      reqResp.addInfo(key, tableInfo);
      reqResp.echoParamInfos(params);

      // set up the output stream
      servletResponse.resetBuffer();
      servletResponse.setContentType("text/xml;x-votable");
      servletResponse.setBufferSize(BUFSIZE);
      outStream = servletResponse.getOutputStream();

      // write the error VOTable using information in the request-response object
      reqResp.writeVOTable((OutputStream) outStream);
    }
    catch (Exception ex2) {
      throw new ServletException(ex2);
    }
    finally {
      if (outStream != null)
        try {
          outStream.close();
        } catch (IOException iox) {
          throw new ServletException(iox);
        }
      reqResp = null;
      tableInfo = null;
    }

    return true;                            // signal that the error was handled
  }

}
