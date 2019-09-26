package dalserver;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Servlet which runs last to initialize the DALServer; specifically
 * by calling the configuration 'reload' method to load the configuration
 * obviating the need for a manual, external HTTP call. Since this servlet
 * should NOT be reloaded (i.e., DO NOT include it in the server configuration),
 * it is a "one-shot": it starts a reload and dies, never to be reborn.
 *
 * @version	1.0, 9/25/2019
 * @author	Tom Hicks
 */
public class StartupServlet extends HttpServlet {

  /** URL to call in this web application to load the configuration. */
  private String reloadURL = "http://localhost:8080/dals/reload";

  /** Servlet startup and initialization. */
  public void init (ServletConfig config) throws ServletException {
    super.init(config);

    String purl = config.getInitParameter("reloadURL");
    if (purl != null)
      reloadURL = purl;

    URL url = null;
    try {
      url = new URL(reloadURL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setReadTimeout(10*1000);
      connection.connect();
      // should log the response code but we don't have logging in the servlet
      int returnCode = connection.getResponseCode();
    } catch (Exception ex) {
      /** ignore - nothing we can do anyway */
    }
  }

  /** Servlet shutdown. */
  public void destroy () { }

  /** Return a brief description of the service.  */
  public String getServletInfo () {
    return ("Initial loading of DALServer configuration");
  }

  /** Handle a GET request. This is a NOP. */
  public void doGet (HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    /** Can't get here: init() method calls reload which should NOT reload this servlet. */
  }

  /** Handle a POST request. This is a NOP. */
  public void doPost (HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    /** Can't get here: init() method calls reload which should NOT reload this servlet. */
  }

}
