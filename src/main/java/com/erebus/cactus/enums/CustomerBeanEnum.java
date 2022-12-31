package com.erebus.cactus.enums;

public enum CustomerBeanEnum {

    CONSUMER_REFRESH_USER_PERMISSION_IMPL("REFRESH_USER_PERMISSION", "refreshUserPermissionServiceImpl", "trigger");
    private final String tag;
    private final String bean;
    private final String method;

    CustomerBeanEnum(String tar, String bean, String method) {
        this.tag = tar;
        this.bean = bean;
        this.method = method;
    }

    public static CustomerBeanEnum getEnumByTar(String tar) {
        for (CustomerBeanEnum statusEnum : CustomerBeanEnum.values()) {
            if (statusEnum.tag.equals(tar)) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("bean is not found,没有找到的tag值为:" + tar);
    }

}
