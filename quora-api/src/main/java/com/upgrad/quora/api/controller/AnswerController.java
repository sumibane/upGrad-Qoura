package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerEditResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    /**
     * Controller function for creating new Answer
     * @param questionId : Question Id against which the answer is created
     * @param accessToken: Bearer Token
     * @return AnswerResponse : HTTP Answer Response
     * @throws AuthorizationFailedException : For invalid Access tokens
     * @throws  InvalidQuestionException : For invalid Question ids
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("questionId") final String questionId,
            AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException{

        AnswerEntity answerEntity = answerBusinessService.createAnswer(accessToken, questionId, answerRequest.getAnswer());

        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.id(answerEntity.getUuid());
        answerResponse.status("ANSWER CREATED");
        return new ResponseEntity<>(answerResponse, HttpStatus.OK);
    }

    /**
     * Controller function for editing an answer
     * @param answerId : Answer UUID that needs to be updated
     * @param accessToken: Bearer Token
     * @return AnswerEditResponse : HTTP Answer Response
     * @throws AuthorizationFailedException : For invalid Access tokens
     * @throws  AnswerNotFoundException : For invalid Answer UUId
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(
            @RequestHeader("authorization") final String accessToken, @PathVariable("answerId") final String answerId,
            AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, AnswerNotFoundException{
        AnswerEntity answerEntity = answerBusinessService.updateAnswer(accessToken, answerId, answerEditRequest.getContent());

        AnswerEditResponse answerEditResponse = new AnswerEditResponse();
        answerEditResponse.setId(answerEntity.getUuid());
        answerEditResponse.setStatus("ANSWER EDITED");

        return new ResponseEntity<>(answerEditResponse, HttpStatus.OK);
    }
}
