/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.controllers;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.Invoice;
import persistence.entities.ShopUser;

/**
 *
 * @author Evang
 */
@Named
@RequestScoped
public class InvoiceJpaController implements Serializable {

    @Resource
    private UserTransaction utx;
    private EntityManager em;

    public void create(Invoice invoice) throws RollbackFailureException, Exception {

        try {
            utx.begin();

            ShopUser userId = invoice.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                invoice.setUserId(userId);
            }
            em.persist(invoice);
            if (userId != null) {
                userId.getInvoiceList().add(invoice);
                userId = em.merge(userId);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Invoice invoice) throws NonexistentEntityException, RollbackFailureException, Exception {

        try {
            utx.begin();

            Invoice persistentInvoice = em.find(Invoice.class, invoice.getId());
            ShopUser userIdOld = persistentInvoice.getUserId();
            ShopUser userIdNew = invoice.getUserId();
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                invoice.setUserId(userIdNew);
            }
            invoice = em.merge(invoice);
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getInvoiceList().remove(invoice);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getInvoiceList().add(invoice);
                userIdNew = em.merge(userIdNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = invoice.getId();
                if (findInvoice(id) == null) {
                    throw new NonexistentEntityException("The invoice with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {

        try {
            utx.begin();

            Invoice invoice;
            try {
                invoice = em.getReference(Invoice.class, id);
                invoice.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The invoice with id " + id + " no longer exists.", enfe);
            }
            ShopUser userId = invoice.getUserId();
            if (userId != null) {
                userId.getInvoiceList().remove(invoice);
                userId = em.merge(userId);
            }
            em.remove(invoice);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        }
    }

    public List<Invoice> findInvoiceEntities() {
        return findInvoiceEntities(true, -1, -1);
    }

    public List<Invoice> findInvoiceEntities(int maxResults, int firstResult) {
        return findInvoiceEntities(false, maxResults, firstResult);
    }

    private List<Invoice> findInvoiceEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Invoice.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Invoice findInvoice(Integer id) {
        return em.find(Invoice.class, id);

    }

    public int getInvoiceCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Invoice> rt = cq.from(Invoice.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

}
