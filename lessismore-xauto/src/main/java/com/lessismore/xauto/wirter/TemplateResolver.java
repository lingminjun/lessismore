package com.lessismore.xauto.wirter;

public interface TemplateResolver {
    String resolver(String relativePath, String suffix, Object model);
}
