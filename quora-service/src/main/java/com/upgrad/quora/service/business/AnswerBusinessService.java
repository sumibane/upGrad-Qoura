package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AnswerBusinessService {

    @Autowired
    private CommonService commonService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Autowired
    private AnswerDao answerDao;

    /**
     * Supporting Method to check User LoggedIn Status
     * @param userAuthEntity : Model of the User Authentication Entity
     * @return boolean : true if the user is logged in, false other wise.
     */
    public boolean checkUserSignedIn(UserAuthEntity userAuthEntity){
        boolean result = false;
        if(userAuthEntity != null && userAuthEntity.getLoginAt() != null){
            if(userAuthEntity.getLogoutAt() == null)
                result = true;
        }
        return result;
    }

    /**
     * Service Method to get the create new Answer
     * @param questionId : UUID of the question
     * @param accessToken : Acess Token generated during user Login.
     * @param answerContent : The data received by HTTP Request
     * @return AnswerEntity : Model object of AnswerEntity
     * @throws AuthorizationFailedException : if AUTh token is invalid or not active
     * @throws InvalidQuestionException : if UUId of the question is invalid
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final String accessToken, String questionId, String answerContent) throws AuthorizationFailedException, InvalidQuestionException{
        UserAuthEntity userAuthEntity = commonService.getAuthToken(accessToken);
        AnswerEntity answerEntity = new AnswerEntity();
        if(userAuthEntity != null){
            if(checkUserSignedIn(userAuthEntity)){
                QuestionEntity question = questionBusinessService.getQuestionById(questionId);
                if(question != null){
                    answerEntity.setUuid(UUID.randomUUID().toString());
                    answerEntity.setAnswer(answerContent);
                    answerEntity.setDate(ZonedDateTime.now());
                    answerEntity.setQuestion(question);
                    answerEntity.setUser(userAuthEntity.getUserid());

                    answerDao.createAnswer(answerEntity);
                }
                else
                    throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
            }
            else
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }
        else
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        return answerEntity;
    }
}