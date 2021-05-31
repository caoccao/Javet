package com.caoccao.javet.enums;

public enum JSScopeType {
    Class(0, "Class"),
    Eval(1, "Eval"),
    Function(2, "Function"),
    Module(3, "Module"),
    Script(4, "Script"),
    Catch(5, "Catch"),
    Block(6, "Block"),
    With(7, "With"),
    Unknown(8, "Unknown");

    private final int id;
    private final String name;

    JSScopeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static JSScopeType parse(int id) {
        switch (id) {
            case 0:
                return Class;
            case 1:
                return Eval;
            case 2:
                return Function;
            case 3:
                return Module;
            case 4:
                return Script;
            case 5:
                return Catch;
            case 6:
                return Block;
            case 7:
                return With;
            default:
                return Unknown;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isBlock() {
        return this == Block;
    }

    public boolean isCatch() {
        return this == Catch;
    }

    public boolean isClass() {
        return this == Class;
    }

    public boolean isEval() {
        return this == Eval;
    }

    public boolean isFunction() {
        return this == Function;
    }

    public boolean isModule() {
        return this == Module;
    }

    public boolean isScript() {
        return this == Script;
    }

    public boolean isWith() {
        return this == With;
    }
}
