package com.specification.reactive.reactivestreams.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class Employee {
    private String empId;
    private String name;
    private int salary;
    private String departmentId;
    private String status;
}
