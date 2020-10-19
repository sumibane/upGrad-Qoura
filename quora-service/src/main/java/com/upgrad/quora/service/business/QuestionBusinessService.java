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

    /**
     * Helper function to retrieve Question by their UID
     * To increase code re-usability
     * @param id : Question Id to be searched
     * @return QuestionEntity : Model object of QuestionEntity class
     * @throws InvalidQuestionException : if the question Uid or role is doesn't match
     */
    @Transactional
    public QuestionEntity getQuestionById(String id) throws InvalidQuestionException{
        QuestionEntity questionEntity = questionDao.getQuestionById(id);
        if(questionEntity == null)
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");

        return questionEntity;
    }

    /**
     * Helper function to validated the owner of a question
     * To increase code re-usability
     * @param userAuthEntity : Model Object of UserAuthEntity class
     * @param  questionEntity : Model object of QuestionEntity class
     * @return boolean : True if the owner is same, false otherwise
     */
    public Boolean isQuestionOwner(UserAuthEntity userAuthEntity, QuestionEntity questionEntity){
        if(questionEntity.getUserId().getUuid().equals(userAuthEntity.getUserid().getUuid()))
            return true;
        else
            return false;
    }

    /**
     * Business service to edit specified question
     * @param uuid : Question Id for the question that is being edited
     * @param accessToken : Bearer Auth token
     * @return String : UUID of the question edited
     * @throws AuthorizationFailedException : if AUTh token is invalid or not active
     * @throws InvalidQuestionException : if the question Uid or role is doesn't match
     */
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

    /**
     * Business service to delete specified question
     * @param uuid : Question Id for the question that is being edited
     * @param accessToken : Bearer Auth token
     * @throws AuthorizationFailedException : if AUTh token is invalid or not active
     * @throws InvalidQuestionException : if the question Uid or role is doesn't match
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(String accessToken, String uuid) throws AuthorizationFailedException, InvalidQuestionException{
        QuestionEntity questionEntity = getQuestionById(uuid);
        UserAuthEntity userAuthEntity = commonService.commonProfiles(accessToken);

        final String userRole = userAuthEntity.getUserid().getRole();
        if(isQuestionOwner(userAuthEntity, questionEntity) || userRole.equals("admin")){
            questionDao.deleteQuestion(uuid);
        }
        else{
            throw new AuthorizationFailedException("ATH-003", "Only the question owner or admin can delete the question");
        }
    }
}
