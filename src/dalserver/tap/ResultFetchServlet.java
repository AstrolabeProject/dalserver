package dalserver.tap;

import dalserver.*;

import java.io.*;
import java.net.*;
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
 *   Last Modified: First working (but hardwired) version. Add config file reading. Rename class.
 */
public class ResultFetchServlet extends HttpServlet {
  private static final Logger log = Logger.getLogger(ResultFetchServlet.class);

  /** Keys for reading values from a parameter map. */
  private static final String BASEDIR_KEY = "baseDir";
  private static final String BASEURL_KEY = "baseURL";

  /** Size of file copy buffers. */
  private static final int BUFSIZE = 8192;

  /** The default disk location where result files have been stored. */
  private String baseDir = "/vos/resultStore";

  /** The default base URL for calling this servlet. */
  private String baseURL = "http://localhost:8080/dals/resultFetch/";

  /** The DALServer runtime context for a request. */
  protected DalContext dalContext = null;

  private ParamSet params = null;


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
    // Internal data.
    boolean error = false;
    String operation = null;
    RequestResponse response = null;

    try {
      params = new ParamSet();
      response = new RequestResponse();
      ServletContext servletContext = getServletContext();
      ServletConfig servletConfig = getServletConfig();

      this.dalContext = new DalContext((ParamSet)params, response,
                                       servletRequest, servletContext, servletConfig);

      // Get the ResultStore storage management parameters.
      this.baseDir = params.getValue(BASEDIR_KEY);
      this.baseURL = params.getValue(BASEURL_KEY);

      if (baseDir == null || baseURL == null) {
        log.error(
          "(ResultFetchServlet.doGet): Configuration missing base directory and/or base URL: " +
            BASEDIR_KEY + "=" + baseDir + " " + BASEURL_KEY + "=" + baseURL);
      }
      else {
        System.err.println("(ResultFetchServlet.doGet): BASE PARAMETERS " +
                           BASEDIR_KEY + "=" + baseDir + ", " + BASEURL_KEY + "=" + baseURL);
        log.error("(ResultFetchServlet.doGet): BASE PARAMETERS " +
                  BASEDIR_KEY + "=" + baseDir + ", " + BASEURL_KEY + "=" + baseURL);
      }

      String fileToken = "ckk27pgv8byupbwi"; // TODO: get file token: hardwired for now

      // try to file the identified result file on the disk
      File resultFile = getResultFile("result_" + fileToken + ".xml");

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

    } catch (DalServerException dsx) {
      error = this.errorResponse(params, servletResponse, dsx);
    } finally {
      if (error) {
        params = null;
        return;
      }
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
    File dir = new File(baseDir);
    if ((dir == null) || !dir.exists() || !dir.isDirectory() || !dir.canRead())
      throw new DalServerException(
        "(ResultFetchServlet): Unable to find or read the configured results directory at '" +
        baseDir + "'");

    File resultFile = new File(dir, filename);
    if ((resultFile == null) || !resultFile.exists() || !resultFile.isFile() || !resultFile.canRead())
      throw new DalServerException(
        "(ResultFetchServlet): Unable to find or read the specified results file'" +
        filename + "' from directory '" + baseDir + "'");

    return resultFile;
  }


  /**
   * Handle an exception, returning an error response to the client.
   * This version return a VOTable.  If any further errors occur while
   * returning the error response, a servlet-level error is returned
   * instead.
   *
   * @param	params		The input service parameter set.
   *
   * @param	response	Servlet response channel.  This will be
   *				reset to ensure that the output stream is
   *				correctly setup for the error response.
   *
   * @param	ex		The exception which triggered the error
   *				response.
   */
  @SuppressWarnings("unchecked")
  private boolean errorResponse (ParamSet params,
                                 HttpServletResponse servletResponse, Exception ex)
    throws ServletException
  {
    boolean error = true;
    ServletOutputStream out = null;
    RequestResponse r = null;
    TableInfo info = null;

    try {
      // Set up a response object with QUERY_STATUS=ERROR. */
      r = new RequestResponse();
      r.setType("results");

      String id, key = "QUERY_STATUS";
      info = new TableInfo(key, "ERROR");
      if (ex.getMessage() != null)
        info.setContent(ex.getMessage());
      r.addInfo(key, info);
      r.echoParamInfos(params);

      // Set up the output stream.
      servletResponse.resetBuffer();
      servletResponse.setContentType("text/xml;x-votable");
      servletResponse.setBufferSize(BUFSIZE);
      out = servletResponse.getOutputStream();

      // Write the output VOTable.
      r.writeVOTable((OutputStream)out);

    } catch (Exception ex1) {
      throw new ServletException(ex1);

    } finally {
      if (out != null)
        try {
          out.close();
        } catch (IOException ex2) {
          throw new ServletException(ex2);
        }
      if (r != null)
        r = null;
      if (info != null)
        info = null;
    }

    return (error);
  }

}
