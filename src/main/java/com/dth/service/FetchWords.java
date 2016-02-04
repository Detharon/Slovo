package com.dth.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class FetchWords {
    private EntityManager em;
    
    public FetchWords(EntityManager em) {
        this.em = em;
    }
    
    public List execute(int maxResults) {
        Query query = em.createQuery("SELECT e FROM WordOccurrence e ORDER BY e.count DESC");
        query.setMaxResults(maxResults);
   
        return query.getResultList();
    }
}
