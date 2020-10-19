package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;
    /**
     * Database Transaction for persisting the new question record
     * @param questionEntity : Model object of the QuestionEntity class
     * @return QuestionEntity : Model object of QuestionEntity class
     */
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        try{
            entityManager.persist(questionEntity);
            return questionEntity;
        }
        catch (Exception e){
            return null;
        }
    }

    /**
     * Database Transaction for retireve all Questions
     * @return QuestionEntity : Result List of QuestionEntity
     */
    public List<QuestionEntity> getAllQuestion(){
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class)
                    .getResultList();
        }
        catch (NoResultException nre){
            return null;
        }
    }
}
