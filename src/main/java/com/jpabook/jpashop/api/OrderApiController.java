package com.jpabook.jpashop.api;


import com.jpabook.jpashop.domain.Address;
import com.jpabook.jpashop.domain.Order;
import com.jpabook.jpashop.domain.OrderItem;
import com.jpabook.jpashop.domain.OrderStatus;
import com.jpabook.jpashop.repository.OrderRepository;
import com.jpabook.jpashop.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;


    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());


        }
        return all;

    }

    @GetMapping("/api/v2/orders") // 쿼리가 많이 나감 -> 최적화 필요
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
            .map(o -> new OrderDto(o))
            .collect(Collectors.toList());
        return result;

    }


    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        for (Order order : orders) {
            System.out.println("order ref=" + order + "id " + order.getId());

        }
        List<OrderDto> result = orders.stream()
            .map(o -> new OrderDto(o))
            .collect(Collectors.toList());

        return result;
    }


    /**
     * V3.1: 페이징 + 성능 최적화 조합 전략
     *
     * - ToOne 관계(member, delivery)는 fetch join 사용 → 페이징 가능
     * - ToMany(orderItems, item)는 지연 로딩 + default_batch_fetch_size or @BatchSize로 N+1 완화
     * - default_batch_fetch_size는 100~1000 사이 추천 (DB IN 절 제한 고려)
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit); // ToOne 관계 fetch join
        List<OrderDto> result = orders.stream()
            .map(o -> new OrderDto(o))
            .collect(Collectors.toList());

        return result;
    }
    /**
     *결론
     * ToOne 관계는 페치 조인해도 페이징에 영향을 주지 않는다. 따라서 ToOne 관계는 페치조인으로 쿼리 수
     * 를 줄이고 해결하고, 나머지는 `hibernate.default_batch_fetch_size`로 최적화 하자.
     */

    /**
     * 참고: `default_batch_fetch_size` 의 크기는 적당한 사이즈를 골라야 하는데, 100~1000 사이를 선택하는
     * 것을 권장한다. 이 전략을 SQL IN 절을 사용하는데, 데이터베이스에 따라 IN 절 파라미터를 1000으로 제한하기
     * 도 한다. 1000으로 잡으면 한번에 1000개를 DB에서 애플리케이션에 불러오므로 DB에 순간 부하가 증가할 수
     * 있다. 하지만 애플리케이션은 100이든 1000이든 결국 전체 데이터를 로딩해야 하므로 메모리 사용량이 같다.
     * 1000으로 설정하는 것이 성능상 가장 좋지만, 결국 DB든 애플리케이션이든 순간 부하를 어디까지 견딜 수 있는
     * 지로 결정하면 된다.
     *
     */


    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;


        public OrderDto(Order order) {

            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();

            orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());

        }
    }

    @Getter
    static class OrderItemDto {
        private String itemname; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량


        public OrderItemDto(OrderItem orderItem) {

            itemname = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();

        }
    }

}
