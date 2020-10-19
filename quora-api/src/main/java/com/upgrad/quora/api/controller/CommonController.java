package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {
    @Autowired
    private CommonService commonService;

    /**
     * Controller Method to view User Profile details based on the User UUID
     * @param userId : UUID of the user
     * @param authorization : Acess Token generated during user Login.
     * @return UserDetailsResponse : Models all the user profile details
     * @throws AuthorizationFailedException : if AUTh token is invalid or not active
     * @throws UserNotFoundException : if UUID of the user is invalid
     * @author Jithesh Nanat
     */
    @RequestMapping(method = RequestMethod.GET , path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetailsResponse> loadUserProfile(@PathVariable("userId") final String userId,
    @RequestHeader("authorization") final String authorization) throws UserNotFoundException, AuthorizationFailedException {

        UserEntity userEntity = commonService.getUserProfile(userId, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        userDetailsResponse.firstName(userEntity.getFirstName());
        userDetailsResponse.lastName(userEntity.getLastName());
        userDetailsResponse.userName(userEntity.getUserName());
        userDetailsResponse.aboutMe(userEntity.getAboutMe());
        userDetailsResponse.contactNumber(userEntity.getContactNumber());
        userDetailsResponse.dob(userEntity.getDob());
        userDetailsResponse.country(userEntity.getCountry());
        userDetailsResponse.emailAddress(userEntity.getEmail());

        return new ResponseEntity<>(userDetailsResponse, HttpStatus.OK);
    }
}
