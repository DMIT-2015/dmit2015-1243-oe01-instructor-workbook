package common.batch;

import jakarta.batch.api.AbstractBatchlet;
import jakarta.batch.api.BatchProperty;
import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Batchlet reads native SQL statements from a script file as a single String
 * and then executes the single SQL string.
 *
 * It either succeeds or fails. If it fails, it CAN be restarted and it runs again.
 */
@Named
@Dependent
public class ExecuteMultiLineSQLStatementBatchlet extends AbstractBatchlet {

    @Inject
    private JobContext _jobContext;

    @Inject
    private Logger _logger;

    @PersistenceContext//(unitName = "mssql-dwpubsales-jpa-pu")
    private EntityManager _entityManager;

    @Inject
    @BatchProperty(name = "sql_script_file")
    private String sqlScriptFile;


    /**
     * Perform a task and return "COMPLETED" if the job has successfully completed
     * otherwise return "FAILED" to indicate the job failed to complete.
     */
    @Transactional
    @Override
    public String process() throws Exception {

        try {
//            try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(sqlScriptFile).toFile()))) { // for reading external files
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(sqlScriptFile)))))	{ // for reading internal files
                StringBuilder sqlStatementBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlStatementBuilder.append(line).append("\n");
                }
                final String sqlStatement = sqlStatementBuilder.toString();
                _entityManager.createNativeQuery(sqlStatement).executeUpdate();
            }
            return BatchStatus.COMPLETED.toString();
        } catch (Exception ex) {
            String errorMessage = String.format("Batch job %s failed to complete.", _jobContext.getJobName());
            _logger.log(Level.SEVERE, errorMessage, ex);
            return BatchStatus.FAILED.toString();
        }

    }
}