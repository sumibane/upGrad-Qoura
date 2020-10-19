package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonService {
    @Autowired
    private UserDao userDao;

    /**
     * Supporting Function to check the validity of User Auth Token
     * @param authorization : ACCESS token in String
     * @return UserAuthEntity : Model object of UserAuthEntity
     * @throws AuthorizationFailedException : if AUTh token is invalid or not active
     */
    public UserAuthEntity commonProfiles(String authorization) throws AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorization);
        if(userAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        if(userAuthEntity.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to get user details");
        return userAuthEntity;
    }

    /**
     * Supporting Function to get User by ID
     * @param userId : User UUID
     * @return UserEntity : Model object of UserEntity
     */
    public UserEntity getUserById(String userId){
        return  userDao.getUserById(userId);
    }
    /**
     * Service Method to get the User profile based on the user UUID
     * @param id : UUID of the user
     * @param authorization : Acess Token generated during user Login.
     * @return UserEntity : Model object of UserEntity
     * @throws AuthorizationFailedException : if AUTh token is invalid or not active
     * @throws UserNotFoundException : if UUID of the user is invalid
     */
    public UserEntity getUserProfile(final String id, final String authorization) throws AuthorizationFailedException, UserNotFoundException{
        //Check Token
        commonProfiles(authorization);

        UserEntity userEntity = getUserById(id);
        if(userEntity == null)
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        return userEntity;
    }
}
