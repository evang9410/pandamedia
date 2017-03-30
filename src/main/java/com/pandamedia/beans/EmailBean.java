package com.pandamedia.beans;

import com.pandamedia.utilities.Messages;
import java.util.List;
import java.util.logging.Logger;
import jodd.mail.Email;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;
import persistence.entities.Invoice;
import persistence.entities.InvoiceAlbum;
import persistence.entities.InvoiceTrack;
import persistence.entities.ShopUser;

/**
 *
 * @author Erika Bourque
 */
public class EmailBean {
    private static final Logger LOG = Logger.getLogger("EmailBean.class");
    private final String emailAddress="ebourquesend@gmail.com";
    private final String emailPassword="erikasendemail";
    private final String smtpServerName="smtp.gmail.com";
    private Invoice invoice;
    
    public EmailBean()
    {
        super();
    }
    
    public void sendInvoiceEmail(String userEmail, Invoice invoice)
    {
        this.invoice = invoice;
        Email email = new Email();
        
        // Preparing the email fields
        email.to(userEmail).from(emailAddress).subject(Messages.
                getString("bundles.messages", "emailSubject", null));        
        email.addHtml(buildMessage());
        
        // Send email
        send(email);
    }
    
    
    private String buildMessage()
    {
        LOG.info("Building email.");
        StringBuilder builder = new StringBuilder();
        
        // Email body start
        builder.append("<html><META http-equiv=Content-Type content=\"text/html; charset=utf-8\"><body>");
        
        // Add the invoice details
        invoiceDetailsTable(builder);        
        builder.append("<br/>");
        billingInfoTable(builder);
        builder.append("<br/>");
        if (!invoice.getInvoiceAlbumList().isEmpty())
        {
            albumInfoTable(builder);
            builder.append("<br/>");
        }
        if (!invoice.getInvoiceTrackList().isEmpty())
        {
            trackInfoTable(builder);
            builder.append("<br/>");
        }
        
        // do stuff here
        
        // Email body end
        builder.append("</body></html>");
        
        return builder.toString();
    }
    
    /**
     * This method sends the email through the Jodd Mail API.
     */
    private void send(Email email)
    {
        LOG.info("Sending email.");        
        // Create the server
        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(smtpServerName)
                .authenticateWith(emailAddress, emailPassword);
        
        // Create the session
        SendMailSession session = smtpServer.createSession();
        
        // Open session, send email, and close session
        session.open();
        session.sendMail(email);
        session.close();
    }
    
    private void invoiceDetailsTable(StringBuilder builder)
    {
        String[] headers = {"invoiceNumHeader", "saleDateHeader", "subtotalHeader", 
            "gstLbl", "pstLbl", "hstLbl", "totalHeader"};
        Object[] fields = {invoice.getId(), invoice.getSaleDate(), invoice.getTotalGrossValue(), 
            invoice.getGstTax(), invoice.getPstTax(), invoice.getHstTax(), invoice.getTotalNetValue()};
        
        // Header
        builder.append("<table><tr><th colspan=2>");
        builder.append(Messages.getString("bundles.messages", "invoiceSummaryTitle", null));
        builder.append("</th></tr>");
        
        // Invoice details
        for(int i = 0; i < headers.length; i++)
        {
            builder.append("<tr><td>");
            builder.append(Messages.getString("bundles.messages", headers[i], null));
            builder.append("</td><td>");
            builder.append(fields[i]);
            builder.append("</td></tr>");
        }
        
        builder.append("</table>");
    }
    
    private void billingInfoTable(StringBuilder builder)
    {
        ShopUser user = invoice.getUserId();
        
        // Creating lines for billing info
        String line1 = user.getTitle() + " " + user.getFirstName() + " " + user.getLastName();
        String line2 = user.getStreetAddress() + " " + user.getStreetAddress2();
        String line3 = user.getCity() + " " + user.getProvinceId().getName() + " " + user.getPostalCode();
        String line5 = user.getHomePhone() + " " + user.getCellPhone();
        String[] fields = {line1, line2, line3, user.getCountry(), line5};
        
        // Header
        builder.append("<table><tr><th>");
        builder.append(Messages.getString("bundles.messages", "billingTitle", null));
        builder.append("</th></tr>");
        
        // Billing detail lines
        for (String field : fields) {
            builder.append("<tr><td>");
            builder.append(field);
            builder.append("</td></tr>");
        }
        
        builder.append("</table>");
    }
    
    private void trackInfoTable(StringBuilder builder)
    {
        List<InvoiceTrack> tracks = invoice.getInvoiceTrackList();
        
        // Headers
        builder.append("<table><tr><th colspan=3>");
        builder.append(Messages.getString("bundles.messages", "trackLbl", null));
        builder.append("</th></tr><tr><th>");
        builder.append(Messages.getString("bundles.messages", "titleHeader", null));
        builder.append("</th><th>");
        builder.append(Messages.getString("bundles.messages", "artistHeader", null));
        builder.append("</th><th>");
        builder.append(Messages.getString("bundles.messages", "priceHeader", null));
        builder.append("</th></tr>");
        
        // Track details
        for(InvoiceTrack track : tracks)
        {
            builder.append("<tr><td>");
            builder.append(track.getTrack().getTitle());
            builder.append("</td><td>");
            builder.append(track.getTrack().getArtistId().getName());
            builder.append("</td><td>");
            builder.append(track.getFinalPrice());
            builder.append("</td></tr>");
        }
        
        builder.append("</table>");
    }
    
    private void albumInfoTable(StringBuilder builder)
    {
        List<InvoiceAlbum> albums = invoice.getInvoiceAlbumList();
        
        // Headers
        builder.append("<table><tr><th colspan=3>");
        builder.append(Messages.getString("bundles.messages", "albumLbl", null));
        builder.append("</th></tr><tr><th>");
        builder.append(Messages.getString("bundles.messages", "titleHeader", null));
        builder.append("</th><th>");
        builder.append(Messages.getString("bundles.messages", "artistHeader", null));
        builder.append("</th><th>");
        builder.append(Messages.getString("bundles.messages", "priceHeader", null));
        builder.append("</th></tr>");
        
        // Album details
        for(InvoiceAlbum album : albums)
        {
            builder.append("<tr><td>");
            builder.append(album.getAlbum().getTitle());
            builder.append("</td><td>");
            builder.append(album.getAlbum().getArtistId().getName());
            builder.append("</td><td>");
            builder.append(album.getFinalPrice());
            builder.append("</td></tr>");
        }
        
        builder.append("</table>");
    }
}
