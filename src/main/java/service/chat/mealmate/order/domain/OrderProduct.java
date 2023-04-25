package service.chat.mealmate.order.domain;

import websocket.spring_websocket.product.domain.Product;

import javax.persistence.*;

@Entity
public class OrderProduct {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderProductId;

    private Long quantity;

    // 구매 당시의 상품 가격을 나타냅니다 (상품의 가격은 const가 아니니까)
    private Long orderPrice;

    @ManyToOne @JoinColumn(name = "orders_id")
    private Orders orders;

    @ManyToOne @JoinColumn(name = "product_id")
    private Product product;
}
