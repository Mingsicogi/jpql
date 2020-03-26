import domain.Address;
import domain.Member;
import domain.Team;
import dto.MemberDTO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

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

        List<Member> dbInfoList = em.createQuery("select m from Member m", Member.class).getResultList();
        dbInfoList.forEach(info -> System.out.println("### " + info.getUsername()));

        Member dbInfo = em.createQuery("select m from Member m where m.username=:username", Member.class)
                .setParameter("username", "minssogi")
                .getSingleResult();
        System.out.println("### " + dbInfo.getUsername());

        System.out.println("\n\n==================================================\n\n");

        dbInfo.setAge(10); // update query가 실행되는걸로 보았을때 조회된 객체는 영속화됨.

        // 엔티티 프로젝션
        Team teamDbInfo = em.createQuery("select t from Member m inner join Team t on m.team.id = t.id and m.username=:username", Team.class)
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

        tx.commit();

        em.close();
        emf.close();
    }
}
