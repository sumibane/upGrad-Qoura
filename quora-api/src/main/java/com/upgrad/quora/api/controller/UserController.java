package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

    /**
     * Controller Method for User Signup Function
     * @param request : HTTP User Request
     * @return SingupUserResponse : HTTP User Response
     * @throws SignUpRestrictedException
     */
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


    /**
     * Controller Method for User Sign in Function
     * @param authorization : HTTP Authorization header
     * @return SigninResponse : HTTP Signin Response
     * @throws AuthenticationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException{
        //Base64 decoder and remove "Basic "
        byte[] decoded = Base64.getDecoder().decode(authorization.split(" ")[1]);
        String decodedText = new String(decoded);
        //Extract the username and array by splitting the decoded text using :
        String[] split = decodedText.split(":");
        UserAuthEntity userAuthEntity = userBusinessService.signin(split[0], split[1]);
        //Create new singinResponse Object
        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(userAuthEntity.getUserid().getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");
        //Create new Header to attach with the payload
        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token",userAuthEntity.getAccessToken());
        return  new ResponseEntity<>(signinResponse,headers,HttpStatus.OK);
    }


    /**
     * Controller Method for User Signout Function
     * @param authorization : HTTP Authorization header
     * @return SignoutResponse : HTTP Singout Response
     * @throws SignOutRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signout")
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException{

        final UserEntity userEntity = userBusinessService.signout(authorization);

        SignoutResponse signoutResponse = new SignoutResponse().id(userEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<>(signoutResponse,HttpStatus.OK);
    }
}
