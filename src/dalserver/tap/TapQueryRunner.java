/**
 * TapQueryRunner.java
 * $ID*;
 */

package dalserver.tap;

import dalserver.*;
import ca.nrc.cadc.dali.tables.votable.VOTableWriter;
import ca.nrc.cadc.log.WebServiceLogInfo;
import ca.nrc.cadc.tap.PluginFactory;
import ca.nrc.cadc.tap.ResultStore;
import ca.nrc.cadc.tap.TapValidator;
import ca.nrc.cadc.tap.UploadManager;
import ca.nrc.cadc.tap.MaxRecValidator;
import ca.nrc.cadc.tap.TapQuery;
import ca.nrc.cadc.tap.TableWriter;
import ca.nrc.cadc.tap.schema.ParamDesc;
import ca.nrc.cadc.tap.schema.SchemaDesc;
import ca.nrc.cadc.tap.schema.TableDesc;
import ca.nrc.cadc.tap.schema.TapSchema;
import ca.nrc.cadc.tap.schema.TapSchemaDAO;
import ca.nrc.cadc.uws.ErrorSummary;
import ca.nrc.cadc.uws.ErrorType;
import ca.nrc.cadc.uws.ExecutionPhase;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;
import ca.nrc.cadc.uws.Result;
import ca.nrc.cadc.uws.server.JobRunner;
import ca.nrc.cadc.uws.server.JobUpdater;
import ca.nrc.cadc.uws.server.SyncOutput;
import ca.nrc.cadc.uws.util.JobLogInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/**
 * UWS JobRunner for the TAP query.  This is a modified version of the generic
 * OpenCADC TAP QueryRunner class, used to establish the DALServer context
 * before executing the query.  An additional modification is to allow the
 * DataSource to be used to be configurable at the level of a service instance.
 * Specifically, this version still defaults to "jdbc/tapuser" as the query
 * DataSource name, but allows a different name to be specified as a
 * service configuration parameter (and likewise for jdbc/tapuploadadm).
 *
 * TODO: This should be refactored so that the generic TAP QueryRunner can be
 * used instead.
 * 
 * @author DTody (based upon OpenCADC version by PDowler)
 */

public class TapQueryRunner implements JobRunner {
    private static final Logger log = Logger.getLogger(TapQueryRunner.class);

    // private static final String queryDataSourceName = "jdbc/tapuser";
    // private static final String uploadDataSourceName = "jdbc/tapuploadadm";
  
    private String queryDataSourceName = "jdbc/tapuser";
    private String uploadDataSourceName = "jdbc/tapuploadadm";

    private Job job;
    private DalContext dalContext;
    private JobUpdater jobUpdater;
    private SyncOutput syncOutput;
    private WebServiceLogInfo logInfo;

    /** No-arg constructor. */
    public TapQueryRunner() { }

    @Override
    public void setJob(Job job) {
        this.job = job;

	// Create and initialize the DALServer context for the query.
	// A reference is stored in the Job descriptor so that the plugins
	// can later retrieve the context.

	try {
	    TapParamSet tapPset = new TapParamSet();
	    RequestResponse response = new RequestResponse();
	    this.dalContext = new DalContext((ParamSet)tapPset, response, job);
	    String sval;

	    // Check if any custom datasource names are defined.
	    // These parameters may be specified as servlet init params,
	    // as service instance globals (service properties file),
	    // or at a higher level, although that would defeat the point
	    // of being able to specify the datasource separately for each
	    // service instance.  Normally service instance parameters
	    // should be used.

	    if ((sval = tapPset.getValue("queryDataSource")) != null)
		queryDataSourceName = sval;
	    if ((sval = tapPset.getValue("uploadDataSource")) != null)
		uploadDataSourceName = sval;
System.out.println ("TapQueryRunner: qDS = '" + queryDataSourceName + "'");
System.out.println ("TapQueryRunner: uDS = '" + uploadDataSourceName + "'");

	    // Maximum number of output records (0 for a metadata only response).
	    // This doesn't yet fully agree with TAP MAXREC handling.
	    // Param p;
	    // if ((p = tapPset.getParam("MAXREC")) != null && p.isSet()) {
		// int maxrec = p.intValue();
		// response.setMaxrec(maxrec);
	    // }

	} catch (DalServerException ex) {
	    // Add some error handling/logging here
	}
    }

    /*
     * ---- The rest is unmodified from the native TAP version. ----
     */

    @Override
    public void setJobUpdater(JobUpdater ju)
    {
        this.jobUpdater = ju;
    }

    @Override
    public void setSyncOutput(SyncOutput so)
    {
        this.syncOutput = so;
    }

    @Override
    public void run()
    {
        logInfo = new JobLogInfo(job);
        log.info(logInfo.start());
        long start = System.currentTimeMillis();

        try
        {
            doIt();
        }
        catch(Exception ex)
        {
            log.error("unexpected exception", ex);
        }

        logInfo.setElapsedTime(System.currentTimeMillis() - start);
        log.info(logInfo.end());
    }

    private void doIt()
    {
        List<Long> tList = new ArrayList<Long>();
        List<String> sList = new ArrayList<String>();

        tList.add(System.currentTimeMillis());
        sList.add("start");

        log.debug("run: " + job.getID());
         List<Parameter> paramList = job.getParameterList();
        log.debug("job " + job.getID() + ": " + paramList.size() + " parameters");
        PluginFactory pfac = new PluginFactory(job);
        log.debug("loaded: " + pfac);

        ResultStore rs = null;
        if (syncOutput == null)
            rs = pfac.getResultStore();

        try
        {
            ExecutionPhase ep = jobUpdater.setPhase(job.getID(), ExecutionPhase.QUEUED, ExecutionPhase.EXECUTING, new Date());
            if ( !ExecutionPhase.EXECUTING.equals(ep) )
            {
                ep = jobUpdater.getPhase(job.getID());
                log.debug(job.getID() + ": QUEUED -> EXECUTING [FAILED] -- phase is " + ep);
                logInfo.setSuccess(false);
                logInfo.setMessage("Could not set job phase to EXECUTING.");
                return;
            }
            log.debug(job.getID() + ": QUEUED -> EXECUTING [OK]");
            tList.add(System.currentTimeMillis());
            sList.add("QUEUED -> EXECUTING: ");

            // start processing the job
            log.debug("invoking TapValidator for REQUEST and VERSION...");
            TapValidator tapValidator = new TapValidator();
            tapValidator.validate(paramList);

            tList.add(System.currentTimeMillis());
            sList.add("initialisation: ");

            log.debug("find DataSource via JNDI lookup...");
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            DataSource queryDataSource = (DataSource) envContext.lookup(queryDataSourceName);
            // this one is optional, so take care
            DataSource uploadDataSource = null;
            try
            {
                uploadDataSource = (DataSource) envContext.lookup(uploadDataSourceName);
            }
            catch (NameNotFoundException nex)
            {
                log.debug(nex.toString());
            }

            if (queryDataSource == null) // application server config issue
                throw new RuntimeException("failed to find the query DataSource");
            tList.add(System.currentTimeMillis());
            sList.add("find DataSources via JNDI: ");

            log.debug("reading TapSchema...");
            TapSchemaDAO dao = pfac.getTapSchemaDAO();
            dao.setDataSource(queryDataSource);
            TapSchema tapSchema = dao.get();
            tList.add(System.currentTimeMillis());
            sList.add("read tap_schema: ");

            log.debug("checking uploaded tables...");
            UploadManager uploadManager = pfac.getUploadManager();
            uploadManager.setDataSource(uploadDataSource);
            Map<String, TableDesc> tableDescs = uploadManager.upload(paramList, job.getID());
            if (tableDescs != null)
            {
                log.debug("adding TAP_UPLOAD SchemaDesc to TapSchema...");
                SchemaDesc tapUploadSchema = new SchemaDesc();
                tapUploadSchema.setSchemaName("TAP_UPLOAD");
                tapUploadSchema.setTableDescs(new ArrayList(tableDescs.values()));
                tapSchema.getSchemaDescs().add(tapUploadSchema);
            }

            log.debug("invoking MaxRecValidator...");
            MaxRecValidator maxRecValidator = pfac.getMaxRecValidator();
            maxRecValidator.setTapSchema(tapSchema);
            maxRecValidator.setJob(job);
            maxRecValidator.setSynchronousMode(syncOutput != null);
            Integer maxRows = maxRecValidator.validate();
            log.debug("MaxRecValidator maxrows = " + maxRows);


            log.debug("creating TapQuery implementation...");
            TapQuery query = pfac.getTapQuery();
            query.setTapSchema(tapSchema);
            query.setExtraTables(tableDescs);
            if (maxRows != null)
                query.setMaxRowCount(maxRows + 1); // +1 so the TableWriter can detect overflow

            log.debug("invoking TapQuery implementation: " + query.getClass().getCanonicalName());
            String sql = query.getSQL();
            List<ParamDesc> selectList = query.getSelectList();
            String queryInfo = query.getInfo();

            log.debug("creating TapTableWriter...");
            TableWriter tableWriter = pfac.getTableWriter();
            tableWriter.setFormatFactory(pfac.getFormatFactory());
            tableWriter.setJob(job);
            tableWriter.setSelectList(selectList);
            tableWriter.setQueryInfo(queryInfo);

            tList.add(System.currentTimeMillis());
            sList.add("parse/convert query: ");

            Connection connection = null;
            PreparedStatement pstmt = null;
            ResultSet resultSet = null;
            URL url = null;
            try
            {
                if (maxRows == null || maxRows.intValue() > 0)
                {
                    log.debug("getting database connection...");
                    connection = queryDataSource.getConnection();
                    tList.add(System.currentTimeMillis());
                    sList.add("get connection from data source: ");

                    // manually control transaction, make fetch size (client batch size) small,
                    // and restrict to forward only so that client memory usage is minimal since
                    // we are only interested in reading the ResultSet once
                    connection.setAutoCommit(false);
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setFetchSize(10000);
                    pstmt.setFetchDirection(ResultSet.FETCH_FORWARD);

                    log.debug("executing query: " + sql);
                    resultSet = pstmt.executeQuery();
                }

                tList.add(System.currentTimeMillis());
                sList.add("execute query and get ResultSet: ");
                
                String filename = "result_" + job.getID() + "." + tableWriter.getExtension();
                String contentType = tableWriter.getContentType();
                
                if (syncOutput != null)
                {
                    
                    log.debug("streaming output: " + contentType);
                    syncOutput.setHeader("Content-Type", contentType);
                    String disp = "attachment; filename=\""+filename+"\"";
                    syncOutput.setHeader("Content-Disposition", disp);
                    if (maxRows == null)
                        tableWriter.write(resultSet, syncOutput.getOutputStream());
                    else
                        tableWriter.write(resultSet, syncOutput.getOutputStream(), maxRows.longValue());
                    tList.add(System.currentTimeMillis());
                    sList.add("stream Result set as " + contentType + ": ");
                }
                else
                {
                    ep = jobUpdater.getPhase(job.getID());
                    if (ExecutionPhase.ABORTED.equals(ep))
                    {
                        log.debug(job.getID() + ": found phase = ABORTED before writing results - DONE");
                        return;
                    }

                    log.debug("result filename: " + filename);
                    rs.setJob(job);
                    rs.setFilename(filename);
                    rs.setContentType(contentType);
                    url = rs.put(resultSet, tableWriter, maxRows);
                    tList.add(System.currentTimeMillis());
                    sList.add("write ResultSet to ResultStore as " + tableWriter.getContentType() + ": ");
                }
                log.debug("executing query... [OK]");
            }
            catch (SQLException ex)
            {
                log.error("SQL Execution error.", ex);
                throw ex;
            }
            finally
            {
                if (connection != null)
                {
                    try
                    {
                        connection.setAutoCommit(true);
                    }
                    catch(Throwable ignore) { }
                    try
                    {
                        resultSet.close();
                    }
                    catch (Throwable ignore) { }
                    try
                    {
                        pstmt.close();
                    }
                    catch (Throwable ignore) { }
                    try
                    {
                        connection.close();
                    }
                    catch (Throwable ignore) { }
                }
            }

            if (syncOutput != null)
            {
                log.debug("setting ExecutionPhase = " + ExecutionPhase.COMPLETED);
                jobUpdater.setPhase(job.getID(), ExecutionPhase.EXECUTING, ExecutionPhase.COMPLETED, new Date());
            }
            else
            {
                try
                {
                    Result res = new Result("result", new URI(url.toExternalForm()));
                    List<Result> results = new ArrayList<Result>();
                    results.add(res);
                    log.debug("setting ExecutionPhase = " + ExecutionPhase.COMPLETED + " with results");
                    jobUpdater.setPhase(job.getID(), ExecutionPhase.EXECUTING, ExecutionPhase.COMPLETED, results, new Date());
                }
                catch (URISyntaxException e)
                {
                    log.error("BUG: URL is not a URI: " + url.toExternalForm(), e);
                    throw e;
                }
            }
        }
        catch (Throwable t)
        {
            logInfo.setMessage(t.getMessage());
            logInfo.setSuccess(false);
            String errorMessage = null;
            URL errorURL = null;
            try
            {
                tList.add(System.currentTimeMillis());
                sList.add("encounter failure: ");

                errorMessage = t.getClass().getSimpleName() + ":" + t.getMessage();
                log.debug("BADNESS", t);
                log.debug("Error message: " + errorMessage);
                VOTableWriter ewriter = new VOTableWriter();
                String filename = "error_" + job.getID() + "." + ewriter.getExtension();
                if (syncOutput != null)
                {
                    syncOutput.setHeader("Content-Type", ewriter.getContentType());
                    String disp = "attachment; filename=\""+filename+"\"";
                    syncOutput.setHeader("Content-Disposition", disp);
                    ewriter.write(t, syncOutput.getOutputStream());
                }
                else
                {
                    rs.setJob(job);
                    rs.setFilename(filename);
                    rs.setContentType(ewriter.getContentType());
                    errorURL = rs.put(t, ewriter);

                    tList.add(System.currentTimeMillis());
                    sList.add("store error with ResultStore ");
                }

                log.debug("Error URL: " + errorURL);
                ErrorSummary es = new ErrorSummary(errorMessage, ErrorType.FATAL, errorURL);
                log.debug("setting ExecutionPhase = " + ExecutionPhase.ERROR);
                jobUpdater.setPhase(job.getID(), ExecutionPhase.EXECUTING, ExecutionPhase.ERROR, es, new Date());
            }
            catch (Throwable t2)
            {
                log.error("failed to persist error", t2);
                // this is really bad: try without the document
                log.debug("setting ExecutionPhase = " + ExecutionPhase.ERROR);
                ErrorSummary es = new ErrorSummary(errorMessage, ErrorType.FATAL);
                try
                {
                    jobUpdater.setPhase(job.getID(), ExecutionPhase.EXECUTING, ExecutionPhase.ERROR, es, new Date());
                }
                catch(Throwable ignore) { }
            }
        }
        finally
        {
            tList.add(System.currentTimeMillis());
            sList.add("set final job state: ");

            for (int i = 1; i < tList.size(); i++)
            {
                long dt = tList.get(i) - tList.get(i - 1);
                log.debug(job.getID() + " -- " + sList.get(i) + dt + "ms");
            }
        }
    }

}
