package persistence.controllers;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.UserTransaction;
import persistence.entities.ShopUser;

/**
 * Holds custom non CRUD methods to access user records.
 *
 * @author Hau Gilles Che
 */
public class UserActionController {

    @Inject
    private ShopUserJpaController shopUserController;
    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    public UserActionController() {
    }

    public ShopUser findUserByEmail(String email) {
        Query query = em.createNamedQuery("ShopUser.findByEmail");
        query.setParameter("email", email);
        ShopUser user = null;
        try {
            user = (ShopUser) query.getSingleResult();
        } catch (EntityNotFoundException | NonUniqueResultException
                | NoResultException ex) {
            FacesMessage msg = com.pandamedia.utilities.Messages.getMessage(
                    "bundles.messages", "duplicateEmail", null);
            FacesContext.getCurrentInstance().addMessage("loginForm", msg);
        }

        return user;
    }
}
