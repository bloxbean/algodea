package com.bloxbean.algodea.idea.nodeint;

public class AlgoConnectionFactoryTest  {
//    String url = "https://testnet-algorand.api.purestake.io/ps2";
//    String account = "3BZDVQMWUFLUZANXBLKOHGGDTB54M57XGB75QKSZZGBZ6VN72QAGLZTSOU";
//    final String PS_API_TOKEN = "";
//
//    public void testConnect() throws Exception {
//        CustomAlgodClient client = new CustomAlgodClient("https://testnet-algorand.api.purestake.io/ps2", 443, PS_API_TOKEN);
//        AccountInformation accountInformation = client.AccountInformation(new Address(account));
//
//        Response<Account> accounts = accountInformation.execute();
//        System.out.println(accounts.body());
//    }
//
//    public void testCompile() throws Exception {
//        CustomAlgodClient client = new CustomAlgodClient("https://testnet-algorand.api.purestake.io/ps2", 443, PS_API_TOKEN);
//
//        String source = "int 0";
//        Response<CompileResponse> res = client.TealCompile().source(source.getBytes()).execute();
//        CompileResponse cs = res.body();
//        if(cs != null) {
//            System.out.println(cs.hash);
//            System.out.println(cs.result);
//        }
//        System.out.println(res);
//    }
//
//    public void testCreateAsset() throws Exception {
//        CustomAlgodClient client = new CustomAlgodClient("https://testnet-algorand.api.purestake.io/ps2", 443, PS_API_TOKEN);
//
//        Response<TransactionParametersResponse> response = client.TransactionParams().execute();
//        if(response == null)
//            throw new Exception("Could not get transaction params");
//
//        if(!response.isSuccessful())
//            throw new Exception("Response not successful");
//
//        TransactionParametersResponse txnParamResponse = response.body();
//
//        System.out.println(txnParamResponse.lastRound);
//
//
//        // Total number of this asset available for circulation
//        BigInteger assetTotal = BigInteger.valueOf(10000);
//
//        // Whether user accounts will need to be unfrozen before transacting
//
//        boolean defaultFrozen = false;
//        // Used to display asset units to user
//        String unitName = "myunit";
//        // Friendly name of the asset
//        String  assetName = "my longer asset name";
//        String url = "http://this.test.com";
//        String assetMetadataHash = "16efaa3924a6fd9d3a4824799a4ac65d";
//
////        Account acct1 = new Account(account1_mnemonic);
////        Account acct2 = new Account(account2_mnemonic);
////        Account acct3 = new Account(account3_mnemonic);

//    }
}