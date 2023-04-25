package service.chat.mealmate.product.domain;

import javax.persistence.*;

@Entity
public class ProductCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productCategoryId;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Category category;
}
