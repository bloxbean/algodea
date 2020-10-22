package com.bloxbean.algodea.idea.nodeint.model;

import java.util.Objects;

public class Network {
    private String genesisId;
    private String geneisHash;

    public Network(String geneisHash, String geneisId) {
        this.geneisHash = geneisHash;
        this.genesisId = genesisId;
    }
    public String getGenesisId() {
        return genesisId;
    }

    public void setGenesisId(String genesisId) {
        this.genesisId = genesisId;
    }

    public String getGeneisHash() {
        return geneisHash;
    }

    public void setGeneisHash(String geneisHash) {
        this.geneisHash = geneisHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return genesisId.equals(network.genesisId) &&
                geneisHash.equals(network.geneisHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genesisId, geneisHash);
    }
}
