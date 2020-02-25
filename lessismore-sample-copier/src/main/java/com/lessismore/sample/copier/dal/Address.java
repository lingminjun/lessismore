package com.lessismore.sample.copier.dal;

import com.lessismore.xauto.annotation.XAutoConvert;
import com.lessismore.xauto.annotation.XAutoTarget;
import lombok.Data;

@Data
@XAutoConvert(targets = @XAutoTarget(targetClassName = "com.lessismore.sample.copier.dto.AddressDTO"))
public class Address {
    public String city;
    public String province;
}
