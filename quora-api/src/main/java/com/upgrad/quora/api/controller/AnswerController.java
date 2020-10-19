package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
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

import java.util.LinkedList;
import java.util.List;

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

    /**
     * Controller function for delete an answer
     * @param answerId : Answer UUID that needs to be updated
     * @param accessToken: Bearer Token
     * @return AnswerDelete : HTTP Answer Delete Response
     * @throws AuthorizationFailedException : For invalid Access tokens
     * @throws  AnswerNotFoundException : For invalid Answer UUId
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
            @RequestHeader("authorization") final String accessToken, @PathVariable("answerId") final String answerId)
        throws AuthorizationFailedException, AnswerNotFoundException {
        answerBusinessService.deleteAnswer(accessToken, answerId);

        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse();
        answerDeleteResponse.id(answerId);
        answerDeleteResponse.status("ANSWER DELETED");

        return new ResponseEntity<>(answerDeleteResponse, HttpStatus.OK);
    }

    /**
     * Controller function for retrieve answers for a question
     * @param questionId : Part of HHTP Request for the selected question
     * @param accessToken: Bearer Token
     * @return AnswerDelete : HTTP Answer Delete Response
     * @throws AuthorizationFailedException : For invalid Access tokens
     * @throws  InvalidQuestionException : For invalid Question UUID
     */
    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswers(
            @RequestHeader("authorization") final String accessToken, @PathVariable("questionId") final String questionId)
        throws AuthorizationFailedException, InvalidQuestionException{
        List<AnswerEntity> answerEntities = answerBusinessService.getAllAnswer(accessToken, questionId);

        List<AnswerDetailsResponse> answerDetailsResponses = new LinkedList<>();
        if(!answerEntities.isEmpty()){
            for(AnswerEntity answerEntity : answerEntities){
                AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
                answerDetailsResponse.id(answerEntity.getUuid());
                answerDetailsResponse.answerContent(answerEntity.getAnswer());
                answerDetailsResponse.questionContent(answerEntity.getQuestion().getContent());

                answerDetailsResponses.add(answerDetailsResponse);
            }
        }
        return new ResponseEntity<>(answerDetailsResponses, HttpStatus.OK);
    }
}
