package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

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


    /**
     * The method will validate the user name and emil and login if both are correct
     * @param userName : Decrypted Username
     *        password : Decrypted Password
     * @return UserAuthEntitiy : Authenication Token Entity
     * @throws AuthenticationFailedException only if authentication fails
     */
    @Transactional
    public UserAuthEntity signin(final String userName, final String password) throws AuthenticationFailedException{
        UserEntity userEntity = userDao.searchUserByUsername(userName);
        if(userEntity == null)
         throw new AuthenticationFailedException("ATH-001", "This username does not exists");

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiry = now.plusHours(8);

            UserAuthEntity authUser = new UserAuthEntity();
            authUser.setUuid(UUID.randomUUID().toString());
            authUser.setUserid(userEntity);
            authUser.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiry));
            authUser.setLoginAt(now);
            authUser.setExpiresAt(expiry);

            userDao.createAuthToken(authUser);

            return authUser;
        }
        else
            throw new AuthenticationFailedException("ATH-002","Password Failed");
    }
}
