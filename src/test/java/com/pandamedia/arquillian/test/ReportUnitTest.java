package com.pandamedia.arquillian.test;

import com.pandamedia.beans.ReportBackingBean;
import com.pandamedia.beans.ReportDateBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;
import static org.assertj.core.api.Assertions.assertThat;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.controllers.InvoiceJpaController;
import persistence.controllers.ProvinceJpaController;
import persistence.controllers.ShopUserJpaController;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.Invoice;
import persistence.entities.ShopUser;
import persistence.entities.Track;

/**
 *
 * @author Erika Bourque
 */
@RunWith(Arquillian.class)
public class ReportUnitTest {
    @Resource(name = "java:app/g4w17test")
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
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/glassfish-resources.xml"), "glassfish-resources.xml")
                .addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource("createtables.sql")
                .addAsLibraries(dependencies);

        return webArchive;
    }
    
    /**
     * This routine is courtesy of Bartosz Majsak who also solved my Arquillian
     * remote server problem
     */
    @Before
    public void seedDatabase() {
        final String seedCreateScript = loadAsString("createtables.sql");
        final String seedDataScript = loadAsString("inserttestingdata.sql");

        try (Connection connection = ds.getConnection()) {
            for (String statement : splitStatements(new StringReader(
                    seedCreateScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
            
            for (String statement : splitStatements(new StringReader(
                    seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
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
        ShopUser test = createNewUser("Mr", "Marley", "Bob", "cat", "catcity", 
                "Canada", "A1A1A1", "1234567890", "bob@cat.com", "kitty", "cat");        
        userJpa.create(test);
//        List<ShopUser> users = fab.getAll();
//        assertThat(lfd).hasSize(200);
    }
    
    private ShopUser createNewUser(String title, String lastName, String firstName, 
            String streetAddress, String city, String country, String postalCode, 
            String homePhone, String email, String password, String salt)
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
        user.setPassword(password);
        user.setSalt(salt);
        user.setProvinceId(provinceJpa.findProvince(1));
        
        return user;
    }
    
    private Invoice createNewInvoice(Date saleDate, double totalNetValue, double totalGrossValue)
    {
        Invoice invoice = new Invoice();
        
        // add invoice fields
        
        return invoice;
    }
}
