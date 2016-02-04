/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dth.service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Piotr
 */
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
