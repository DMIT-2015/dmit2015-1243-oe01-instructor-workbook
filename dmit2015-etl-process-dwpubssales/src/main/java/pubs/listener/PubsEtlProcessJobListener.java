package pubs.listener;

import common.ejb.EmailSessionBean;
import jakarta.batch.api.listener.JobListener;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Logger;

/**
 * This listener contains methods that executes before and after a job execution runs.
 * To apply this listener to a batch job you must define a listener element in the Job Specification Language (JSL) file
 * BEFORE the first step element as follows:
 * <pre>{@code
 *
 * <listeners>
 *      <listener ref="pubsEtlProcessJobListener" />
 * </listeners>
 *
 * }</pre>
 */
@Named
@Dependent
public class PubsEtlProcessJobListener implements JobListener {

    @Inject
    private EmailSessionBean emailSessionBean;

    @Inject
    private JobContext _jobContext;

    private Logger _logger = Logger.getLogger(PubsEtlProcessJobListener.class.getName());

    private long _startTime;

    @Override
    public void beforeJob() throws Exception {
        _logger.info(_jobContext.getJobName() + " beforeJob");
        _startTime = System.currentTimeMillis();


    }

    @Override
    public void afterJob() throws Exception {
        _logger.info(_jobContext.getJobName() + "afterJob");
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - _startTime) / 1000;
        String message = _jobContext.getJobName() + " completed in " + durationSeconds + " seconds";
        _logger.info(message);

        emailSessionBean.sendTextEmail(
                "yourname@your-email-domain.com",
                "Pubs ETL Process Job Complete",
                message);
    }

}