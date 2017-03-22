package com.pandamedia.arquillian.test;

import com.pandamedia.beans.ReportBackingBean;
import com.pandamedia.beans.ReportDateBean;
import persistence.controllers.InvoiceJpaController;
import persistence.controllers.ProvinceJpaController;
import persistence.controllers.ShopUserJpaController;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.Invoice;
import persistence.entities.ShopUser;
import persistence.entities.Track;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * TODO find a way to log, also find a way to get province correctly
 * 
 * @author Erika Bourque
 */
@RunWith(Arquillian.class)
public class ReportUnitTest {
//    private static final Logger LOG = Logger.getLogger("ShopUserJpaController.class");
    
//    @Resource(name = "java:app/jdbc/waldo2g4w17")
    @Resource(name = "java:app/jdbc/pandamedialocal")
    private DataSource ds;
    
    @Inject
    private ReportBackingBean reports;

    @Inject
    private ReportDateBean dates;
    
    @Inject
    private ShopUserJpaController userJpa;
    
    @Inject
    private ProvinceJpaController provinceJpa;
    
    @Inject
    private InvoiceJpaController invoiceJpa;
    
    @Deployment
    public static WebArchive deploy() {

        // Use an alternative to the JUnit assert library called AssertJ
        // Need to reference MySQL driver as it is not part of GlassFish
        final File[] dependencies = Maven
                .resolver()
                .loadPomFromFile("pom.xml")
                .resolve(
                        "org.assertj:assertj-core").withoutTransitivity()
                .asFile();

        // For testing Arquillian prefers a resources.xml file over a
        // context.xml
        // Actual file name is resources-mysql-ds.xml in the test/resources
        // folder
        // The SQL script to create the database is also in this folder
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test.war")
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
                .addPackage(ReportBackingBean.class.getPackage())
                .addPackage(RollbackFailureException.class.getPackage())
                .addPackage(Track.class.getPackage())
                .addPackage(ShopUserJpaController.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/glassfish-resources.xml"), "glassfish-resources.xml")
                .addAsResource(new File("src/test/resources-glassfish-remote/test-persistence.xml"), "META-INF/persistence.xml")
//                .addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource("createtestdatabase.sql")
                .addAsLibraries(dependencies);

//        System.out.println(webArchive.toString(true));
        
        return webArchive;
    }
    
    /**
     * This routine is courtesy of Bartosz Majsak who also solved my Arquillian
     * remote server problem
     */
    @Before
    public void seedDatabase() {
        final String seedCreateScript = loadAsString("createtestdatabase.sql");
        //final String seedDataScript = loadAsString("inserttestingdata.sql");

        try (Connection connection = ds.getConnection()) {
            for (String statement : splitStatements(new StringReader(
                    seedCreateScript), ";")) {
                connection.prepareStatement(statement).execute();
                System.out.println("Statement successful: " + statement);
            }
            
//            for (String statement : splitStatements(new StringReader(
//                    seedDataScript), ";")) {
//                connection.prepareStatement(statement).execute();
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed seeding database", e);
        }
        //System.out.println("Seeding works");
    }

    /**
     * The following methods support the seedDatabse method
     */
    private String loadAsString(final String path) {
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(path)) {
            return new Scanner(inputStream).useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close input stream.", e);
        }
    }

    private List<String> splitStatements(Reader reader,
            String statementDelimiter) {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<>();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || isComment(line)) {
                    continue;
                }
                sqlStatement.append(line);
                if (line.endsWith(statementDelimiter)) {
                    statements.add(sqlStatement.toString());
                    sqlStatement.setLength(0);
                }
            }
//            System.out.println(statements);
            return statements;
        } catch (IOException e) {
            throw new RuntimeException("Failed parsing sql", e);
        }
    }

    private boolean isComment(final String line) {
        return line.startsWith("--") || line.startsWith("//")
                || line.startsWith("/*");
    }
    
    /**
     *
     * @throws SQLException
     */
    @Test
    public void findZeroShopUser() throws SQLException, Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        Date saleDate = format.parse("2017/02/20");
        
        ShopUser test = createNewUser("Mr", "Marley", "Bob", "cats avenue", "catcity", 
                "Canada", "A1A1A1", "1234567890", "bob@cat.com", "kitty".getBytes(), "cat");        
        userJpa.create(test);
        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        invoiceJpa.create(invoice);
        
        List<ShopUser> list = reports.getZeroUsers(start, end);

        assertThat(list.contains(test));
//        assertThat(true);
    }
    
    private ShopUser createNewUser(String title, String lastName, String firstName, 
            String streetAddress, String city, String country, String postalCode, 
            String homePhone, String email, byte[] password, String salt)
    {
        ShopUser user = new ShopUser();
        
        user.setTitle(title);
        user.setLastName(lastName);
        user.setFirstName(firstName);
        user.setStreetAddress(streetAddress);
        user.setCity(city);
        user.setCountry(country);
        user.setPostalCode(postalCode);
        user.setHomePhone(homePhone);
        user.setEmail(email);
        user.setHashedPw(password);
        user.setSalt(salt);
//        LOG.info(provinceJpa.findProvinceEntities().toString());
        user.setProvinceId(provinceJpa.findProvinceEntities().get(0));
        
        return user;
    }
    
    private Invoice createNewInvoice(Date saleDate, double totalNetValue, double totalGrossValue, ShopUser user)
    {
        Invoice invoice = new Invoice();
        
        invoice.setSaleDate(saleDate);
        invoice.setTotalNetValue(totalNetValue);
        invoice.setTotalGrossValue(totalGrossValue);
        invoice.setUserId(user);
        
        return invoice;
    }
}
