package com.bloxbean.algodea.idea.nodeint.purestake;

import com.algorand.algosdk.v2.client.algod.HealthCheck;
import com.algorand.algosdk.v2.client.algod.Metrics;
import com.algorand.algosdk.v2.client.algod.SwaggerJSON;
import com.algorand.algosdk.v2.client.algod.AccountInformation;
import com.algorand.algosdk.v2.client.algod.GetPendingTransactionsByAddress;
import com.algorand.algosdk.v2.client.algod.GetBlock;
import com.algorand.algosdk.v2.client.algod.GetSupply;
import com.algorand.algosdk.v2.client.algod.GetStatus;
import com.algorand.algosdk.v2.client.algod.WaitForBlock;
import com.algorand.algosdk.v2.client.algod.RawTransaction;
import com.algorand.algosdk.v2.client.algod.TransactionParams;
import com.algorand.algosdk.v2.client.algod.GetPendingTransactions;
import com.algorand.algosdk.v2.client.algod.PendingTransactionInformation;
import com.algorand.algosdk.v2.client.algod.GetApplicationByID;
import com.algorand.algosdk.v2.client.algod.GetAssetByID;
import com.algorand.algosdk.v2.client.algod.TealCompile;
import com.algorand.algosdk.v2.client.algod.TealDryrun;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Client;
import com.algorand.algosdk.v2.client.common.HttpMethod;
import com.algorand.algosdk.v2.client.common.QueryData;
import com.squareup.okhttp.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomAlgodClient extends AlgodClient {
    private final static String PURESTAKE = "purestake.io";

    private boolean isPurestake;
    private String[] headers;
    private String[] values;

    /**
     * Construct an AlgodClient for communicating with the REST API.
     *
     * @param host  using a URI format. If the scheme is not supplied the client will use HTTP.
     * @param port  REST server port.
     * @param token authentication token.
     */
    public CustomAlgodClient(String host, int port, String token) {
        super(host, port, token);

        if(host != null && host.contains(PURESTAKE)) {
            isPurestake = true;
            headers = new String[]{"X-API-Key"};
            values = new String[]{token};
        }
    }

//    /**
//     * Construct an AlgodClient for communicating with the REST API.
//     * @param host using a URI format. If the scheme is not supplied the client will use HTTP.
//     * @param port REST server port.
//     * @param token authentication token.
//     */
//    public CustomAlgodClient(String host, int port, String token) {
//        super(host, port, token, host.contains("purestake") ? "x-api-key": "X-Algo-API-Token");
//    }

    @Override
    public Response executeCall(QueryData qData, HttpMethod httpMethod, String[] headers, String[] values) throws Exception {
        if(isPurestake) {
            if(headers == null) {
                headers = this.headers;
            } else {
                String[] _headers = new String[headers.length + this.headers.length];
                System.arraycopy(headers, 0, _headers, 0, headers.length);
                System.arraycopy(this.headers, 0, _headers, headers.length, this.headers.length);

                headers = _headers;
            }
            if(values == null) {
                values = this.values;
            } else {
                String[] _values = new String[values.length + this.values.length];
                System.arraycopy(values, 0, _values, 0, values.length);
                System.arraycopy(this.values, 0, _values, values.length, this.values.length);

                values = _values;
            }
        }
        return super.executeCall(qData, httpMethod, headers, values);
    }
}

