package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    /**
     * Support Function to generate the List of QuestionDetailResponse
     * TO provide code Reusability
     * @param allQuestions : A List of Question Entity
     * @return List<QuestionDetailsResponse> : A linked list of HTTP Response
     */
    private List<QuestionDetailsResponse> buildQuestionDetailsResponseList(List<QuestionEntity> allQuestions){
        //Create a LinkedList to save all the questions
        List<QuestionDetailsResponse> questionList = new LinkedList<>();

        for(QuestionEntity question: allQuestions){
            //Create a single Question Response
            QuestionDetailsResponse questionDetails = new QuestionDetailsResponse();
            questionDetails.setId(question.getUuid());
            questionDetails.setContent(question.getContent());
            //Add the Question response to the Linked List
            questionList.add(questionDetails);
        }
        return  questionList;
    }

    /**
     * Controller to create new Question
     * @param questionRequest : HTTP Request
     * @param accessToken : Bearer Authentication
     * @return QuestionResponse : HTTP Response
     * @throws AuthorizationFailedException : if AUTh token is invalid or not active
     */
    @RequestMapping(method = RequestMethod.POST, path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(
            @RequestHeader("authorization") final String accessToken,
            final QuestionRequest questionRequest) throws AuthorizationFailedException{

        UserAuthEntity userAuthEntity = commonService.commonProfiles(accessToken);
        //Create a blank QuestionEntity object to persist in DB
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUserId(userAuthEntity.getUserid());
        //DB persist
        QuestionEntity question = questionBusinessService.createQuestion(questionEntity);
        //HTTP Response Model created for the QuestionResponse
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.id(question.getUuid());
        questionResponse.status("QUESTION CREATED");

        return new ResponseEntity<>(questionResponse, HttpStatus.CREATED);
    }

    /**
     * Controller to get all questions
     * @param accessToken : Bearer Authentication
     * @return QuestionResponse : List of HTTP Response
     * @throws AuthorizationFailedException : if AUTh token is invalid or not active
     */
    @RequestMapping(method = RequestMethod.GET, path = "/all" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String accessToken)throws AuthorizationFailedException{

        //Check the validity of the BearerToken
        commonService.commonProfiles(accessToken);
        //Retrieve the collections and build a Linked List of QuestionDetailsResponse
        List<QuestionEntity> allQuestions = questionBusinessService.getAllQuestions();
        List<QuestionDetailsResponse> questionDetailsResponses = buildQuestionDetailsResponseList(allQuestions);

        return new ResponseEntity<>(questionDetailsResponses,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion
            (@RequestHeader("authorization") final String accessToken,@PathVariable("questionId") final String questionId
            ,final QuestionEditRequest questionEditRequest) throws InvalidQuestionException, AuthorizationFailedException{
        questionBusinessService.editQuestion(questionId, questionEditRequest.getContent(),accessToken);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse();
        questionEditResponse.id(questionId);
        questionEditResponse.status("QUESTION EDITED");

        return new ResponseEntity<>(questionEditResponse, HttpStatus.OK);
    }
}
