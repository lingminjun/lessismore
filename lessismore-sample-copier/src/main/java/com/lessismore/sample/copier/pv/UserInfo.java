package com.lessismore.sample.copier.pv;

import com.lessismore.xauto.annotation.XAutoAccessor;
import lombok.Data;

import java.util.List;

@XAutoAccessor
public class UserInfo {
    private Long uid;
    private String nick;
    private String head;

    private List<Person> friends;
}
