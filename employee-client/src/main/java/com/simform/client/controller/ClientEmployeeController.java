package com.simform.client.controller;

import com.simform.client.entity.Employee;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/api/employees")
public class ClientEmployeeController {

    @Autowired
    private RSocketRequester rSocketRequester;

    @GetMapping("/error")
    public Mono<String> sendRequest() {
        return rSocketRequester
                .route("handleError")
                .data(1001)
                .retrieveMono(String.class)
                .doOnError(e -> log.error(String.valueOf(e)))
                .onErrorResume(e -> Mono.just(e.getMessage()));
    }

    @GetMapping("/authenticate")
    public Mono<Employee> doAuthentication() {
        return rSocketRequester
                .route("authenticate")
                .metadata(
                        new UsernamePasswordMetadata("test", "test"),
                        MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString())
                )
                .data(Mono.empty())
                .retrieveMono(Employee.class);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<Employee> retrieveEmployees() {
        log.info("Sending request via Channel Stream");
        return rSocketRequester
                .route("allEmployees")
                .retrieveFlux(new ParameterizedTypeReference<>() {
                });
    }

    @PostMapping
    public Publisher<Void> addEmployee(@RequestBody Employee employee) {
        log.info("Sending request via Fire And Forget");
        return rSocketRequester
                .route("newEmployee")
                .data(employee)
                .send();
    }

    @GetMapping("/{empId}")
    public Publisher<Employee> retrieveEmployeeById(@PathVariable("empId") int empId) {
        log.info("Sending request via Request-Response");
        return rSocketRequester
                .route("employeeDataById")
                .data(empId)
                .retrieveMono(Employee.class);
    }
}


