package com.lessismore.sample.copier.dal;

import com.lessismore.sample.copier.dto.UserDTO;
import com.lessismore.xauto.annotation.*;
import lombok.Data;

import java.util.Date;

@XAutoGenerator(@XAutoTemplate(className = "${model.className}Getter",
        classTemplate = "template/test_getter.ftl",
        model = SimpleUser.class,
        metaInfoFileName = "info/xserver",
        others = {User.class, UserDTO.class}))
public class SimpleUser extends BaseUser {
    public String name;
    public Integer age;
    public Integer sex; // gender对应
    public Date birthDay;
}
