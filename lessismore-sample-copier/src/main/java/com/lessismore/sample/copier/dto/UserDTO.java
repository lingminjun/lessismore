package com.lessismore.sample.copier.dto;

import com.lessismore.xauto.annotation.XAutoAccessor;
import com.lessismore.xauto.annotation.XAutoConvert;
import lombok.Data;

import java.util.List;

@XAutoAccessor
@XAutoConvert("com.lessismore.sample.copier.dal.User")
public class UserDTO {
    private long uid;
    private String nick;
    private String mobile;
    private String name;
    private int age;
    private Gender gender; // gender对应

    private List<UserDTO> friends;
}
