package com.specification.reactive.reactivestreams.model;


import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "create")
public class Person {
    private String empId;
    private String name;
    private int age;
    private int salary;
    private String departmentId;
    private String status;

    public Person() {
        this.empId = String.valueOf(RsUtil.faker().number().numberBetween(10, 25));
        this.name = RsUtil.faker().name().fullName();
        this.age = RsUtil.faker().number().numberBetween(10, 50);
        this.salary = RsUtil.faker().number().numberBetween(1000, 100000);
        this.departmentId = RsUtil.faker().commerce().department();
        this.status = "APPROVED";
    }
}
