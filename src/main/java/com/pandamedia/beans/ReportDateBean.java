package com.pandamedia.beans;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Erika Bourque
 */
@Named("reportDates")
@RequestScoped
public class ReportDateBean {
    private static final Logger LOG = Logger.getLogger("ReportDateBean.class");
    
    private Date startDate;
    private Date endDate;
    
    // TODO: should this be in a diff bean?
    public Date getDefaultEndDate() {
        return Calendar.getInstance().getTime();
    }

    // TODO: should this be in a diff bean?
    public Date getDefaultStartDate() {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_YEAR, -30);

        return start.getTime();
    }

    // TODO: should this be in a diff bean?
    public Date getStartDate() {
        if (startDate == null) {
            startDate = getDefaultStartDate();
        }
        return startDate;
    }

    // TODO: should this be in a diff bean?
    public Date getEndDate() {
        if (endDate == null) {
            endDate = getDefaultEndDate();
        }
        return endDate;
    }

    // TODO: should this be in a diff bean?
    public void setStartDate(Date date) {
        LOG.log(Level.INFO, "--- New start date: {0}", date);
        LOG.log(Level.INFO, "--- Current end date: {0}", endDate);
        startDate = date;
    }

    // TODO: should this be in a diff bean?
    public void setEndDate(Date date) {
        LOG.log(Level.INFO, "--- New end date: {0}", date);
        LOG.log(Level.INFO, "--- Current start date: {0}", startDate);
        endDate = date;
    }
}
