
package com.pandamedia.arquillian.test;

import com.pandamedia.beans.ReportBackingBean;
import com.pandamedia.beans.TrackBackingBean;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.controllers.ShopUserJpaController;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.Track;

/**
 *
 * @author Naasir Jusab
 */
@RunWith(Arquillian.class)
public class TrackArq {
    
            
    @Resource(name = "java:app/jdbc/pandamedialocal")
    private DataSource ds;
    @Inject
    private TrackBackingBean trackBacking;
    
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
    
    
    @Test
    public void testAddItem()
    {
        trackBacking.addItem(1);
        Track track = trackBacking.findTrackById(1);
        assertEquals(track.getRemovalStatus(),0);
    }
    
    @Test
    public void testRemoveItem()
    {
        trackBacking.removeItem(1);
        Track track = trackBacking.findTrackById(1);
        assertEquals(track.getRemovalStatus(),1);
    }
    
    @Test
    public void testEdit()
    {
        Track t = trackBacking.findTrackById(1);
        Date date = Calendar.getInstance().getTime();
        t.setDateEntered(date);
        trackBacking.setTrack(t);
        trackBacking.edit();
        assertEquals(trackBacking.getTrack().getDateEntered(), date);

    }
    
}
