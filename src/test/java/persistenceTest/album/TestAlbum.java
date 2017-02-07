/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistenceTest.album;

import java.util.List;
import org.junit.Test;
import persistence.Album;
import persistence.controllers.AlbumJpaController;

/**
 *
 * @author Evang
 */
public class TestAlbum {
    @Test
    public void testAlbum(){
        AlbumJpaController album_jpa = new AlbumJpaController();
        List<Album> expected = album_jpa.findAlbumEntities(5, 0);
    }
    
    
    
    
}
