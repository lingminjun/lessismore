#Less is More

##一、存取方法支持（Setter、Getter）

**1、XAutoAccessor**

同时生成Setter和Getter方法
```
@XAutoAccessor
public class Person {
    private Long uid;
    private String nick;
    private String head;
    private String mobile;
}

```

**2、XAutoSetter**

单独生成Setter
```
@XAutoSetter
public class Person {
    private Long uid;
    private String nick;
    private String head;
    private String mobile;
}

```

**3、XAutoGetter**

单独生成Getter
```
@XAutoGetter
public class Person {
    private Long uid;
    private String nick;
    private String head;
    private String mobile;
}

```

##二、对象拷贝支持（Copier）

**1、XAutoConvert**

配置可以拷贝的目标类型，此配置含本身具备XAutoAccessor配置

其定义如下：
```
/**
 * 标记需要做转换,目标类型集合，自动生成转化类
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@XAutoAccessor
public @interface XAutoConvert {
    /**
     * 所有支持的类
     * @return
     */
    String[] value();
}
```

使用案例：
```
@XAutoConvert(@XAutoTarget(targetClassName = "com.lessismore.sample.copier.dal.User", mapping = @XAutoMapping(field = "sex", from = "gender")))
public class User extends BaseUser {
    public String name;
    public Integer age;
    public Integer sex; // gender对应
}
```


**2、XAutoConverterConfiguration**

集中配置拷贝器，对于目标和源对象都是三方库的对象（不可编辑）时，则可采用此方式配置
```
@XAutoConverterConfiguration({@XAutoConverter(target = Person.class, source = User.class)})
public class ConverterAutoConfiguration  {
}
```

**3、高级用法**

* 3.1 **自定义Copier**：
继承实现CopierInterface，并在META-INF.service的com.lessismore.xauto.copy.CopierInterface添加自定义的Copier类，让CopierFactory可以加载那你自定义的Copier。


* 3.2 **设置默认的Copier**：
CopierFactory提供设置默认的拷贝器，可以指定一个为默认拷贝器

**4、自定义生成器**