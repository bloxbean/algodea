package com.bloxbean.algodea.idea.codegen.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder(alphabetic = true)
public class StateSchemaEx implements Serializable {
    @JsonProperty("nui")
    BigInteger numUint = BigInteger.ZERO;

    @JsonProperty("nbs")
    BigInteger numByteSlice = BigInteger.ZERO;

    public StateSchemaEx() {

    }

    public StateSchemaEx(BigInteger numUint, BigInteger numByteSlice) {
        this.numUint = numUint;
        this.numByteSlice = numByteSlice;
    }

    public BigInteger getNumUint() {
        return numUint;
    }

    public void setNumUint(BigInteger numUint) {
        this.numUint = numUint;
    }

    public BigInteger getNumByteSlice() {
        return numByteSlice;
    }

    public void setNumByteSlice(BigInteger numByteSlice) {
        this.numByteSlice = numByteSlice;
    }

}
