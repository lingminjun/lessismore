package com.lessismore.sample.copier.pv;


import com.lessismore.xauto.annotation.XAutoAccessor;
import lombok.Data;
import lombok.Getter;

@XAutoAccessor
public class Person {
    private Long uid;
    private String nick;
    private String head;
    private String mobile;
}
