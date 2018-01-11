package com.citi.tts.solidityDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import com.citi.tts.Exchange;


public class DeployContract {

	private static final Logger log = LoggerFactory.getLogger(DeployContract.class);

	public static void main(String[] args) throws Exception {
		new DeployContract().run();
	}

	private void run() throws Exception {

		// We start by creating a new web3j instance to connect to remote nodes on the
		// network.
		// Note: if using web3j Android, use Web3jFactory.build(...
		Web3j web3j = Web3j.build(new HttpService("https://rinkeby.infura.io/TrDfxWjmBq2W7KmHve5e")); 
		
		log.info("Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());
		
		Credentials credentials =
                WalletUtils.loadCredentials(
                        "Test1234",
                        "C://Udemy//Angular//Limelab-2//keystore//UTC--2018-01-05T17-30-39.593000000Z--96d17f676a0ec53ef11b84c3cf6604ac12363f69.json");
        log.info("Credentials loaded");
        
      /*  log.info("Deploying smart contract");
        FixedSupplyToken fixedSupplyContract = FixedSupplyToken.deploy(
                web3j, credentials,
                ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT).send();

        String fixedSupplyContractAddress = fixedSupplyContract.getContractAddress();
        log.info("Smart Exchange contract deployed to address " + fixedSupplyContractAddress);
        log.info("View contract at https://rinkeby.etherscan.io/address/" + fixedSupplyContractAddress);
        
        */
        log.info("Deploying smart contract");
        Exchange exchangeContract = Exchange.deploy(
                web3j, credentials,
                ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT).send();

        String contractAddress = exchangeContract.getContractAddress();
        log.info("Smart Exchange contract deployed to address " + contractAddress);
        log.info("View contract at https://rinkeby.etherscan.io/address/" + contractAddress);
        
        
        
	}

}
