package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
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
     * Supporting Method to get Answer by the Answer UUId
     * @param uuid : Answer UUId whose records needs to be fetched
     * @return AnswerEntity : Model of the User Answer Entity
     */
    @Transactional
    public AnswerEntity getAnswerForAnswerId(String uuid) {
        return answerDao.getAnswerForAnswerId(uuid);
    }

    /**
     * Supporting Method to check if the user is the owner of the answer
     * @param user : Model of the User Answer Entity holding information about User Login
     * @param answerOwner : Model of the User Answer Entity holding information about answer being updated
     * @return boolean : true if the user is the owner of the answer, false otherwise
     */
    public boolean isAnswerOwner(UserEntity user, UserEntity answerOwner){
        if(user.getUuid().equals(answerOwner.getUuid()))
            return true;
        return false;
    }

    /**
     * Supporting Method to check if the user is an admin
     * @param user : Model of the User Answer Entity
     * @return boolean : true if the user is the admin of the answer, false otherwise
     */
    public boolean isUserAdmin(UserEntity user){
        if(user.getRole().equals("admin"))
            return true;
        else
            return false;
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

    /**
     * Service Method to get the update an Answer
     * @param answerId : UUID of the answer requested for updation
     * @param accessToken : Acess Token generated during user Login.
     * @param answerContent : The data received by HTTP Request
     * @return AnswerEntity : Model object of AnswerEntity
     * @throws AuthorizationFailedException : if AUTH token is invalid or not active
     * @throws AnswerNotFoundException : if the Answer is not found in the database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer(final String accessToken, final String answerId, final String answerContent) throws AuthorizationFailedException, AnswerNotFoundException{
        UserAuthEntity userAuthEntity = commonService.getAuthToken(accessToken);
        if(userAuthEntity != null){
            if(checkUserSignedIn(userAuthEntity)){
                AnswerEntity answerEntity = getAnswerForAnswerId(answerId);
                if(answerEntity != null){
                    if(isAnswerOwner(userAuthEntity.getUserid(),answerEntity.getUser())){
                        answerEntity.setAnswer(answerContent);
                        return answerDao.updateAnswer(answerEntity);
                    }
                    else
                        throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
                }
                else
                    throw  new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
            }
            else
                throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to edit an answer");
        }
        else
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
    }

    /**
     * Service Method to get the delete an Answer
     * @param answerId : UUID of the answer requested for updation
     * @param accessToken : Acess Token generated during user Login.
     * @throws AuthorizationFailedException : if AUTH token is invalid or not active
     * @throws AnswerNotFoundException : if the Answer is not found in the database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(final String accessToken, final String answerId) throws AuthorizationFailedException, AnswerNotFoundException{
        UserAuthEntity userAuthEntity = commonService.getAuthToken(accessToken);
        if(userAuthEntity != null){
            if(checkUserSignedIn(userAuthEntity)){
                AnswerEntity answerEntity = getAnswerForAnswerId(answerId);
                if(answerEntity != null){
                    if(isAnswerOwner(userAuthEntity.getUserid(), answerEntity.getUser()) || isUserAdmin(userAuthEntity.getUserid())){
                        answerDao.deleteAnswer(answerEntity);
                    }
                    else
                        throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
                }
                else
                    throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
            }
            else
                throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to delete an answer");
        }
        else
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
    }
}
