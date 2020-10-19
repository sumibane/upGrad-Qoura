package com.upgrad.quora.service.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AdminDao {
    @PersistenceContext
    private EntityManager entityManager;

    public void deleteUser(final String userId){
        try{
            entityManager.createNamedQuery("deleteUserById")
                    .setParameter("uuid", userId).executeUpdate();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
