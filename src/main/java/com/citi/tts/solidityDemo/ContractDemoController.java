package com.citi.tts.solidityDemo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import com.citi.tts.Exchange;

@RestController
public class ContractDemoController {

	private static final Logger log = LoggerFactory.getLogger(ContractDemoController.class);

	@Autowired
	private Web3j web3j;

	@Value("${exchangeContractAddress}")
	private String exchangeContractAddress;// = new String("0x3b570ff040afa0cc5e499f92310dce7e4ab34e8a");

	@Value("${credentialpath}")
	private String credentialpath;

	@Value("${password}")
	private String password;

	@Value("${web3jprovider}")
	private String web3jprovider;

	@RequestMapping("/getOrderBook")
	public ServiceResponse getOrderBook(@RequestParam(value = "tokenSymbol") String tokenSymbol, @RequestParam(value = "orderType") String orderType ) {

		ArrayList<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
		ServiceResponse response = new ServiceResponse();
		OrderDetail orderDetail;
		Tuple2<List<BigInteger>, List<BigInteger>> tuple2;

		try {

			// Check if we have a non empty value for the request param
			// if there is no value, set the status to Error and return
			// message

			if (StringUtils.isEmpty(tokenSymbol)) {
				response.setMessage(" Token Symbol should not be empty");
				response.setStatus("ERROR");
				return response;
			}

			// We start by creating a new web3j instance to connect to remote nodes on the
			// network.
			// Note: if using web3j Android, use Web3jFactory.build(...
			// web3j = Web3j.build(new
			// HttpService("https://rinkeby.infura.io/TrDfxWjmBq2W7KmHve5e"));
			web3j = Web3j.build(new HttpService("http://localhost:8545"));

			log.info(
					"Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());

			Credentials credentials = WalletUtils.loadCredentials(password, credentialpath);
			
			log.info("   Credential Verified      " + credentials.getAddress());
			
			Exchange exchangeContract = Exchange.load(exchangeContractAddress, web3j, credentials,
					ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
			
			// Check if token is present on exchange for the Symbol
			boolean isTokenPresent = exchangeContract.hasToken(tokenSymbol).send();

			log.info(" Does the Exchange have " + tokenSymbol + "  symbol  " + isTokenPresent);

			if (isTokenPresent) {
				
				if (!StringUtils.isEmpty(orderType) && "SELL".equals(orderType)) {
					log.info(" Sell Order Book Details Requested ");
					// Retrieve Sell Order Details
					tuple2 = exchangeContract.getSellOrderBook(tokenSymbol).send();
					
				}else {
					log.info(" Buy Order Book Details Requested ");
					tuple2 = exchangeContract.getBuyOrderBook(tokenSymbol).send();
				}

				if (null != tuple2 && tuple2.getSize() >= 1) {
					
					log.info(" Sell Order Book Details Received ");
					
					List quantityList = tuple2.getValue2();
					List costList = tuple2.getValue1();

					// Length Should be same
					if (quantityList.size() == costList.size()) {
						int iteratorIndex = 0;
						int size = quantityList.size();
						while (iteratorIndex < size) {
							orderDetail = new OrderDetail();
							orderDetail.setTokenName(tokenSymbol);
							orderDetail.setQuantity(((Uint256) quantityList.get(iteratorIndex)).getValue());
							orderDetail.setCost(((Uint256) costList.get(iteratorIndex)).getValue());
							orderDetailList.add(orderDetail);
							iteratorIndex++;
						}

					}

				}
				log.info(" Sell Order Book Details Sent ");
				response.setStatus("SUCCESS");
				response.setMessage("Order Book Details Retrieved for Symbol : " + tokenSymbol);
				response.setOrderDetailList(orderDetailList);
			} else {

				response.setMessage(" Token Symbol is not present on exchange");
				response.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus("ERROR");
		}

		return response;
	}

}
