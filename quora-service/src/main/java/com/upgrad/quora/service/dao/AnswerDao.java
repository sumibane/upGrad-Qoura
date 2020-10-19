package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Database Transaction for persist changes in the ANSWER table
     * @param answerEntity : Model of the Answer Entity
     */
    public void createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
    }
}
