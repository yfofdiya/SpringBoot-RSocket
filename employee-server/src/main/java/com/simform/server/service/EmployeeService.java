package com.simform.server.service;

import com.simform.server.entity.Employee;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    private static final List<Employee> employees = new ArrayList<>();

    static {
        employees.add(new Employee(101, "Test", 10000));
        employees.add(new Employee(102, "Dummy", 5000));
        employees.add(new Employee(103, "Example", 50000));
    }

    public Mono<Employee> retrieveEmployeeById(int empId) {
        return Mono.justOrEmpty(employees.stream()
                .filter(e -> e.getEmpId() == empId)
                .findFirst()
        );
    }

    public Employee addEmployee(Employee employee) {
        employees.add(employee);
        return employee;
    }

    public Flux<Employee> retrieveEmployees() {
        return Flux
                .fromIterable(employees)
                .delayElements(Duration.ofSeconds(2));
    }

    public Mono<Employee> retrieveEmployeeByName(String username) {
        return Mono.justOrEmpty(employees.stream()
                .filter(e -> e.getEmpName().equalsIgnoreCase(username))
                .findFirst()
        );
    }
}
