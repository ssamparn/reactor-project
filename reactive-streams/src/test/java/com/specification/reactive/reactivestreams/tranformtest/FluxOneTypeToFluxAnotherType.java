package com.specification.reactive.reactivestreams.tranformtest;

import com.specification.reactive.reactivestreams.model.Document;
import com.specification.reactive.reactivestreams.model.Employee;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;

public class FluxOneTypeToFluxAnotherType {

    @Test
    public void flux_ofOneType_ToFlux_ofAnotherType_test() {
        Document document1 = Document.create("1500", "1", "10", "Approved");
        Document document2 = Document.create("2500", "2", "10", "Approved");

        Flux<Document> documentFlux = Flux.just(document1, document2);

        Flux<Employee> employeeFlux = documentFlux.map(this::toEmployee);

        StepVerifier.create(employeeFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    private Employee toEmployee(Document document) {
        Employee employee = new Employee();
        employee.setEmpId(document.getEmpId());
        employee.setSalary(document.getSalary());
        employee.setDepartmentId(document.getDepartmentId());
        employee.setStatus(document.getStatus());

        return employee;
    }

    @Test
    public void list_ofOneType_ToList_ofAnotherType_test() {
        Document document1 = Document.create("1500", "1", "10", "Approved");
        Document document2 = Document.create("2500", "2", "10", "Approved");

        List<Document> documentList = List.of(document1, document2);

        List<Employee> employeeList = documentList.stream()
                .map(this::toEmployee)
                .toList();

        System.out.println(employeeList.size());
    }
}
