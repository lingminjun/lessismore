package com.lessismore.sample.copier.dal;

import com.lessismore.xauto.annotation.XAutoConvert;
import lombok.Data;

@Data
@XAutoConvert("com.lessismore.sample.copier.dto.UserDTO")
public class User extends BaseUser {
    public String name;
    public Integer age;
    public Integer sex; // gender对应
    public Integer getGender() {
        return sex;
    }
}
