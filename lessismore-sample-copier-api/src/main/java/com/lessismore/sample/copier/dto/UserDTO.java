package com.lessismore.sample.copier.dto;

import com.lessismore.xauto.annotation.XAutoConvert;
import com.lessismore.xauto.annotation.XAutoMapping;
import com.lessismore.xauto.annotation.XAutoTarget;
import lombok.Data;

import java.util.List;

//@XAutoAccessor
@Data
//@XAutoConvert(targets = @XAutoTarget(targetClassName = "com.lessismore.sample.copier.dal.User",
//        mapping = {@XAutoMapping(field = "sex", from = "gender")}))
//@XAutoConvert
public class UserDTO {
    public Long uid;
    public String nick;
    public String mobile;
    public String name;
    public Integer age;
    public Gender gender; // gender对应
    public String birthDay;

    public String city;

    public AddressDTO address;

    private List<UserDTO> friends;
}
