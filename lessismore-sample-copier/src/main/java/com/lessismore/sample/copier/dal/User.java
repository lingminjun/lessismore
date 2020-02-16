package com.lessismore.sample.copier.dal;

import com.lessismore.xauto.annotation.XAutoConvert;

@XAutoConvert("com.lessismore.sample.copier.dto.UserDTO")
public class User extends BaseUser {
    public String name;
    public Integer age;
    public Integer sex; // gender对应
}
