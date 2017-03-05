package persistence.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Erika Bourque
 */
@Entity
@Table(name = "front_page_settings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FrontPageSettings.findAll", query = "SELECT f FROM FrontPageSettings f")
    , @NamedQuery(name = "FrontPageSettings.findById", query = "SELECT f FROM FrontPageSettings f WHERE f.id = :id")})
public class FrontPageSettings implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "survey_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Survey surveyId;
    @JoinColumn(name = "newsfeed_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Newsfeed newsfeedId;
    @JoinColumn(name = "advert_a_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Advertisement advertAId;

    public FrontPageSettings() {
    }

    public FrontPageSettings(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Survey getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Survey surveyId) {
        this.surveyId = surveyId;
    }

    public Newsfeed getNewsfeedId() {
        return newsfeedId;
    }

    public void setNewsfeedId(Newsfeed newsfeedId) {
        this.newsfeedId = newsfeedId;
    }

    public Advertisement getAdvertAId() {
        return advertAId;
    }

    public void setAdvertAId(Advertisement advertAId) {
        this.advertAId = advertAId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FrontPageSettings)) {
            return false;
        }
        FrontPageSettings other = (FrontPageSettings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.entities.FrontPageSettings[ id=" + id + " ]";
    }
    
}
