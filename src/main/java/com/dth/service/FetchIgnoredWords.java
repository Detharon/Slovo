package com.dth.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class FetchIgnoredWords {
    private EntityManager em;
    
    public FetchIgnoredWords(EntityManager em) {
        this.em = em;
    }
    
    public List execute(int maxResults) {
        Query query = em.createQuery("SELECT e FROM IgnoreList e");
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }
}
