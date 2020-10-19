package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private CommonService commonService;

    /**
     * Business service to create a new Question
     * @param questionEntity : Model object of the QuestionEntity class
     * @return List<QuestionEntity> : List of Question Entities.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        QuestionEntity createdQuestion = questionDao.createQuestion(questionEntity);
        return createdQuestion;
    }

    /**
     * Business service to get all questions
     * @return QuestionEntity : Model object of QuestionEntity class
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(){
        List<QuestionEntity> allQuestions = questionDao.getAllQuestion();
        return allQuestions;
    }

    @Transactional
    public QuestionEntity getQuestionById(String id) throws InvalidQuestionException{
        QuestionEntity questionEntity = questionDao.getQuestionById(id);
        if(questionEntity == null)
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");

        return questionEntity;
    }

    public Boolean isQuestionOwner(UserAuthEntity userAuthEntity, QuestionEntity questionEntity){
        if(questionEntity.getUserId().getUuid().equals(userAuthEntity.getUserid().getUuid()))
            return true;
        else
            return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String editQuestion(String uuid, String questionContent, String accessToken) throws InvalidQuestionException, AuthorizationFailedException{
        QuestionEntity question = getQuestionById(uuid);
        UserAuthEntity userAuthEntity = commonService.commonProfiles(accessToken);
        if(isQuestionOwner(userAuthEntity, question)){
            questionDao.editQuestion(uuid, questionContent);
        }
        else
            throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
        return uuid;
    }
}
