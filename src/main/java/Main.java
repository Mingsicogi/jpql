import domain.Address;
import domain.Member;
import domain.Team;
import dto.MemberDTO;

import javax.persistence.*;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("minsMall");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Team team = new Team();
        team.setName("TeamA");
        em.persist(team);

        Member member = new Member();
        member.setUsername("minssogi");
        member.setTeam(team);
        member.setAddress(new Address("seoul", "d", "c"));

        em.persist(member);

        em.flush();
        em.clear();

        Member member2 = new Member();
        member2.setUsername("minssogi2");
        member2.setAge(50);
        member2.setTeam(team);
        member2.setAddress(new Address("seoul", "d", "c"));

        em.persist(member2);

        em.flush();
        em.clear();

        List<Member> dbInfoList = em.createQuery("select m from Member m", Member.class).getResultList();
        dbInfoList.forEach(info -> System.out.println("### " + info.getUsername()));

        Member dbInfo = em.createQuery("select m from Member m where m.username=:username", Member.class)
                .setParameter("username", "minssogi")
                .getSingleResult();
        System.out.println("### " + dbInfo.getUsername());



        System.out.println("\n\n==================================================\n\n");

        dbInfo.setAge(10); // update query가 실행되는걸로 보았을때 조회된 객체는 영속화됨.

        // 엔티티 프로젝션
        Team teamDbInfo = em.createQuery("select m.team from Member m inner join m.team where m.username=:username", Team.class)
                .setParameter("username", "minssogi")
                .getSingleResult();
        System.out.println("### " + teamDbInfo.getName());

        // 임베디드 프로젝션
        Address minssogiAddr = em.createQuery("select m.address from Member m where m.username=?1", Address.class)
                .setParameter(1, "minssogi")
                .getSingleResult();
        System.out.println("### " + minssogiAddr.getCity());

        // 스칼라 타입 프로젝션
        MemberDTO memberDtoInfo = em.createQuery("select new dto.MemberDTO(m.username, m.age) from Member m where m.username =: username", MemberDTO.class)
                .setParameter("username", "minssogi")
                .getSingleResult();
        System.out.println("### " + memberDtoInfo.getAge());




        System.out.println("\n\n==================================================\n\n");

        // 페이징
        Stream.iterate(1, i -> i < 100, i -> i + 1).forEach(i -> {
            Member dbParam = new Member();
            dbParam.setAge(i);
            dbParam.setUsername("minssogi" + i);
            em.persist(dbParam);
        });

        List<Member> dbInfoList2 = em.createQuery("select m from Member m order by m.age desc ", Member.class)
                .setFirstResult(0)
                .setMaxResults(20)
                .getResultList();
        System.out.println("### " + dbInfoList2.size());
        dbInfoList2.forEach(a -> System.out.println(a.toString()));




        System.out.println("\n\n==================================================\n\n");

        // 조인
        List<Member> resultList = em.createQuery("select m from Member m inner join m.team t on t.name = 'teamA'", Member.class)
                .getResultList();
//        resultList.forEach(a -> System.out.println(a.toString()));

        List<Member> resultList2 = em.createQuery("select m from Member m left outer join m.team", Member.class)
                .getResultList();

        List<Member> resultList3 = em.createQuery("select m from Member m, Team t", Member.class)
                .getResultList();



        System.out.println("\n\n==================================================\n\n");
        // sub-query
        List<Member> resultList4 = em.createQuery("select m from Member m where m.age > (select avg(m1.age) from Member m1)", Member.class)
                .getResultList();

        List<Member> resultList5 = em.createQuery("select m from Member m where (select count(o) from Order o where m = o.member) > 0", Member.class)
                .getResultList();

        // 조건식
        List<String> resultList1 = em.createQuery(
                "select " +
                        "   case " +
                        "       when m.age <= 10 then 'student'" +
                        "       when m.age > 60 then 'older'" +
                        "       else '일반'" +
                        "   end " +
                        "from Member m", String.class).getResultList();
        resultList1.forEach(System.out::println);
        // 단순 조건
        List<String> resultList6 = em.createQuery(
                "select " +
                        "   case t.name" +
                        "       when 'TeamA' then 'A'" +
                        "       when 'TeamB' then 'B'" +
                        "       else 'C'" +
                        "   end " +
                        "from Member m inner join Team t on m.team.id = t.id", String.class
        ).getResultList();
        resultList6.forEach(System.out::println);
        // coalesce, nullif
        List<String> s = em.createQuery("select coalesce(m.username, '이름 없음') from Member m", String.class).getResultList();
        s.forEach(System.out::println);
        List<String> resultList7 = em.createQuery("select NULLIF(m.username, 'admin') from Member m", String.class).getResultList();
        resultList7.forEach(System.out::println);

        // jpql basic function
        List<String> singleResult = em.createQuery("select concat('aaa', 'bbb', 'cccc') from Member m", String.class).getResultList();
        singleResult.forEach(System.out::println);

        tx.commit();
        em.close();
        emf.close();
    }
}
