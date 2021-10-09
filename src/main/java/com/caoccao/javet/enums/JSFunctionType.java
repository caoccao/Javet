package com.caoccao.javet.enums;

/**
 * The enum JS function type.
 *
 * @since 0.8.8
 */
public enum JSFunctionType {
    /**
     * Native.
     *
     * @since 0.8.8
     */
    Native(0, "Native"),
    /**
     * API.
     *
     * @since 0.8.8
     */
    API(1, "API"),
    /**
     * User defined.
     *
     * @since 0.8.8
     */
    UserDefined(2, "UserDefined"),
    /**
     * Unknown.
     *
     * @since 0.8.8
     */
    Unknown(3, "Unknown");

    private final int id;
    private final String name;

    JSFunctionType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse JS function type by id.
     *
     * @param id the id
     * @return the JS function type
     * @since 0.8.8
     */
    public static JSFunctionType parse(int id) {
        switch (id) {
            case 0:
                return Native;
            case 1:
                return API;
            case 2:
                return UserDefined;
            default:
                return Unknown;
        }
    }

    /**
     * Gets id.
     *
     * @return the id
     * @since 0.8.8
     */
    public int getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     * @since 0.8.8
     */
    public String getName() {
        return name;
    }

    /**
     * Is API.
     *
     * @return the boolean
     * @since 0.8.8
     */
    public boolean isAPI() {
        return this == API;
    }

    /**
     * Is native.
     *
     * @return the boolean
     * @since 0.8.8
     */
    public boolean isNative() {
        return this == Native;
    }

    /**
     * Is user.
     *
     * @return the boolean
     * @since 0.8.8
     */
    public boolean isUserDefined() {
        return this == UserDefined;
    }
}
