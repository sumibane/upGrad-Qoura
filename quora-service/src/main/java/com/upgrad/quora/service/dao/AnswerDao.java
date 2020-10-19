package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    /**
     * Database Transaction to retrive answer by their UUID
     * @return AnswerEntity : Model of the Answer Entity
     */
    public AnswerEntity getAnswerForAnswerId(String uuid) {
        try {
            return this.entityManager.createNamedQuery("getAnswerForAnswerId", AnswerEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Database Transaction to update an Answer
     * @param answerEntity : Model of the Answer Entity
     * @return AnswerEntity : Model of the Answer Entity
     */
    public AnswerEntity updateAnswer(AnswerEntity answerEntity){
        return entityManager.merge(answerEntity);
    }

    /**
     * Database Transaction to delete an Answer
     * @param answerEntity : Model of the Answer Entity
     */
    public void deleteAnswer(AnswerEntity answerEntity){
        entityManager.remove(answerEntity);
    }
}
