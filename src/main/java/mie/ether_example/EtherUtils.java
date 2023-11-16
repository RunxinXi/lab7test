package mie.ether_example;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;


public class EtherUtils {
	public static final BigInteger GAS_PRICE = BigInteger.valueOf(200000000L);
	public static final BigInteger GAS_LIMIT_ETHER_TX = BigInteger.valueOf(500000L);
	public static final BigInteger GAS_LIMIT_CONTRACT_TX = BigInteger.valueOf(3000000L);
	
	public static List<Credentials> getAccounts(List<String> privateKeys){
		List<Credentials> accounts = new ArrayList<>();
		
		for (String privateKey : privateKeys) {
			accounts.add(Credentials.create(privateKey));
		};
		
		return accounts;
	}
	
	public static Bytes32 stringToBytes32(String string) {
        byte[] byteValue = string.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }
	
	public static String bytes32ToString(Bytes32 bString) {
		// convert to string and eliminate trailing zeros
		return new String(bString.getValue(), StandardCharsets.UTF_8).replaceFirst("\u0000+$", "");
	}
	
	public static void reportTransaction(String message, TransactionReceipt transactionReceipt) {
		String formattedMessage = String.format("%s [Block: %d, Transaction: %s]",
				message, transactionReceipt.getBlockNumber().intValue(),
				transactionReceipt.getTransactionHash());
		System.out.println(formattedMessage);
	}
	
}
