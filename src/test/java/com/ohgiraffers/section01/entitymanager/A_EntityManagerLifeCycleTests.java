package com.ohgiraffers.section01.entitymanager;

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class A_EntityManagerLifeCycleTests {


    private  static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeAll
    public  static void initFactoty(){

        entityManagerFactory = Persistence.createEntityManagerFactory("jpatest");

    }

    @BeforeEach//EntityManager는 매번 만들어져야하므로 test하나가 수행되기 전마다 수행되는 BeforeEach 작성
    public void initManager(){

        entityManager = entityManagerFactory.createEntityManager();

    }

    @AfterAll //BeforeAll과 AfterAll은 static으로 작성한다.
    public  static  void closeFactory(){

        entityManagerFactory.close();
    }


    @AfterEach
    public void closeManager(){
        entityManager.close();
    }

    @Test
    public void 엔터티_매니저_팩토리와_엔터티_매니저_생명주기_확인1(){

        System.out.println("entityManagerFactory.hashCode :" + entityManagerFactory.hashCode());
        System.out.println("entityManger.hashCode :" + entityManager.hashCode());

    }

    @Test
    public void 엔터티_매니저_팩토리와_엔터티_매니저_생명주기_확인2(){

        System.out.println("entityManagerFactory.hashCode :" + entityManagerFactory.hashCode());
        System.out.println("entityManger.hashCode :" + entityManager.hashCode());

    }


}
