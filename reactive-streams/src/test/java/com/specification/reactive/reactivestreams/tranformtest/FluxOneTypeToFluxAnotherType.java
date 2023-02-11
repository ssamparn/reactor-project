package com.specification.reactive.reactivestreams.tranformtest;

import com.specification.reactive.reactivestreams.model.Person;
import com.specification.reactive.reactivestreams.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

@Slf4j
public class FluxOneTypeToFluxAnotherType {

    @Test
    public void flux_ofOneType_ToFlux_ofAnotherType_test() {
        Person person1 = Person.create("1500", "Harry", 23, 1000, "10", "Approved");
        Person person2 = Person.create("2500", "Suzanne", 32, 2000, "11", "Approved");

        Flux<Person> personFlux = Flux.just(person1, person2);

        Flux<Employee> employeeFlux = personFlux.map(this::toEmployee);

        StepVerifier.create(employeeFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    private Employee toEmployee(Person person) {
        Employee employee = new Employee();
        employee.setEmpId(person.getEmpId());
        employee.setName(person.getName());
        employee.setSalary(person.getSalary());
        employee.setDepartmentId(person.getDepartmentId());
        employee.setStatus(person.getStatus());

        return employee;
    }

    @Test
    public void list_ofOneType_ToList_ofAnotherType_test() {
        Person person1 = Person.create("1500", "Harry",23,1000, "10", "Approved");
        Person person2 = Person.create("2500", "Suzanne", 32, 2000, "10", "Approved");

        List<Person> personList = List.of(person1, person2);

        List<Employee> employeeList = personList.stream()
                .map(this::toEmployee)
                .toList();

        log.info("employeeList : {}", employeeList.size());
    }
}
