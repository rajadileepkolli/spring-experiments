package com.example.hibernatecache.repositories;

import com.example.hibernatecache.entities.Order;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends BaseJpaRepository<Order, Long>, PagingAndSortingRepository<Order, Long> {

    void deleteAll();
}
