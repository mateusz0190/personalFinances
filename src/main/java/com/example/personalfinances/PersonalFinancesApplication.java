package com.example.personalfinances;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonalFinancesApplication implements  Runnable{

    public static void main(String[] args) {
        SpringApplication.run(PersonalFinancesApplication.class, args);
    }

    @Override
    public void run() {

    }
}
