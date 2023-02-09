package com.specification.reactive.reactivestreams.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class Document {
    private String salary;
    private String empId;
    private String departmentId;
    private String status;
}
