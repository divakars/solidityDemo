package com.citi.tts.solidityDemo;

import java.math.BigInteger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import com.citi.tts.Exchange;
import com.citi.tts.Exchange.TokenAddedToSystemEventResponse;
import com.citi.tts.FixedSupplyToken;

//import org.slf4j.Logger;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import rx.Observable;

public class InitialiseExchange {

	//private static final Logger log = LoggerFactory.getLogger(InitialiseExchange.class);
	private static final Logger log = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	
	@Autowired
	private Web3j web3j;
	
	public static void main(String[] args) throws Exception {
		new InitialiseExchange().run();
	}
	
	private void run() throws Exception {
			
		log.setLevel(Level.INFO);
		 // We start by creating a new web3j instance to connect to remote nodes on the network.
        // Note: if using web3j Android, use Web3jFactory.build(...
		
        
        
        
        /** Rinkeby Credential
         * 
         * 
        web3j = Web3j.build(new HttpService(
                "https://rinkeby.infura.io/TrDfxWjmBq2W7KmHve5e")); 
                
        Credentials credentials =
                WalletUtils.loadCredentials(
                        "Test1234",
                        "C://Udemy//Angular//Limelab-2//keystore//UTC--2018-01-05T17-30-39.593000000Z--96d17f676a0ec53ef11b84c3cf6604ac12363f69.json");
         
         Exchange exchangeContract = Exchange.load("0xDa597BBDb8ECB0917237761d0C597583d6C8F402", web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        
		 FixedSupplyToken fstContract = FixedSupplyToken.load("0xe8dd90bf109cfbba80ee66c98b0f1f96fb1531dd", web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
		 
		 String exchangeContractAddress = new String("0xDa597BBDb8ECB0917237761d0C597583d6C8F402");
         String tokenContractAddress = new String ("0xe8dd90bf109cfbba80ee66c98b0f1f96fb1531dd");
         		
		**/
        
        web3j = Web3j.build(new HttpService("http://localhost:8545")); 
        
        log.info("Connected to Ethereum client version: "
                + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        
        Credentials credentials =
                WalletUtils.loadCredentials(
                        "Test1234",
                        "C://Udemy//Angular//Limelab-2//keystore//UTC--2018-01-11T17-38-46.205000000Z--f192d7eaf60b8d16e31bf3098b8e599c90073dfd.json");
        
      
        String exchangeContractAddress = new String("0x3b570ff040afa0cc5e499f92310dce7e4ab34e8a");
        String tokenContractAddress = new String ("0x8a8467feb0db940f481a8265387651c1419ecb73");
        
		Exchange exchangeContract = Exchange.load(exchangeContractAddress, web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        
		FixedSupplyToken fstContract = FixedSupplyToken.load(tokenContractAddress, web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
		
		
		 log.info("Adding Token to Exchange");
		 
		 TransactionReceipt transactionReceipt = exchangeContract.addToken("LTI", tokenContractAddress).send();
		 
		 log.info("Trasnaction Receipt Value "+transactionReceipt.getStatus());
		 log.info("Trasnaction Receipt Value "+transactionReceipt.getTransactionHash());
		 
		 Observable<TokenAddedToSystemEventResponse> response = exchangeContract.tokenAddedToSystemEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST);
		 response.subscribe(tokenAddedToSystemEventResponse -> {
                System.out.println("Token Name   "+tokenAddedToSystemEventResponse._token
                     + " " +tokenAddedToSystemEventResponse._symbolIndex + "  "+ tokenAddedToSystemEventResponse._timestamp
             );             
         }, Throwable::printStackTrace);
		 
		// Events enable us to log specific events happening during the execution of our smart
        // contract to the blockchain. Index events cannot be logged in their entirety.
        // For Strings and arrays, the hash of values is provided, not the original value.
        // For further information, refer to https://docs.web3j.io/filters.html#filters-and-events
		 
        for (Exchange.TokenAddedToSystemEventResponse event : exchangeContract.getTokenAddedToSystemEvents(transactionReceipt)) {
            log.info(" &&&&&&&&  Token Added event fired, New Token: " + event._token  );            
        }
        
        log.info("Token Addition Completed");
        
        log.info(" Does the Exchange have FIXED symbol  "+exchangeContract.hasToken("LTI").send());
        
        
        
        log.info(" *******************   Approving Token Usage Start *********************");
        
        transactionReceipt = fstContract.approve("0xDa597BBDb8ECB0917237761d0C597583d6C8F402", BigInteger.valueOf(100L)).send();
        for (FixedSupplyToken.ApprovalEventResponse event : fstContract.getApprovalEvents(transactionReceipt)) {
            log.info("Token Approval event fired, New Owner: " + event._owner  );            
        }
        
        log.info(" *******************   Approving Token Usage End *********************");
        
        log.info(" *******************   Deposit Token to exchange start *********************");
        
        transactionReceipt = exchangeContract.depositToken("FIXED", BigInteger.valueOf(100L)).send();
        
        for (Exchange.DepositForTokenReceivedEventResponse event : exchangeContract.getDepositForTokenReceivedEvents(transactionReceipt)) {
            log.info("Token Deposit event fired, New Owner: " + event._symbolIndex  );            
        }
        
        log.info(" *******************   Deposit Token to exchange End *********************");
        
        
        log.info(" *******************   Token Sell Order 1 Start *********************");
        
        transactionReceipt = exchangeContract.sellToken("FIXED", BigInteger.valueOf(9500000L), BigInteger.valueOf(10L)).send();
        
        for (Exchange.LimitBuyOrderCreatedEventResponse event : exchangeContract.getLimitBuyOrderCreatedEvents(transactionReceipt)) {
            log.info("Token Deposit event fired, New Owner: " + event._symbolIndex  );            
        }
        
        log.info(" *******************   Token Sell Order End *********************");
        
        
	}

}
