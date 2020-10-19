package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {


    @Autowired
    private AdminDao adminDao;

    @Autowired
    private CommonService commonService;

    /**
     * To validate if the user is having a role of Admin
     * @param accessToken: JWT token
     * @return boolean : true if the user is an admin, false otherwise
     */
    private boolean confirmAdmin(final String accessToken) throws AuthorizationFailedException{
        UserAuthEntity userToken = commonService.commonProfiles(accessToken);
        if(userToken.getUserid().getRole().equals("admin"))
            return true;
        else
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
    }

    /**
     * Business Logic to delete the user
     * @param accessToken : JWT Authorization
     * @param userId : User UUID
     * @return String : User UUId of the deleted User
     * @throws AuthorizationFailedException : the user is not authorized to delete
     * @throws UserNotFoundException: the user doesn't exist in the database
     */
    @Transactional
    public String deleteUser(String accessToken, String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userEntity = commonService.getUserById(userId);
        if(this.confirmAdmin(accessToken)){
            if(commonService.getUserById(userId) == null)
                throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
            else
                adminDao.deleteUser(userId);
        }
        return userId;
    }
}
