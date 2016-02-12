package com.dth.service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class EraseWords {
    private EntityManager em;
    
    public EraseWords(EntityManager em) {
        this.em = em;
    }
    
    public void execute() {
        Query query = em.createQuery("DELETE FROM WordOccurrence");
        query.executeUpdate();
    }
}
