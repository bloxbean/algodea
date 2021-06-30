package com.bloxbean.algodea.idea.language.opcode.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Field {
    private int index;
    private String name;
    private String type;
    private String note;
    private String desc;
    private int since;

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

    public Optional<String> formatHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");

        //createLiTag(sb, "Value", String.valueOf(index));

        if(name != null) {
            createLiTag(sb, "Name", name);
        }

        if(type != null && !type.isEmpty()) {
            createLiTag(sb, "Type", type);
        }

        if(desc != null && !desc.isEmpty()) {
            createLiTag(sb, null, desc);
        }

        if(note != null && !note.isEmpty()) {
            createLiTag(sb, null, note);
        }

        return Optional.of(sb.toString());
    }

    private void createLiTag(StringBuilder sb, String key, String value) {
        sb.append("<li>");
        if(key != null) {
            sb.append(key);
            sb.append(": ");
        }
        sb.append(value);
        sb.append("</li>");
    }

}
