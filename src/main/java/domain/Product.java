package domain;

import javax.persistence.*;

@Entity
public class Product {

    @Id @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    private String name;
    private int price;
    private int stockAmount;

    @OneToOne(mappedBy = "product")
    private Order order;
}
