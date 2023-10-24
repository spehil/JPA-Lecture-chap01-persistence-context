package com.ohgiraffers.section02.crud;

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class A_EntityManagerCRUDTests {


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

    //JPA코드로 crud작성

    @Test
    public void 메뉴코드로_메뉴_조회_테스트(){

        //메뉴테이블에서 데이터 조회
        //test코드 작성시
        //given:주어진게 무엇인지 작성

        int menuCode = 2;

        //when
        //munu테이블의 엔티티와

        Menu foundMenu= entityManager.find(Menu.class, menuCode);//엔터티클래스, 프라이머리키

        //then
        Assertions.assertNotNull(foundMenu);//Null인지 아닌지 테스트
        Assertions.assertEquals(menuCode, foundMenu.getMenuCode());//2번이라는 메뉴코드와 실제 조회한 메뉴코드와 같은지 조회
        System.out.println("foundMenu =" + foundMenu);
    }

    @Test
    public  void  새로운_메뉴_추가_테스트(){

        //given
        //새로 운메뉴 추가해주기 시퀀스를 이용해서 MenuCode를 이용해서 추가해주고싶다고한다면,
        //Entity(Menu)에 시퀀스 사용설정을 해줘야한다.
        Menu menu = new Menu();
        menu.setMenuName("JPA 테스트용 신규 메뉴");
        menu.setMenuPrice(5000);
        menu.setCategoryCode(4);
        menu.setOrderableStatus("Y");

        //when
        //entityManager에서 트랜젝션을 가져와서 호출을 한다음,
        EntityTransaction entitiyTransaction = entityManager.getTransaction();
        entitiyTransaction.begin();
    try {
        //메뉴라는 엔터티를 저장하겠다.
        entityManager.persist(menu);

        //커밋
        entitiyTransaction.commit();
    }catch (Exception e){
        //저장과 커밋사이에 exception발생되면 rollback해준다.
        entitiyTransaction.rollback();
        e.printStackTrace();
    }
        //then
        //저장이 잘됐으면 insert문과 커밋, 엔터티매니저에 잘 들어갔는지 확인
        Assertions.assertTrue(entityManager.contains(menu));

    }
    @Test
    public void 메뉴_이름_수정_테스트(){

        //given
        Menu menu = entityManager.find(Menu.class,2);//메뉴엔어티의 프라이머리키2를 조회
        System.out.println("menu =" + menu);
        String menuNameToChange = "갈치스무디";

        //when
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        //삽입수정삭제시에는 트랜젝션의 commit rollback처리해줘야한다.
        try{
            menu.setMenuName(menuNameToChange);
            entityTransaction.commit();
        }catch(Exception e){
            entityTransaction.rollback();
            e.printStackTrace();
    }
        //then
        Assertions.assertEquals(menuNameToChange, entityManager.find(Menu.class,2).getMenuName());

    }

    @Test
    public void 메뉴_삭제하기_테스트(){

        //given
        Menu menutoRemove = entityManager.find(Menu.class,1);

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        //삽입수정삭제시에는 트랜젝션의 commit rollback처리해줘야한다.
        try{
            entityManager.remove(menutoRemove);
            entityTransaction.commit();
        }catch(Exception e){
            entityTransaction.rollback();
            e.printStackTrace();
        }

        //then
        Menu removedMenu = entityManager.find(Menu.class,1);
        Assertions.assertEquals(null, removedMenu);
    }

}
