package com.lessismore.sample.copier.dal;

import com.lessismore.xauto.annotation.XAutoConvert;
import com.lessismore.xauto.annotation.XAutoMapping;
import com.lessismore.xauto.annotation.XAutoTarget;
import lombok.Data;

@Data
@XAutoConvert(@XAutoTarget(targetClassName = "com.lessismore.sample.copier.dto.UserDTO", mapping = @XAutoMapping(field = "gender", from = "sex")))
public class User extends BaseUser {
    public String name;
    public Integer age;
    public Integer sex; // gender对应
//    public Integer getGender() {
//        return sex;
//    }
}
