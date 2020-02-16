# xauto介绍
* 旨在快速实现spring-boot或者spring-cloud服务间调用过程，简化编程过程。有一种与Dubbo一样的体验
* 旨在降低领域驱动设计的编码复杂过程，重点放在领域模型编写上，通过ORM实现模型往下的存储过程

# 技术实现
* 采用Annotation Processor实现注解增强
* AST修改则使用com.sun.tools接口（与lombok类似，没有lombok复杂，简化了AST编写过程）

# 执行测试
sh test_run.sh 测试 UserService.java, User.java

# 