package domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;

    @OneToMany(mappedBy = "member") // 양방향 설정
    private List<Order> orders = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}
