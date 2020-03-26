package domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"orders", "team"})
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") // 양방향 설정
    private List<Order> orders = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;


}
