package com.example.multitenancy.partition.web.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.multitenancy.partition.common.AbstractIntegrationTest;
import com.example.multitenancy.partition.config.tenant.TenantIdentifierResolver;
import com.example.multitenancy.partition.entities.Customer;
import com.example.multitenancy.partition.repositories.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

class CustomerControllerIT extends AbstractIntegrationTest {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private TenantIdentifierResolver tenantIdentifierResolver;

    private List<Customer> customerList = null;

    @BeforeEach
    void setUp() {
        tenantIdentifierResolver.setCurrentTenant("dbsystc");
        customerRepository.deleteAll();

        customerList = new ArrayList<>();
        customerList.add(new Customer(null, "First Customer"));
        customerList.add(new Customer(null, "Second Customer"));
        customerList.add(new Customer(null, "Third Customer"));
        customerList = customerRepository.saveAll(customerList);
    }

    @Test
    void shouldFetchAllCustomers() throws Exception {
        this.mockMvc
                .perform(get("/api/customers").param("tenant", "dbsystc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(customerList.size())));
        this.mockMvc
                .perform(get("/api/customers").param("tenant", "dbsystp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    void shouldFindCustomerById() throws Exception {
        Customer customer = customerList.get(0);
        Long customerId = customer.getId();

        this.mockMvc
                .perform(get("/api/customers/{id}", customerId).param("tenant", "dbsystc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(customer.getText())));
    }

    @Test
    void shouldCreateNewCustomer() throws Exception {
        Customer customer = new Customer(null, "New Customer", null);
        long count = this.customerRepository.countByTenant("dbsystc");
        this.mockMvc
                .perform(
                        post("/api/customers")
                                .param("tenant", "dbsystc")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text", is(customer.getText())));
        assertThat(this.customerRepository.countByTenant("dbsystc")).isEqualTo(count + 1);
    }

    @Test
    void shouldReturn400WhenCreateNewCustomerWithoutText() throws Exception {
        Customer customer = new Customer(null, null, null);

        this.mockMvc
                .perform(
                        post("/api/customers")
                                .param("tenant", "dbsystc")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andExpect(jsonPath("$.instance", is("/api/customers")))
                .andReturn();
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        Customer customer = customerList.get(0);
        customer.setText("Updated Customer");

        this.mockMvc
                .perform(
                        put("/api/customers/{id}", customer.getId())
                                .param("tenant", "dbsystc")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(customer.getText())));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        Customer customer = customerList.get(0);
        long count = this.customerRepository.countByTenant("dbsystc");
        this.mockMvc
                .perform(delete("/api/customers/{id}", customer.getId()).param("tenant", "dbsystc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(customer.getText())));
        assertThat(this.customerRepository.countByTenant("dbsystc")).isEqualTo(count - 1);
    }
}
