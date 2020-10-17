package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /**
     * The method will create a new user only if UserName and Email doesn't exist
     * @param userEntity : User object to be created
     * @return UserEntity : New User Object
     * @throws SignUpRestrictedException only if validation fails
     */
    @Transactional
    public UserEntity signUp(UserEntity userEntity) throws SignUpRestrictedException{
        //Check if the userName exists before creating
        if(userDao.getUserByUserName(userEntity.getUserName())){
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }
        //Check if the userEmail exists before creating
        if(userDao.getUserByEmail(userEntity.getEmail())){
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
        //Else encrypt the password for the new user
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        //Persist the user data
        UserEntity signupUser = userDao.createUser(userEntity);
        return signupUser;
    }

}
