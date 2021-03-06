package com.lessismore.sample.copier.dal;

import com.lessismore.sample.copier.dto.UserDTO;
import com.lessismore.xauto.annotation.XAutoConvert;
import com.lessismore.xauto.annotation.XAutoMapping;
import com.lessismore.xauto.annotation.XAutoSource;
import com.lessismore.xauto.annotation.XAutoTarget;
import lombok.Data;

import java.util.Date;

@Data
@XAutoConvert(targets = @XAutoTarget(target = UserDTO.class,
        mapping = {@XAutoMapping(field = "gender", from = "sex"),
                @XAutoMapping(field = "city", from = "address", expression = "#{fromValue} != null ? #{fromValue}.city : null")}),
        sources = @XAutoSource(source =UserDTO.class,
                mapping = {@XAutoMapping(field = "gender", from = "sex"),
                        @XAutoMapping(field = "city", from = "address", expression = "#{fromValue} != null ? #{fromValue}.city : null")}))
public class User extends BaseUser {
    public String name;
    public Integer age;
    public Integer sex; // gender对应
    public Date birthDay;

    public Address address;

}
