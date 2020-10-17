package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(final UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }

    public Boolean getUserByUserName(final String userName){
        try{
              UserEntity result = entityManager.createNamedQuery("getUserByUserName", UserEntity.class)
                    .setParameter("userName", userName).getSingleResult();
              return true;
        }
        catch (NoResultException nre)
        {
            return false;
        }
    }

    public Boolean getUserByEmail(final String email){
        try{
            UserEntity result = entityManager.createNamedQuery("getUserByEmail", UserEntity.class)
                    .setParameter("email", email).getSingleResult();
            return true;
        }
        catch (NoResultException nre)
        {
            return false;
        }
    }
}
