import domain.Member;

import javax.persistence.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("minsMall");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Member member = new Member();
        member.setUsername("minssogi");
        em.persist(member);

        em.flush();
        em.clear();

        List<Member> dbInfoList = em.createQuery("select m from Member m", Member.class).getResultList();
        dbInfoList.forEach(info -> System.out.println("### " + info.getUsername()));

        Member dbInfo = em.createQuery("select m from Member m where m.username=:username", Member.class)
                .setParameter("username", "minssogi")
                .getSingleResult();
        System.out.println("### " + dbInfo.getUsername());

        tx.commit();

        em.close();
        emf.close();
    }
}
