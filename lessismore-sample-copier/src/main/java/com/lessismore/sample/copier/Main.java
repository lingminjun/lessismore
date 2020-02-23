package com.lessismore.sample.copier;

import com.alibaba.fastjson.JSON;
import com.lessismore.sample.copier.dal.User;
import com.lessismore.sample.copier.dto.Gender;
import com.lessismore.sample.copier.dto.UserDTO;
import com.lessismore.sample.copier.pv.Person;
import com.lessismore.xauto.copy.CopierFactory;
import com.lessismore.xauto.copy.CopierInterface;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        System.out.println("测试Copier");

        User user = new User();
        user.uid = 111l;
        user.name = "张帆";
        user.nick = "不重要";
        user.head = "https://www.ddd.com/dasd/xsa.png";
        user.sex = 1;
        user.age = 30;
        user.birthDay = new Date();

        CopierInterface<User, UserDTO> copier1 = CopierFactory.getCopier(User.class, UserDTO.class);
        UserDTO dto = copier1.copy(user);
        System.out.println(JSON.toJSON(dto));

        CopierInterface<User, Person> copier2 = CopierFactory.getCopier(User.class, Person.class);
        Person person = copier2.copy(user);
        System.out.println(JSON.toJSON(person));
    }
}
