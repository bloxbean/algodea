/*
 * Copyright (c) 2020 BloxBean Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bloxbean.algodea.idea.nodeint;

import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.bloxbean.algodea.idea.common.Tuple;
import com.intellij.openapi.util.text.StringUtil;
import com.squareup.okhttp.HttpUrl;
import org.apache.commons.lang3.ArrayUtils;

public class AlgoConnectionFactory {
    private final static String PURESTAKE = "purestake.io";

    private String apiUrl;
    private String indexerApiUrl;
    private String apiKey;

    private boolean purestakeUrl;

    private String[] headers;
    private String[] values;

    public AlgoConnectionFactory(String apiUrl, String indexerApiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.indexerApiUrl = indexerApiUrl;
        this.apiKey = apiKey;

        if(apiUrl != null && apiUrl.contains(PURESTAKE)) {
            purestakeUrl = true;
            headers = new String[] {"X-API-Key"};
            values = new String[] {apiKey};
        } else {
            headers = new String[0];
            values = new String[0];
        }
    }

    public AlgodClient connect() {
        HttpUrl url = HttpUrl.parse(apiUrl);
        AlgodClient algodClient = new AlgodClient(apiUrl, url.port(), apiKey);
        return algodClient;
    }

    public IndexerClient connectToIndexerApi() {
        if(StringUtil.isEmpty(indexerApiUrl))
            return null;

        HttpUrl url = HttpUrl.parse(indexerApiUrl);
        IndexerClient indexerClient = new IndexerClient(indexerApiUrl, url.port(), apiKey);
        return indexerClient;
    }

    public Tuple<String[], String[]> getHeaders() {
        return new Tuple<>(headers, values);
    }

    public Tuple<String[], String[]> getHeadersForBinaryContent() {
        String[] txHeaders = ArrayUtils.add(headers, "Content-Type");
        String[] txValues = ArrayUtils.add(values, "application/x-binary");

        return new Tuple<>(txHeaders, txValues);
    }

    public boolean isPurestakeUrl() {
        return purestakeUrl;
    }

}
