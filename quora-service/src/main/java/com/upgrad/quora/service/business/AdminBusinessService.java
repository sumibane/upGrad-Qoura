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

    private boolean confirmAdmin(final String accessToken) throws AuthorizationFailedException{
        UserAuthEntity userToken = commonService.commonProfiles(accessToken);
        if(userToken.getUserid().getRole().equals("admin"))
            return true;
        else
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
    }

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
