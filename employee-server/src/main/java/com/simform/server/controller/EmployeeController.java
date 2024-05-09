package com.simform.server.controller;

import com.simform.server.entity.Employee;
import com.simform.server.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @MessageMapping("handleError")
    public Mono<String> handleError(int empId) {
        Mono<Employee> employee = employeeService.retrieveEmployeeById(empId);
        return employee
                .switchIfEmpty(Mono.error(new RuntimeException("Employee not found with id " + empId)))
                .flatMap(emp -> Mono.just("Employee found with id " + empId));
    }

    @MessageExceptionHandler(RuntimeException.class)
    public Mono<RuntimeException> exceptionHandler(RuntimeException e) {
        log.error(e.getMessage());
        return Mono.error(e);
    }

    @MessageMapping("authenticate")
    public Mono<Employee> handleAuthentication(@AuthenticationPrincipal Mono<UserDetails> user) {
        return user.flatMap(userDetails -> {
            String username = userDetails.getUsername();
            return employeeService.retrieveEmployeeByName(username);
        });
    }

    @MessageMapping("allEmployees")
    public Flux<Employee> retrieveEmployees() {
        return employeeService.retrieveEmployees();
    }

    @MessageMapping("newEmployee")
    public Mono<Void> addEmployee(Employee employee) {
        Employee emp = employeeService.addEmployee(employee);
        log.info("New Employee with ID : {}, Name: {}, Salary {} ", emp.getEmpId(), emp.getEmpName(), emp.getEmpSalary());
        return Mono.empty();
    }

    @MessageMapping("employeeDataById")
    public Mono<Employee> retrieveEmployeeDataById(int empId) {
        return employeeService.retrieveEmployeeById(empId);
    }
}
