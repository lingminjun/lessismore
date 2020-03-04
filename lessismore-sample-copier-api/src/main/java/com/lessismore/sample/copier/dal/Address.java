package com.lessismore.sample.copier.dal;

import com.lessismore.sample.copier.dto.AddressDTO;
import com.lessismore.xauto.annotation.XAutoConvert;
import com.lessismore.xauto.annotation.XAutoTarget;
import lombok.Data;

@Data
@XAutoConvert(targets = @XAutoTarget(target = AddressDTO.class))
public class Address {
    public String city;
    public String province;
}
