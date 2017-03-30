package com.pandamedia.beans;

import com.pandamedia.utilities.Messages;
import jodd.mail.Email;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;
import persistence.entities.Invoice;

/**
 *
 * @author Erika Bourque
 */
public class EmailBean {
    private final String emailAddress="ebourquesend@gmail.com";
    private final String emailPassword="erikasendemail";
    private final String smtpServerName="smtp.gmail.com";
    
    public EmailBean()
    {
        super();
    }
    
    public void sendInvoiceEmail(String userEmail, Invoice invoice)
    {
        Email email = new Email();
        
        // Preparing the email fields
        email.to(userEmail).from(emailAddress).subject(Messages.
                getString("bundles.messages", "emailSubject", null));        
        email.addHtml(buildMessage(invoice));
        
        // Send email
        send(email);
    }
    
    
    private String buildMessage(Invoice invoice)
    {
        StringBuilder builder = new StringBuilder();
        
        // Email body start
        builder.append("<html>"
                + "<META http-equiv=Content-Type content=\"text/html; charset=utf-8\">"
                + "<body>");
        
        // do stuff here
        
        // Email body end
        builder.append("</body>"
                + "</html>");
        
        return builder.toString();
    }
    
    /**
     * This method sends the email through the Jodd Mail API.
     */
    private void send(Email email)
    {
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
}
