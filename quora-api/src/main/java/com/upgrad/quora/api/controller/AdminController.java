package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminBusinessService adminBusinessService;

    @RequestMapping(method = RequestMethod.DELETE , path = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(
            @PathVariable("userId") final String userId,
            @RequestHeader("authorization") final String authorization)
    throws AuthorizationFailedException, UserNotFoundException {
       String deletedUserId = adminBusinessService.deleteUser(authorization,userId);

       UserDeleteResponse userDeleteResponse = new UserDeleteResponse();
       userDeleteResponse.id(deletedUserId);
       userDeleteResponse.status("USER SUCCESSFULLY DELETED");
       return new ResponseEntity<>(userDeleteResponse, HttpStatus.OK);
    }
}
