package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;
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
}
