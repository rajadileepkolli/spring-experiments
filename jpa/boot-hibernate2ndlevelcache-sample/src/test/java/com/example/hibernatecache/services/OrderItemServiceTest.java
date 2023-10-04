package com.example.hibernatecache.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;

import com.example.hibernatecache.entities.OrderItem;
import com.example.hibernatecache.mapper.Mapper;
import com.example.hibernatecache.model.response.OrderItemResponse;
import com.example.hibernatecache.model.response.PagedResult;
import com.example.hibernatecache.repositories.OrderItemRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock private OrderItemRepository orderItemRepository;
    @Mock private Mapper mapper;

    @InjectMocks private OrderItemService orderItemService;

    @Test
    void findAllOrderItems() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<OrderItem> orderItemPage = new PageImpl<>(List.of(getOrderItem()));
        given(orderItemRepository.findAll(pageable)).willReturn(orderItemPage);
        given(mapper.orderItemListToOrderItemResponseList(List.of(getOrderItem())))
                .willReturn(List.of(getOrderItemResponse()));

        // when
        PagedResult<OrderItemResponse> pagedResult =
                orderItemService.findAllOrderItems(0, 10, "id", "asc");

        // then
        assertThat(pagedResult).isNotNull();
        assertThat(pagedResult.data()).isNotEmpty().hasSize(1);
        assertThat(pagedResult.hasNext()).isFalse();
        assertThat(pagedResult.pageNumber()).isEqualTo(1);
        assertThat(pagedResult.totalPages()).isEqualTo(1);
        assertThat(pagedResult.isFirst()).isTrue();
        assertThat(pagedResult.isLast()).isTrue();
        assertThat(pagedResult.hasPrevious()).isFalse();
        assertThat(pagedResult.totalElements()).isEqualTo(1);
    }

    @Test
    void findOrderItemById() {
        // given
        given(orderItemRepository.findById(1L)).willReturn(Optional.of(getOrderItem()));
        given(mapper.orderItemToOrderItemResponse(getOrderItem()))
                .willReturn(getOrderItemResponse());
        // when
        Optional<OrderItemResponse> optionalOrderItem = orderItemService.findOrderItemById(1L);
        // then
        assertThat(optionalOrderItem).isPresent();
        OrderItemResponse orderItem = optionalOrderItem.get();
        assertThat(orderItem.orderItemId()).isEqualTo(1L);
        assertThat(orderItem.text()).isEqualTo("junitTest");
    }

    @Test
    void saveOrderItem() {
        // given
        given(orderItemRepository.save(getOrderItem())).willReturn(getOrderItem());
        given(mapper.orderItemToOrderItemResponse(getOrderItem()))
                .willReturn(getOrderItemResponse());
        // when
        OrderItemResponse persistedOrderItem = orderItemService.saveOrderItem(getOrderItem());
        // then
        assertThat(persistedOrderItem).isNotNull();
        assertThat(persistedOrderItem.orderItemId()).isEqualTo(1L);
        assertThat(persistedOrderItem.text()).isEqualTo("junitTest");
    }

    @Test
    void deleteOrderItemById() {
        // given
        willDoNothing().given(orderItemRepository).deleteById(1L);
        // when
        orderItemService.deleteOrderItemById(1L);
        // then
        verify(orderItemRepository, times(1)).deleteById(1L);
    }

    private OrderItem getOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setText("junitTest");
        return orderItem;
    }

    private OrderItemResponse getOrderItemResponse() {
        return new OrderItemResponse(1L, "junitTest");
    }
}
