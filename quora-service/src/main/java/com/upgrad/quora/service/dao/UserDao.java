package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Database Operation to create a new user
     * @param userEntity : User object to be created
     * @return UserEntity : New User Object
     */
    public UserEntity createUser(final UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     * Database Operation to search user by UserName
     * @param userName : Username that needs to be searched
     * @return Boolean : True if found, otherwise false.
     */
    public Boolean getUserByUserName(final String userName){
        try{
              entityManager.createNamedQuery("getUserByUserName", UserEntity.class)
                    .setParameter("userName", userName).getSingleResult();
              return true;
        }
        catch (NoResultException nre)
        {
            return false;
        }
    }

    /**
     * Database Operation to search user by user email
     * @param email : User Email that needs to be searched
     * @return Boolean : True if found, otherwise false.
     */
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

    /**
     * Database Operation to search user by UserName
     * @param userName : Username that needs to be searched
     * @return UserEntity if found, null otherwise
     */
    public UserEntity searchUserByUsername(final String userName){
        try{
            return entityManager.createNamedQuery("getUserByUserName", UserEntity.class)
                    .setParameter("userName", userName).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    /**
     * Database Operation to create new Auth Token
     * @param authEntity : UserAuthEntity Token
     * @return UserAuthEntity
     */
    public UserAuthEntity createAuthToken(final UserAuthEntity authEntity){
        entityManager.persist(authEntity);
        return authEntity;
    }

    /**
     * Database Operation to search user by AuthToken
     * @param accessToken : UserAuthEntity Acess token
     * @return UserAuthEntity if found, null otherwise
     */
    public UserAuthEntity getUserAuthToken(final String accessToken){
        try{
            return entityManager.createNamedQuery("authTokenbyAcessToken", UserAuthEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    /**
     * Database Operation update the Logout Time
     * @param userAuthEntity : UserAuthEntity Acess token
     */
    public void updateLogoutTime(UserAuthEntity userAuthEntity){
        entityManager.merge(userAuthEntity);
    }

    /**
     * Database Operation search user by the UUID
     * @param id : User UUID
     * @return UserEntity Model
     */
    public UserEntity getUserById(final String id){
        try{
            return entityManager.createNamedQuery("getUserByUuid", UserEntity.class)
                    .setParameter("uuid", id).getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }
}
