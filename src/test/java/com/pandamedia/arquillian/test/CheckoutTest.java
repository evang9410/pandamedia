package com.pandamedia.arquillian.test;

import com.pandamedia.beans.ReportBackingBean;
import com.pandamedia.beans.UserBean;
import com.pandamedia.beans.purchasing.CheckoutBackingBean;
import com.pandamedia.beans.purchasing.ShoppingCart;
import com.pandamedia.commands.ChangeLanguage;
import com.pandamedia.converters.AlbumConverter;
import com.pandamedia.filters.LoginFilter;
import com.pandamedia.utilities.Messages;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.controllers.AlbumJpaController;
import persistence.controllers.ProvinceJpaController;
import persistence.controllers.ShopUserJpaController;
import persistence.controllers.TrackJpaController;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.Album;
import persistence.entities.Invoice;
import persistence.entities.InvoiceAlbum;
import persistence.entities.InvoiceTrack;
import persistence.entities.ShopUser;
import persistence.entities.Track;

/**
 * This class tests the checkout bean.
 * 
 * @author Erika Bourque
 */
@RunWith(Arquillian.class)
@Ignore
public class CheckoutTest {
    // TO TEST ON WALDO comment and uncomment the @Resources
    // AND the persistence XMLs, both needed to work
    
//    @Resource(name = "java:app/jdbc/waldo2g4w17")
    @Resource(name = "java:app/jdbc/pandamedialocal")
    private DataSource ds;
    @Inject
    private ShopUserJpaController userJpa;
    @Inject
    private ProvinceJpaController provinceJpa;
    @Inject
    private AlbumJpaController albumJpa;
    @Inject
    private TrackJpaController trackJpa;
    @Inject
    private ShoppingCart cart;
    @Inject
    private CheckoutBackingBean checkout;
    @Inject
    private UserBean userBean;

    @Deployment
    public static WebArchive deploy() {
        // Use an alternative to the JUnit assert library called AssertJ
        // Need to reference MySQL driver and jodd as it is not part of GlassFish
        final File[] dependencies = Maven
                .resolver()
                .loadPomFromFile("pom.xml")
                .resolve(new String[]{
            "org.assertj:assertj-core", "org.jodd:jodd-mail"}).withoutTransitivity()
                .asFile();

        // For testing Arquillian prefers a resources.xml file over a
        // context.xml
        // Actual file name is resources-mysql-ds.xml in the test/resources
        // folder
        // The SQL script to create the database is also in this folder
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test.war")
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
                .addPackage(ReportBackingBean.class.getPackage())
                .addPackage(ChangeLanguage.class.getPackage())
                .addPackage(AlbumConverter.class.getPackage())
                .addPackage(LoginFilter.class.getPackage())
                .addPackage(Messages.class.getPackage())
                .addPackage(ShopUserJpaController.class.getPackage())
                .addPackage(RollbackFailureException.class.getPackage())
                .addPackage(Track.class.getPackage())
                .addPackage(ShoppingCart.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/glassfish-resources.xml"), "glassfish-resources.xml")
                .addAsResource(new File("src/test/resources-glassfish-remote/test-persistence.xml"), "META-INF/persistence.xml")
//                .addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource("createtestdatabase.sql")
                .addAsLibraries(dependencies);
        return webArchive;
    }

    /**
     * This routine is courtesy of Bartosz Majsak who also solved my Arquillian
     * remote server problem
     */
    @Before
    public void seedDatabase() {
        final String seedCreateScript = loadAsString("createtestdatabase.sql");

        try (Connection connection = ds.getConnection()) {
            for (String statement : splitStatements(new StringReader(
                    seedCreateScript), ";")) {
                connection.prepareStatement(statement).execute();
                System.out.println("Statement successful: " + statement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed seeding database", e);
        }
    }

    /**
     * The following methods support the seedDatabase method
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
    
    @Test
    public void finalizeInvoiceTest() throws Exception
    {
        // Set up
        ShopUser tester = createTestUser();
        userBean.setShopUser(tester);
        cart.addAlbum(albumJpa.findAlbum(1));
        List<Invoice> invoices;
        
        // Action
        // TODO: injections in checkout bean do not work, making all these tests fail :(
//        System.out.println(checkout.getGst());
        checkout.finalizePurchase();
        invoices = userJpa.findShopUser(tester.getId()).getInvoiceList();
        
        // New user, should only have the new invoice
        // Assert
        assertThat(invoices.size()).isEqualTo(1);
    }
    
    @Test
    @Ignore
    public void finalizeAlbumTest() throws Exception
    {
        // Set up
        boolean isFound = false;
        ShopUser tester = createTestUser();
        userBean.setShopUser(tester);
        Album album = albumJpa.findAlbum(1);
        cart.addAlbum(album);
        List<InvoiceAlbum> albumList;
        
        // Action
        checkout.finalizePurchase();
        albumList = userJpa.findShopUser(tester.getId()).getInvoiceList().get(0).getInvoiceAlbumList();
        for(InvoiceAlbum ia : albumList)
        {
            if (ia.getAlbum().equals(album))
            {
                isFound = true;
            }
        }
        
        // Assert
        assertThat(isFound).isTrue();
    }
    
    @Test
    @Ignore
    public void finalizeTrackTest() throws Exception
    {
        // Set up
        boolean isFound = false;
        ShopUser tester = createTestUser();
        userBean.setShopUser(tester);
        Track track = trackJpa.findTrack(1);
        cart.addTrack(track);
        List<InvoiceTrack> trackList;
        
        // Action
        checkout.finalizePurchase();
        trackList = userJpa.findShopUser(tester.getId()).getInvoiceList().get(0).getInvoiceTrackList();
        for(InvoiceTrack ia : trackList)
        {
            if (ia.getTrack().equals(track))
            {
                isFound = true;
            }
        }
                
        // Assert
        assertThat(isFound).isTrue();
    }
    
    private ShopUser createTestUser() throws Exception {
        ShopUser user = new ShopUser();

        user.setTitle("Mr");
        user.setLastName("Marley");
        user.setFirstName("Bob");
        user.setStreetAddress("cats avenue");
        user.setCity("catcity");
        user.setCountry("Canada");
        user.setPostalCode("A1A1A1");
        user.setHomePhone("1234567890");
        // For invoice email, sends to itself
        user.setEmail("ebourquesend@gmail.com");
        user.setHashedPw("kitty".getBytes());
        user.setSalt("cat");
        user.setProvinceId(provinceJpa.findProvinceEntities().get(0));
        userJpa.create(user);

        return user;
    }
}
