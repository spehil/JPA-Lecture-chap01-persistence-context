package com.ohgiraffers.section03.persistencecontext;

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

public class A_EntityLifeCycleTests {

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
    public void 비영속성_테스트(){

        //given
        Menu foundMenu = entityManager.find(Menu.class,11);//foundMenu:영속엔티티이다.-관리되고있는 영속엔티티
        Menu newMenu = new Menu(); //newMenu:비영속성 엔티티
        newMenu.setMenuCode(foundMenu.getMenuCode());
        newMenu.setMenuName(foundMenu.getMenuName());
        newMenu.setMenuPrice(foundMenu.getMenuPrice());
        newMenu.setCategoryCode(foundMenu.getCategoryCode());
        newMenu.setOrderableStatus(foundMenu.getOrderableStatus());//객체의 필드값이 동일해짐

        //when
        boolean isTrue = (foundMenu == newMenu);//객체의 주소값을 비교하는것으로 false가나온다. 각각의 객체의 주소값이 다르다.

        //then
        assertFalse(isTrue);//(클래스명을 생략하고) static import해줌import static org.junit.jupiter.api.Assertions.assertFalse;

        //새로운 엔티티는 xml파일에 등록을 해줘야한다!!! 중요
    }

    @Test
    public void 영속성_연속_조회_테스트(){

        //given
         Menu fouondMenu1 = entityManager.find(Menu.class , 11);//selecte동작이 1번일어나고
         Menu foundMenu2 = entityManager.find(Menu.class, 11);//1차 캐시에 것을 찾아서 똑같은 키값은 같게 반환된다.

         //when
        boolean isTrue = (fouondMenu1 == foundMenu2);//같은 객체로 해시코드또한 같음. 이름만 다른것이지 처음에는 select로 만들어서 가져온것이고 두번째는 1차캐시를 확인해 가져오는것이다.
        //then
        assertTrue(isTrue);//같음을 알수 있고 한번만 select동작이 일어났다.
    }

    @Test
    public  void 영속성_객체_추가_테스트(){
        //given
        Menu menuToRegist = new Menu();
        menuToRegist.setMenuCode(500);
        menuToRegist.setMenuName("수박죽");
        menuToRegist.setMenuPrice(10000);
        menuToRegist.setCategoryCode(1);
        menuToRegist.setOrderableStatus("Y");

        //when
        entityManager.persist(menuToRegist);
        Menu foundMenu = entityManager.find(Menu.class, 500);//관리되는 엔터티가 존재하므로 1차캐시에서 가져온다.
        boolean isTrue = (menuToRegist == foundMenu);


    }

    @Test
    public  void 영속성_객체_추가_값_변경_테스트(){

        //given 비영속객체 생성
        Menu menuToRegist = new Menu();
        menuToRegist.setMenuCode(500);
        menuToRegist.setMenuName("수박죽");
        menuToRegist.setMenuPrice(10000);
        menuToRegist.setCategoryCode(1);
        menuToRegist.setOrderableStatus("Y");

        //when
        entityManager.persist(menuToRegist);//관리되는 영속객체로 만든다.(1차캐시에 저장) ,persist는 관리되는엔티티가 됐다는것이다.
        menuToRegist.setMenuName("메론죽"); //그걸 바꾼다는것은 관리되는 엔터티를 바꾼다는것임.
        Menu foundMenu = entityManager.find(Menu.class, 500);

        //then
        assertEquals("메론죽", foundMenu.getMenuName());

    }

    @Test
    public void 준영속_detach_테스트(){

        //given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);
        Menu foundMenu2 = entityManager.find(Menu.class, 12);

        //when
        entityManager.detach(foundMenu2);//준영속상태로만든다 detach(); ->1차 캐시 안에서 제거가 된다.
        foundMenu1.setMenuPrice(5000);
        foundMenu2.setMenuPrice(5000);

        //then
        assertEquals(5000, entityManager.find(Menu.class, 11).getMenuPrice());//True 1차캐시를 보니까 존재해서 해당 객체의 주소값의 값이 반환
        assertEquals(5000, entityManager.find(Menu.class, 12).getMenuPrice());//False 1차캐시에 없으므로 DB에서 다시 조회해서(select구문으로 조회) 새로운 객체를 만들어서 영속성컨텍스트에 1차캐시에 생성해준다. 이건 새로생성된것으로 아까 5000값으로 변경해준 객체와 다르므로 False //DB에서 select조회해옴

    }

    @Test
    public void 준영속성_clear_테스트(){

        //given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);

        Menu foundMenu2 = entityManager.find(Menu.class, 12);

        //when
        entityManager.clear();//영속성 클래스를 clear()하겠다는것으로 준영속상태로! 객체가 메모리상에 존재하지만, 영속성컨텍스트에서는 더이상 관리하지 않는 상태가 된다.
        foundMenu1.setMenuPrice(5000);
        foundMenu2.setMenuPrice(5000);

        //then
        assertEquals(5000, entityManager.find(Menu.class, 11).getMenuPrice());//여기에서 오류가 나서 다음코드는 실행이 안됨.
        assertEquals(5000, entityManager.find(Menu.class, 12).getMenuPrice());
    }

    @Test
    public void 준영속성_close_테스트(){

        //given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);

        Menu foundMenu2 = entityManager.find(Menu.class, 12);

        //when
        entityManager.close();//
        foundMenu1.setMenuPrice(5000);
        foundMenu2.setMenuPrice(5000);

        //then
        assertEquals(5000, entityManager.find(Menu.class, 11).getMenuPrice());//entityManager가 닫혀서 에러가남.
        assertEquals(5000, entityManager.find(Menu.class, 12).getMenuPrice());
    }

    @Test
    public void 삭제_remove_테스트(){

        //given
        Menu foundMenu = entityManager.find(Menu.class, 2);
        //when
        entityManager.remove(foundMenu);//삭제-객체를 메모리상에서 삭제한다는것이 아니라, 더이상 영속성컨텍스트에서 찾을수 없음(find로 해당값을 다시 select해서 찾아올수가 없음 detach와의 차이점.)
        Menu refoundMenu = entityManager.find(Menu.class,2);//refoundMenu에는 null값 //remove는 온전히 삭제한다는것. detach와 다름(detach는 다시DB에서 select조회를 하는데 remove는 다시 select하지않음)
        //then
        assertEquals(2, foundMenu.getMenuCode());
        assertEquals(null, refoundMenu);
    }

    @Test
    public  void 병합_merge_수정_테스트(){

        //given
        Menu menuToDetach = entityManager.find(Menu.class, 2);//갈치스무디
        entityManager.detach(menuToDetach);//준영속상태로 만들었음
        //when
        menuToDetach.setMenuName("수박죽");
        Menu refoundMenu = entityManager.find(Menu.class,2);//detach이므로 새로운 객체를 생성
        entityManager.merge(menuToDetach);//merge: 해당객체(새로운객체)에 존재하는값을 모두 merge한다.
        //then
        Menu mergeMenu = entityManager.find(Menu.class, 2);
        assertEquals("수박죽", mergeMenu.getMenuName());
    }

    @Test
    public void 병합_merge_삽입_테스트(){
        //given
        Menu menuToDetach = entityManager.find(Menu.class, 2);//갈치스무디
        entityManager.detach(menuToDetach);//준영속상태로 만들었음
        //when
        menuToDetach.setMenuCode(999);//DB에 조회할수 없는 키값으로 변경에서 해보자!
        menuToDetach.setMenuName("수박죽");
        entityManager.merge(menuToDetach);//merge: 해당객체에 존재하는값을 모두 merge(덮어쓰기)한다.근데 detach한값이 존재하지 않는값에는 insert된다고생각하면된다.
        //영속상태의 엔티티와 병합해야 하지만 존재하지 않을 경우 삽입된다!!
        //트랜잭션은하지 않아서 DB에 insert된것은 아니지만, 영속성텍스트에 관리하는 엔티티가 된다.


        //then
        Menu mergeMenu = entityManager.find(Menu.class, 999);
        assertEquals("수박죽", mergeMenu.getMenuName());

    }
}
