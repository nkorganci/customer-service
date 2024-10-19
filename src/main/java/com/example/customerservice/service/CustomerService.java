package com.example.customerservice.service;

import com.example.customerservice.model.Customer;
import com.example.customerservice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;


    public Customer registerCustomer(Customer customer) {
        // 1. Check if a customer with the same email already exists
//        Customer existingCustomer = customerRepository.findByEmail(customer.getEmail());
//        if (existingCustomer != null) {
//            // 2. If email exists, throw an exception to prevent duplicate registration
//            System.out.println("email exist");
//        }
        // 4. Save the new customer to the database
        return customerRepository.save(customer);

    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
}
