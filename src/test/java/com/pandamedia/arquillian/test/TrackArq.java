
package com.pandamedia.arquillian.test;

import com.pandamedia.beans.ReportBackingBean;
import com.pandamedia.beans.TrackBackingBean;
import com.pandamedia.commands.ChangeLanguage;
import com.pandamedia.converters.AlbumConverter;
import com.pandamedia.filters.LoginFilter;
import com.pandamedia.utilities.Messages;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.controllers.AlbumJpaController;
import persistence.controllers.ArtistJpaController;
import persistence.controllers.CoverArtJpaController;
import persistence.controllers.GenreJpaController;
import persistence.controllers.ShopUserJpaController;
import persistence.controllers.SongwriterJpaController;
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
    @Inject
    private AlbumJpaController albumController;
    @Inject
    private ArtistJpaController artistController;
    @Inject
    private SongwriterJpaController songwriterController;
    @Inject
    private GenreJpaController genreController;
    @Inject
    private CoverArtJpaController coverArtController;
    
    
    
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
                .addPackage(ChangeLanguage.class.getPackage())
                .addPackage(AlbumConverter.class.getPackage())
                .addPackage(LoginFilter.class.getPackage())
                .addPackage(Messages.class.getPackage())
                .addPackage(ShopUserJpaController.class.getPackage())
                .addPackage(RollbackFailureException.class.getPackage())
                .addPackage(Track.class.getPackage())                
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
        Date date = Calendar.getInstance().getTime();
        short i = 1;
        Track t = trackBacking.findTrackById(1);
        
        t.setTitle("hehe");
        t.setReleaseDate(date);
        t.setPlayLength("1:45");
        t.setDateEntered(date);
        t.setPartOfAlbum(i);
        t.setAlbumTrackNumber(1);
        t.setCostPrice(1.0);
        t.setListPrice(1.5);
        t.setSalePrice(0);
        t.setRemovalStatus(i);
        t.setRemovalDate(date);
        t.setAlbumId(albumController.findAlbum(1));
        t.setArtistId(artistController.findArtist(1));
        t.setSongwriterId(songwriterController.findSongwriter(1));
        t.setGenreId(genreController.findGenre(1));
        t.setCoverArtId(coverArtController.findCoverArt(1));
        
        trackBacking.setTrack(t);
        trackBacking.edit();
        Track editedTrack = trackBacking.findTrackById(1);
        
        boolean isEdited = true;
        
        if(!editedTrack.getTitle().equals(t.getTitle()))
            isEdited = false;
        if(!editedTrack.getReleaseDate().equals(t.getReleaseDate()))
            isEdited=false;
        if(!editedTrack.getPlayLength().equals(t.getPlayLength()))
            isEdited=false;
        if(!editedTrack.getDateEntered().equals(t.getDateEntered()))
            isEdited=false;
        if(editedTrack.getPartOfAlbum() != t.getPartOfAlbum() )
            isEdited=false;
        if(editedTrack.getAlbumTrackNumber() != t.getAlbumTrackNumber())
            isEdited=false;
        if(editedTrack.getCostPrice() != t.getCostPrice())
            isEdited=false;
        if(editedTrack.getListPrice() != t.getListPrice())
            isEdited=false;
        if(editedTrack.getSalePrice() != t.getSalePrice())
            isEdited=false;
        if(editedTrack.getRemovalStatus() != t.getRemovalStatus())
            isEdited=false;
        if(!editedTrack.getRemovalDate().equals(t.getRemovalDate()))
            isEdited=false;
        if(!editedTrack.getAlbumId().equals(t.getAlbumId()))
            isEdited=false;
        if(!editedTrack.getArtistId().equals(t.getArtistId()))
            isEdited=false;
        if(!editedTrack.getSongwriterId().equals(t.getSongwriterId()))
            isEdited=false;
        if(!editedTrack.getGenreId().equals(t.getGenreId()))
            isEdited=false;
        if(!editedTrack.getCoverArtId().equals(t.getCoverArtId()))
            isEdited=false;
        
        assertTrue(isEdited);
    }
    
    @Test
    public void testCreate()
    {
        Date date = Calendar.getInstance().getTime();
        short i = 1;
        Track t = new Track();
        
        t.setTitle("hehe");
        t.setReleaseDate(date);
        t.setPlayLength("1:45");
        t.setDateEntered(date);
        t.setPartOfAlbum(i);
        t.setAlbumTrackNumber(1);
        t.setCostPrice(1.0);
        t.setListPrice(1.5);
        t.setSalePrice(0);
        t.setRemovalStatus(i);
        t.setRemovalDate(date);
        t.setAlbumId(albumController.findAlbum(1));
        t.setArtistId(artistController.findArtist(1));
        t.setSongwriterId(songwriterController.findSongwriter(1));
        t.setGenreId(genreController.findGenre(1));
        t.setCoverArtId(coverArtController.findCoverArt(1));      
        
        trackBacking.setTrack(t);
        List<Track> listBefore = trackBacking.getAll();
        trackBacking.create();
        List<Track> listAfter = trackBacking.getAll();
        assertEquals(listBefore.size() + 1, listAfter.size() );

    }
    
}
