package com.lessismore.sample.copier;

import com.lessismore.sample.copier.dal.User;
import com.lessismore.sample.copier.pv.Person;
import com.lessismore.xauto.annotation.XAutoConverter;
import com.lessismore.xauto.annotation.XAutoConverterConfiguration;

/** This is Less-is-More generating codes. Not allowed to edit. **/
@XAutoConverterConfiguration({@XAutoConverter(target = Person.class, source = User.class)})
public class ConverterAutoConfiguration  {
}