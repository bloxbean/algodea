package com.bloxbean.algorand.idea.language.opcode.model;

import java.util.Objects;

public class Field {
    private int index;
    private String name;
    private String type;
    private String note;
    private String desc;

    public Field() {

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return index == field.index &&
                Objects.equals(name, field.name) &&
                Objects.equals(type, field.type) &&
                Objects.equals(note, field.note) &&
                Objects.equals(desc, field.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, name, type, note, desc);
    }

    @Override
    public String toString() {
        return "Field{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", note='" + note + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
