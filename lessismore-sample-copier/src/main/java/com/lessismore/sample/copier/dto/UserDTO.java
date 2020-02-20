package com.lessismore.sample.copier.dto;

import com.lessismore.xauto.annotation.XAutoAccessor;
import com.lessismore.xauto.annotation.XAutoConvert;
import lombok.Data;

import java.util.List;

//@XAutoAccessor
@Data
@XAutoConvert("com.lessismore.sample.copier.dal.User")
public class UserDTO {
    public Long uid;
    public String nick;
    public String mobile;
    public String name;
    public Integer age;
    public Gender gender; // gender对应

    private List<UserDTO> friends;
}
