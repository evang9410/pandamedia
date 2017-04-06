package com.pandamedia.arquillian.test;

import com.pandamedia.beans.ReportBackingBean;
import com.pandamedia.beans.purchasing.ShoppingCart;
import com.pandamedia.commands.ChangeLanguage;
import com.pandamedia.converters.AlbumConverter;
import com.pandamedia.filters.LoginFilter;
import com.pandamedia.utilities.Messages;
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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import org.junit.Ignore;
import persistence.controllers.AlbumJpaController;
import persistence.controllers.ArtistJpaController;
import persistence.controllers.CoverArtJpaController;
import persistence.controllers.GenreJpaController;
import persistence.controllers.InvoiceAlbumJpaController;
import persistence.controllers.InvoiceTrackJpaController;
import persistence.controllers.RecordingLabelJpaController;
import persistence.controllers.SongwriterJpaController;
import persistence.controllers.TrackJpaController;
import persistence.entities.Album;
import persistence.entities.InvoiceAlbum;
import persistence.entities.InvoiceAlbumPK;
import persistence.entities.InvoiceTrack;
import persistence.entities.InvoiceTrackPK;

/**
 * TODO find a way to log, also find a way to get province correctly
 * 
 * @author Erika Bourque
 */
@RunWith(Arquillian.class)
public class ReportUnitTest {
//    private static final Logger LOG = Logger.getLogger("ShopUserJpaController.class");

    // TO TEST ON WALDO comment and uncomment the @Resources
    // AND the persistence XMLs, both needed to work
//    @Resource(name = "java:app/jdbc/waldo2g4w17")
    @Resource(name = "java:app/jdbc/pandamedialocal")
    private DataSource ds;    
    @Inject
    private ReportBackingBean reports;
    @Inject
    private ShopUserJpaController userJpa;
    @Inject
    private ProvinceJpaController provinceJpa;
    @Inject
    private InvoiceJpaController invoiceJpa;
    @Inject
    private InvoiceTrackJpaController invoiceTrackJpa;
    @Inject
    private AlbumJpaController albumJpa;
    @Inject
    private ArtistJpaController artistJpa;
    @Inject
    private CoverArtJpaController coverJpa;
    @Inject
    private GenreJpaController genreJpa;
    @Inject
    private SongwriterJpaController songwriterJpa;
    @Inject
    private TrackJpaController trackJpa;
    @Inject
    private RecordingLabelJpaController recordingJpa;
    @Inject
    private InvoiceAlbumJpaController invoiceAlbumJpa;
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
    @Ignore
    public void getZeroUsersContainsTest() throws Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date outside start and end
        Date saleDate = format.parse("2017/02/15");
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        
        // Action
        List<ShopUser> list = reports.getZeroUsers(start, end);

        // Assert
        assertThat(list.contains(test));
    }
    
    /**
     *
     * @throws SQLException
     */
    @Test
    @Ignore
    public void getZeroUsersNotContainsTest() throws Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");
        ShopUser test = createTestUser();        
        createNewInvoice(saleDate, 10, 11, test);
        
        // Action
        List<ShopUser> list = reports.getZeroUsers(start, end);

        // Assert
        assertThat(!list.contains(test));
    }
    
    /**
     *
     * @throws SQLException
     */
    @Test
    @Ignore
    public void getZeroTracksContainsTest() throws SQLException, Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date outside start and end
        Date saleDate = format.parse("2017/02/15");
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = createTestTrack();
        createNewInvoiceTrack(invoice, track, 10.00);
        
        // Action
        List<Track> list = reports.getZeroTracks(start, end);

        // Assert
        assertThat(list.contains(track));
    }
    
    @Test
    @Ignore
    public void getZeroTracksNotContainsTest() throws SQLException, Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = createTestTrack();
        createNewInvoiceTrack(invoice, track, 10.00);
        
        // Action
        List<Track> list = reports.getZeroTracks(start, end);

        // Assert
        assertThat(!list.contains(track));
    }
    
    @Test
    @Ignore
    public void getTopClientsContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");
        ShopUser test = createTestUser();        
        createNewInvoice(saleDate, 10, 11, test);
        
        // Action
        List<Object[]> list = reports.getTopClients(start, end);        
        // obj[0] is amount sold, obj[1] is user
        for(Object[] obj : list)
        {
            if (obj[1].equals(test))
            {
                isFound = true;
                break;
            }
        }

        // Assert
        assertThat(isFound);
    }
    
    @Test
    @Ignore
    public void getTopClientsNotContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date outside start and end
        Date saleDate = format.parse("2017/02/15");
        ShopUser test = createTestUser();        
        createNewInvoice(saleDate, 10, 11, test);
        
        // Action
        List<Object[]> list = reports.getTopClients(start, end);        
        // obj[0] is amount sold, obj[1] is user
        for(Object[] obj : list)
        {
            if (obj[1].equals(test))
            {
                isFound = true;
                break;
            }
        }

        // Assert
        assertThat(!isFound);
    }
    
    @Test
    @Ignore
    public void getTopSellersTracksContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = createTestTrack();
        createNewInvoiceTrack(invoice, track, 10.00);
        
        // Action
        List<Object[]> list = reports.getTopSellersTracks(start, end);
        // obj[0] is amount sold, obj[1] is track
        for(Object[] obj : list)
        {
            if (obj[1].equals(track))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(isFound);
    }
    
    @Test
    @Ignore
    public void getTopSellersTracksNotContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date outside start and end
        Date saleDate = format.parse("2017/02/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = createTestTrack();
        createNewInvoiceTrack(invoice, track, 10.00);
        
        // Action
        List<Object[]> list = reports.getTopSellersTracks(start, end);
        // obj[0] is amount sold, obj[1] is track
        for(Object[] obj : list)
        {
            if (obj[1].equals(track))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(!isFound);
    }
    
    @Test
    @Ignore
    public void getTopSellersAlbumsContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Album album = createTestAlbum();
        createNewInvoiceAlbum(invoice, album, 10.00);
        
        // Action
        List<Object[]> list = reports.getTopSellersAlbums(start, end);
        // obj[0] is amount sold, obj[1] is album
        for(Object[] obj : list)
        {
            if (obj[1].equals(album))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(isFound);
    }
    
    @Test
    @Ignore
    public void getTopSellersAlbumsNotContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date outside start and end
        Date saleDate = format.parse("2017/02/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Album album = createTestAlbum();
        createNewInvoiceAlbum(invoice, album, 10.00);
        
        // Action
        List<Object[]> list = reports.getTopSellersAlbums(start, end);
        // obj[0] is amount sold, obj[1] is album
        for(Object[] obj : list)
        {
            if (obj[1].equals(album))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(!isFound);
    }
    
    @Test
    @Ignore
    public void getTotalSalesAlbumsContainsTest() throws SQLException, Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        double previousFinal = 1336.9100000000005;
        double previousCost = 248.5;
        double previousProfit = 1088.4100000000005;
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Album album = createTestAlbum();
        double albumCost = 5.00;
        double albumFinal = 10.00;
        double albumProfit = 5.00;
        createNewInvoiceAlbum(invoice, album, albumFinal);
        
        // Action
        List<Object[]> list = reports.getTotalSalesAlbums(start, end);
        // First item in list contains data
        Object[] array = list.get(0);
        double totalFinal = (double)array[0];
        double totalCost = (double)array[1];
        double totalProfit = (double)array[2];
        
//        System.out.println("Total Sales Albums Totals: " + totalFinal + " " + totalCost + " " + totalProfit);
        
        // Assert
        assertThat((totalFinal == albumFinal + previousFinal) && (totalCost == albumCost + previousCost) && (totalProfit == albumProfit + previousProfit));
    }
    
    @Test
    @Ignore
    public void getTotalSalesAlbumsNotContainsTest() throws SQLException, Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        double previousFinal = 1336.9100000000005;
        double previousCost = 248.5;
        double previousProfit = 1088.4100000000005;
        // Sale date outside start and end
        Date saleDate = format.parse("2017/02/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Album album = createTestAlbum();
        double albumFinal = 10.00;
        createNewInvoiceAlbum(invoice, album, albumFinal);
        
        // Action
        List<Object[]> list = reports.getTotalSalesAlbums(start, end);
        // First item in list contains data
        Object[] array = list.get(0);
        double totalFinal = (double)array[0];
        double totalCost = (double)array[1];
        double totalProfit = (double)array[2];
        
//        System.out.println("Total Sales Albums Totals: " + totalFinal + " " + totalCost + " " + totalProfit);
        
        // Assert
        assertThat((totalFinal == previousFinal) && (totalCost == previousCost) && (totalProfit == previousProfit));
    }
    
    @Test
    @Ignore
    public void getTotalSalesTracksContainsTest() throws SQLException, Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        double previousFinal = 513.1700000000001;
        double previousCost = 45.5;
        double previousProfit = 467.6700000000001;
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = createTestTrack();
        double trackCost = 5.00;
        double trackFinal = 10.00;
        double trackProfit = 5.00;
        createNewInvoiceTrack(invoice, track, trackFinal);
        
        // Action
        List<Object[]> list = reports.getTotalSalesTracks(start, end);
        // First item in list contains data
        Object[] array = list.get(0);
        double totalFinal = (double)array[0];
        double totalCost = (double)array[1];
        double totalProfit = (double)array[2];
        
//        System.out.println("Total Sales Tracks Totals: " + totalFinal + " " + totalCost + " " + totalProfit);
        
        // Assert
        assertThat((totalFinal == trackFinal + previousFinal) && (totalCost == trackCost + previousCost) && (totalProfit == trackProfit + previousProfit));
    }
    
    @Test
    @Ignore
    public void getTotalSalesTracksNotContainsTest() throws SQLException, Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        double previousFinal = 513.1700000000001;
        double previousCost = 45.5;
        double previousProfit = 467.6700000000001;
        // Sale date outside start and end
        Date saleDate = format.parse("2017/02/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = createTestTrack();
        double trackFinal = 10.00;
        createNewInvoiceTrack(invoice, track, trackFinal);
        
        // Action
        List<Object[]> list = reports.getTotalSalesTracks(start, end);
        // First item in list contains data
        Object[] array = list.get(0);
        double totalFinal = (double)array[0];
        double totalCost = (double)array[1];
        double totalProfit = (double)array[2];
        
//        System.out.println("Total Sales Tracks Totals: " + totalFinal + " " + totalCost + " " + totalProfit);
        
        // Assert
        assertThat((totalFinal == previousFinal) && (totalCost == previousCost) && (totalProfit == previousProfit));
    }
    
    @Test
    @Ignore
    public void getTotalSalesTracksDetailsContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = createTestTrack();
        createNewInvoiceTrack(invoice, track, 10.00);
        
        // Action
        List<Object[]> list = reports.getTotalSalesTracksDetails(start, end);
        // obj[1] is track, rest is other details
        for(Object[] obj : list)
        {
            if (obj[1].equals(track))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(isFound);
    }
    
    @Test
    @Ignore
    public void getTotalSalesTracksDetailsNotContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date outside start and end
        Date saleDate = format.parse("2017/02/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = createTestTrack();
        createNewInvoiceTrack(invoice, track, 10.00);
        
        // Action
        List<Object[]> list = reports.getTotalSalesTracksDetails(start, end);
        // obj[1] is track, rest is other details
        for(Object[] obj : list)
        {
            if (obj[1].equals(track))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(!isFound);
    }
    
    @Test
    @Ignore
    public void getTotalSalesAlbumsDetailsContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Album album = createTestAlbum();
        createNewInvoiceAlbum(invoice, album, 10.00);
        
        // Action
        List<Object[]> list = reports.getTotalSalesAlbumsDetails(start, end);
        // obj[0] is amount sold, obj[1] is album
        for(Object[] obj : list)
        {
            if (obj[1].equals(album))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(isFound);
    }
    
    @Test
    @Ignore
    public void getTotalSalesAlbumsDetailsNotContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date outside start and end
        Date saleDate = format.parse("2017/02/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Album album = createTestAlbum();
        createNewInvoiceAlbum(invoice, album, 10.00);
        
        // Action
        List<Object[]> list = reports.getTotalSalesAlbumsDetails(start, end);
        // obj[0] is amount sold, obj[1] is album
        for(Object[] obj : list)
        {
            if (obj[1].equals(album))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(!isFound);
    }
    
    @Test
    @Ignore
    public void getSalesByAlbumDetailsContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Album album = albumJpa.findAlbum(1);
        createNewInvoiceAlbum(invoice, album, 10.00);
        
        // Action
        List<Object[]> list = reports.getSalesByAlbum(start, end, album);
        // obj[0] has invoice
        for(Object[] obj : list)
        {
            if (obj[0].equals(invoice))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(isFound);
    }
    
    @Test
    @Ignore
    public void getSalesByAlbumDetailsContainsNotTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        Date saleDate = format.parse("2017/02/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Album album = albumJpa.findAlbum(1);
        createNewInvoiceAlbum(invoice, album, 10.00);
        
        // Action
        List<Object[]> list = reports.getSalesByAlbum(start, end, album);
        // obj[0] has invoice
        for(Object[] obj : list)
        {
            if (obj[0].equals(invoice))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(!isFound);
    }
    
    @Test
    @Ignore
    public void getSalesByAlbumContainsTest() throws SQLException, Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        double previousFinal = 95.76;
        double previousCost = 42.0;
        double previousProfit = 53.760000000000005;
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Album album = albumJpa.findAlbum(1);
        double albumCost = album.getCostPrice();
        double albumFinal = 10.00;
        double albumProfit = albumFinal - albumCost;
        createNewInvoiceAlbum(invoice, album, albumFinal);
        
        // Action
        List<Object[]> list = reports.getSalesByAlbumTotals(start, end, album);
        // First item in list contains data
        Object[] array = list.get(0);
        double totalFinal = (double)array[0];
        double totalCost = (double)array[1];
        double totalProfit = (double)array[2];
        
        // Assert
        assertThat((totalFinal == albumFinal + previousFinal) && (totalCost == albumCost + previousCost) && (totalProfit == albumProfit + previousProfit));
    }
    
    @Test
    @Ignore
    public void getSalesByAlbumContainsNotTest() throws SQLException, Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        double previousFinal = 95.76;
        double previousCost = 42.0;
        double previousProfit = 53.760000000000005;
        // Sale date outside start and end
        Date saleDate = format.parse("2017/02/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Album album = albumJpa.findAlbum(1);
        double albumFinal = 10.00;
        createNewInvoiceAlbum(invoice, album, albumFinal);
        
        // Action
        List<Object[]> list = reports.getSalesByAlbumTotals(start, end, album);
        // First item in list contains data
        Object[] array = list.get(0);
        double totalFinal = (double)array[0];
        double totalCost = (double)array[1];
        double totalProfit = (double)array[2];
        
        // Assert
        assertThat((totalFinal == previousFinal) && (totalCost == previousCost) && (totalProfit == previousProfit));
    }

    @Test
    @Ignore
    public void getSalesByTrackDetailsContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = trackJpa.findTrack(1);
        createNewInvoiceTrack(invoice, track, 10.00);
        
        // Action
        List<Object[]> list = reports.getSalesByTrack(start, end, track);
        // obj[0] has invoice
        for(Object[] obj : list)
        {
            if (obj[0].equals(invoice))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(isFound);
    }
    
    @Test
    @Ignore
    public void getSalesByTrackDetailsNotContainsTest() throws SQLException, Exception {
        // Set Up
        boolean isFound = false;
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date outside start and end
        Date saleDate = format.parse("2017/02/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = trackJpa.findTrack(1);
        createNewInvoiceTrack(invoice, track, 10.00);
        
        // Action
        List<Object[]> list = reports.getSalesByTrack(start, end, track);
        // obj[0] has invoice
        for(Object[] obj : list)
        {
            if (obj[0].equals(invoice))
            {
                isFound = true;
                break;
            }
        }
        
        // Assert
        assertThat(!isFound);
    }

    @Test
    @Ignore
    public void getSalesByTrackContainsTest() throws SQLException, Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        double previousFinal = 2.38;
        double previousCost = 0.5;
        double previousProfit = 1.88;
        // Sale date between start and end
        Date saleDate = format.parse("2017/01/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = trackJpa.findTrack(3);
        double trackCost = track.getCostPrice();
        double trackFinal = 10.00;
        double trackProfit = trackFinal - trackCost;
        createNewInvoiceTrack(invoice, track, trackFinal);
        
        // Action
        List<Object[]> list = reports.getSalesByTrackTotals(start, end, track);
        // First item in list contains data
        Object[] array = list.get(0);
        double totalFinal = (double)array[0];
        double totalCost = (double)array[1];
        double totalProfit = (double)array[2];
        
//        System.out.println("Sales by track totals: " + totalFinal + "\t" + totalCost + "\t" + totalProfit);
        
        // Assert
        assertThat((totalFinal == trackFinal + previousFinal) && (totalCost == trackCost + previousCost) && (totalProfit == trackProfit + previousProfit));
    }
    
    @Test
    @Ignore
    public void getSalesByTrackNotContainsTest() throws SQLException, Exception {
        // Set Up
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start = format.parse("2017/01/01");
        Date end = format.parse("2017/02/01");
        // Sale date between start and end
        double previousFinal = 2.38;
        double previousCost = 0.5;
        double previousProfit = 1.88;
        // Sale date between start and end
        Date saleDate = format.parse("2017/02/15");        
        ShopUser test = createTestUser();        
        Invoice invoice = createNewInvoice(saleDate, 10, 11, test);
        Track track = trackJpa.findTrack(3);
        double trackFinal = 10.00;
        createNewInvoiceTrack(invoice, track, trackFinal);
        
        // Action
        List<Object[]> list = reports.getSalesByTrackTotals(start, end, track);
        // First item in list contains data
        Object[] array = list.get(0);
        double totalFinal = (double)array[0];
        double totalCost = (double)array[1];
        double totalProfit = (double)array[2];
        
//        System.out.println("Sales by track totals: " + totalFinal + "\t" + totalCost + "\t" + totalProfit);
        
        // Assert
        assertThat((totalFinal == previousFinal) && (totalCost == previousCost) && (totalProfit == previousProfit));
    }    
    
    // TODO: sales by artist track
    // TODO: sales by artist track details
    // TODO: sales by artist album
    // TODO: sales by artist album details
    // TODO: sales by client
    // TODO: sales by client details
    
    /**
     * Test user never has to be different, do not need to customize.
     * 
     * @return the user
     */
    private ShopUser createTestUser() throws Exception
    {
        ShopUser user = new ShopUser();
        
        user.setTitle("Mr");
        user.setLastName("Marley");
        user.setFirstName("Bob");
        user.setStreetAddress("cats avenue");
        user.setCity("catcity");
        user.setCountry("Canada");
        user.setPostalCode("A1A1A1");
        user.setHomePhone("1234567890");
        user.setEmail("bob@cat.com");
        user.setHashedPw("kitty".getBytes());
        user.setSalt("cat");
        user.setProvinceId(provinceJpa.findProvinceEntities().get(0));
        userJpa.create(user);
        
        return user;
    }
    
    /**
     * Invoices must be customizable, as dates can vary.
     * 
     * @param saleDate          Sale date of invoice
     * @param totalNetValue     net value of invoice
     * @param totalGrossValue   gross value of invoice
     * @param user              invoice's user
     * @return                  the invoice
     */
    private Invoice createNewInvoice(Date saleDate, double totalNetValue, 
            double totalGrossValue, ShopUser user) throws Exception
    {
        Invoice invoice = new Invoice();
        
        invoice.setSaleDate(saleDate);
        invoice.setTotalNetValue(totalNetValue);
        invoice.setTotalGrossValue(totalGrossValue);
        invoice.setUserId(user);
        invoiceJpa.create(invoice);
        
        return invoice;
    }
    
    /**
     * Test track never has to be different, do not need to customize.
     * 
     * @return              the test track
     * @throws Exception 
     */
    private Track createTestTrack() throws Exception
    {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Track t = new Track();
        
        t.setTitle("Title");
        t.setReleaseDate(format.parse("2016/12/31"));
        t.setPlayLength("3:00");
        t.setAlbumTrackNumber(1);
        t.setPartOfAlbum((short)1);
        t.setCostPrice(5.0);
        t.setListPrice(6.0);
        t.setAlbumId(albumJpa.findAlbum(1));
        t.setArtistId(artistJpa.findArtist(1));
        t.setCoverArtId(coverJpa.findCoverArt(1));
        t.setGenreId(genreJpa.findGenre(1));
        t.setSongwriterId(songwriterJpa.findSongwriter(1));
        t.setDateEntered(Calendar.getInstance().getTime());
        trackJpa.create(t);
        
        return t;
    }
    
    /**
     * Invoice Tracks must be customizable, as ids and final price are variable.
     * 
     * @param invoiceId
     * @param trackId
     * @param finalPrice
     * @return              the invoice track
     * @throws Exception 
     */
    private void createNewInvoiceTrack(Invoice invoiceId, Track trackId, 
            double finalPrice) throws Exception
    {
        InvoiceTrack it = new InvoiceTrack();
        InvoiceTrackPK itpk = new InvoiceTrackPK();
        
        itpk.setInvoiceId(invoiceId.getId());
        itpk.setTrackId(trackId.getId());
        
        it.setInvoiceTrackPK(itpk);
        it.setFinalPrice(finalPrice);
        it.setInvoice(invoiceId);
        it.setTrack(trackId);
        invoiceTrackJpa.create(it);
    }
    
    private Album createTestAlbum() throws Exception
    {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Album a = new Album();
        
        a.setTitle("Title");
        a.setReleaseDate(format.parse("2016/12/31"));
        a.setNumTracks(1);
        a.setDateEntered(Calendar.getInstance().getTime());
        a.setCostPrice(5.0);
        a.setListPrice(6.0);
        a.setArtistId(artistJpa.findArtist(1));
        a.setCoverArtId(coverJpa.findCoverArt(1));
        a.setGenreId(genreJpa.findGenre(1));
        a.setRecordingLabelId(recordingJpa.findRecordingLabel(1));
        albumJpa.create(a);
        
        return a;
    }
    
    /**
     * Invoice Albums must be customizable, as ids and final price are variable.
     * 
     * @param invoiceId
     * @param trackId
     * @param finalPrice
     * @return              the invoice track
     * @throws Exception 
     */
    private void createNewInvoiceAlbum(Invoice invoiceId, Album albumId, 
            double finalPrice) throws Exception
    {
        InvoiceAlbum ia = new InvoiceAlbum();
        InvoiceAlbumPK iapk = new InvoiceAlbumPK();
        
        iapk.setInvoiceId(invoiceId.getId());
        iapk.setAlbumId(albumId.getId());
        
        ia.setInvoiceAlbumPK(iapk);
        ia.setFinalPrice(finalPrice);
        ia.setInvoice(invoiceId);
        ia.setAlbum(albumId);
        invoiceAlbumJpa.create(ia);
    }
}
