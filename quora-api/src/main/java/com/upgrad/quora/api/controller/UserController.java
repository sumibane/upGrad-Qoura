package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

    /*
     * Sign up method called for the endpoint "/user/signup"
     * */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SignupUserResponse> signup (final SignupUserRequest request) throws SignUpRestrictedException{

        //Create an empty Object
        UserEntity userEntity = new UserEntity();
        //Fill in the object with the RequestModel
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(request.getFirstName());
        userEntity.setLastName(request.getLastName());
        userEntity.setUserName(request.getUserName());
        userEntity.setEmail(request.getEmailAddress());
        userEntity.setPassword(request.getPassword());
        userEntity.setCountry(request.getCountry());
        userEntity.setAboutMe(request.getAboutMe());
        userEntity.setDob(request.getDob());
        userEntity.setContactNumber(request.getContactNumber());

        //Create the user
        final UserEntity createdUser = userBusinessService.signUp(userEntity);
        //Create the payload
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUser.getUuid())
                .status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }
}
