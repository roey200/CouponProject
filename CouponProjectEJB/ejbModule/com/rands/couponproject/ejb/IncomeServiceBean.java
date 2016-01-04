package com.rands.couponproject.ejb;

import java.util.Collection;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.rands.couponproject.jpa.Income;
import com.rands.couponproject.jpa.IncomeType;


/**
 * Session Bean implementation class IncomeServiceBean
 */
@Stateless
@LocalBean
public class IncomeServiceBean {
	
	@PersistenceContext(unitName = "couponSystem")
	private EntityManager em;
	
	static Logger logger = Logger.getLogger(IncomeServiceBean.class);

    public IncomeServiceBean() {
        // TODO Auto-generated constructor stub
    }
    
	public void StoreIncome(Income income) {
		logger.debug("StoreIncome " + income);
		em.persist(income);
		logger.info("StoreIncome " + income); // should have the id
	}

	public Collection<Income> viewAllIncome() {
		logger.debug("viewAllIncome");
		Query query;
		query = em.createQuery("SELECT i FROM Income AS i");
		List<Income> rslt = query.getResultList();
		logger.debug("viewAllIncome ResultList size = " + rslt.size());
		return rslt;
	}

	public Collection<Income> viewIncomeByName(String name) {
		logger.debug("viewIncomeByName : name = " + name);
		Query query;
		query = em.createQuery("SELECT i FROM Income AS i WHERE i.name =?1");
		query.setParameter(1, name);
		List<Income> rslt = query.getResultList();
		logger.debug("viewIncomeByName ResultList size = " + rslt.size());
		return rslt;
	}    


	public Collection<Income> viewIncomeByCustomer(String name) {
		logger.debug("viewIncomeByCustomer : name = " + name);
		
		String jpql = "SELECT i FROM Income AS i WHERE i.name =?1 and i.description = ?2";
		Query query = em.createQuery(jpql);
		query.setParameter(1, name);
		query.setParameter(2, IncomeType.CUSTOMER_PURCHASE);
		List<Income> rslt = query.getResultList();
		logger.debug("viewIncomeByCustomer ResultList size = " + rslt.size());
		return rslt;
	}
	
	public Collection<Income> viewIncomeByCompany(String name) {
		logger.debug("viewIncomeByCompany : name = " + name);
		String jpql = "SELECT i FROM Income AS i WHERE i.name =?1 and (i.description = ?2 or i.description = ?3)";
		Query query = em.createQuery(jpql);
		query.setParameter(1, name);
		query.setParameter(2, IncomeType.COMPANY_CREATE_COUPON);
		query.setParameter(3, IncomeType.COMPANT_UPDATE_COUPON);
		List<Income> rslt = query.getResultList();
		logger.debug("viewIncomeByCompany ResultList size = " + rslt.size());
		return rslt;
	}    
}
