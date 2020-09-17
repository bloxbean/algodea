package com.bloxbean.algorand.idea.language.opcode.model;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.bloxbean.algorand.idea.language.opcode.model.OpCodeConstants.*;

public class OpCode {
    private String op;
    private String opcode;
    private String[] pops;
    private String[] pushes;
    String error;
    String desc;
    Map<String, String> metadata;
    String mode;
    String additionalDesc;
    Cost[] costs;

    public OpCode() {

    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public String[] getPops() {
        return pops;
    }

    public void setPops(String[] pops) {
        this.pops = pops;
    }

    public String[] getPushes() {
        return pushes;
    }

    public void setPushes(String[] pushes) {
        this.pushes = pushes;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAdditionalDesc() {
        return additionalDesc;
    }

    public void setAdditionalDesc(String additionalDesc) {
        this.additionalDesc = additionalDesc;
    }

    public Cost[] getCosts() {
        return costs;
    }

    public void setCosts(Cost[] costs) {
        this.costs = costs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpCode opCode = (OpCode) o;
        return Objects.equals(op, opCode.op) &&
                Objects.equals(opcode, opCode.opcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, opcode);
    }

    public Optional<String> formatHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");

        if(opcode != null) {
            createLiTag(sb, "Opcode", opcode);
        }

        if(pops != null) {
            createLiTagForValues(sb, "Pops", pops);
        }

        if(pushes != null) {
            createLiTagForValues(sb, "Pushes", pushes);
        }

        if(desc != null && !desc.isEmpty()) {
            createLiTag(sb, null, desc);
        }

        if(error != null && !error.isEmpty()) {
            createLiTag(sb, "Error", error);
        }

        if(metadata != null && metadata.size() > 0) {
            metadata.entrySet()
                    .forEach(entry -> {
                        createLiTag(sb, entry.getKey(), entry.getValue());
                    });
        }

        if(costs != null && costs.length > 0) {
            sb.append("<li>Cost:");
            sb.append("<ul>");
            for(Cost cost: costs) {
                cost.formatHtml(sb);
            }
            sb.append("</ul></li>");
        }

        sb.append("</ul>");

        if(additionalDesc != null && !additionalDesc.isEmpty()) {
            sb.append("<br>");
            sb.append(additionalDesc);
        }

        if(op != null) {
            sb.append("<br>");
            sb.append("<a href=")
                    .append(ALGO_DOC_BASE_URL)
                    .append("#")
                    .append(op)
                    .append("\">")
                    .append("Official Doc")
                    .append("</a>");

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

    private void createLiTagForValues(StringBuilder sb, String key, String[] values) {
        sb.append("<li>");
        if(key != null) {
            sb.append(key);
            sb.append(": ");
        }

        if(values.length == 0)
            sb.append(NONE);
        else
            sb.append(String.join(",", values));

        sb.append("</li>");
    }


    static class Cost {
        private int cost;
        private String filter;

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public void formatHtml(StringBuilder sb) {
            sb.append("<li>");
            sb.append(cost);

            if(filter != null && !filter.isEmpty()) {
                sb.append("(");
                sb.append(filter);
                sb.append(")");
            }
        }
    }
}


