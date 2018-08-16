package top.bfylu;

import org.junit.Test;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Ethereum;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author bfy--lujian
 * @version 1.0.0
 * 创建时间：2018/8/10 15:53
 * @email bfyjian@gmail.com
 */
public class Eth {

    //连接钱包节点【后续所有操作都需要钱包节点广播出去】
    private static Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/6c8600c1d46b4a128c6e4c3058f8a171"));

    //现在当然是最重要的是交易啦。
    //产生交易需要加载私钥和钱包地址。这里用引用文件的方式来加载钱包地址！
    String path="钱包加密文件地址";
    Credentials ALICE = WalletUtils.loadCredentials("你的密码", path);

    //请求到一个钱包信息对象！
    BigInteger nonce = getNonce("发送钱包地址");

    private static BigInteger getNonce(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount =
                web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                .sendAsync().get();
        return ethGetTransactionCount.getTransactionCount();
    }

    private static RawTransaction createEtherTransaction(BigInteger nonce, String toAddress) {
        BigInteger value = Convert.toWei("数量", Convert.Unit.ETHER).toBigInteger();

        //交易手续费由price*limit来决定，所有这两个值你可以自定义，也可以使用系统参数获取当前两个值
        /**
         * 影响的结果就是自定义手续费会影响到账时间，手续费过低矿机会最后才处理你的！使用系统的话，手续费可能会很高，系统
         * 是获取当前最新成交的一笔手续来计算的。可能一笔需要几百人民币
         */
        BigInteger price = BigInteger.valueOf(100);
        BigInteger limit = BigInteger.valueOf(1);
        return RawTransaction.createEtherTransaction(nonce, price,limit , toAddress, value);
    }

    public Eth() throws Exception {

    }


    @Test
    public void test01() {
        //连接钱包节点【后续所以操作都需要钱包节点广播出去】
        Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/6c8600c1d46b4a128c6e4c3058f8a171"));
    }

    @Test
    public void test02() {
        //连接钱包节点【后续所以操作都需要钱包节点广播出去】
        Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/6c8600c1d46b4a128c6e4c3058f8a171"));
        //测试节点是否链接成功
        Web3ClientVersion web3ClientVersion;
        try {
            web3ClientVersion = web3.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            System.out.println(clientVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>创建钱包地址与密钥</p>
     */
    @Test
    public void test03() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        String filePath = "E:/pictures";
        String fileName;
        //创建钱包地址
        //eth-密码需要自己管理，自己设置
        fileName = WalletUtils.generateNewWalletFile("lu123456", new File(filePath), false);
        System.out.println(fileName); ////保存你的加密文件信息
        System.out.println(ALICE.getAddress()); //钱包地址
        System.out.println(ALICE.getEcKeyPair().getPrivateKey()); //密钥
        System.out.println(ALICE.getEcKeyPair().getPublicKey()); //公钥

    }

    /**
     * <p>发送金额</p>
     */
    @Test
    public void test04() {
        //发送金额
        RawTransaction rawTransaction = createEtherTransaction(nonce, "mubia钱包地址");

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, ALICE);

        //交易订单号
        String hexValue = Numeric.toHexString(signedMessage);

    }

    @Test
    public void test05() throws IOException {
        //获取余额
        EthGetBalance ethGetBalance1 = web3.ethGetBalance("0xb86d57174bf8c53f1084be7f565f9fd9dabd87d0", DefaultBlockParameter.valueOf("latest")).send();
        //eth默认会部18个0这里处理比较随意，大家可以随便处理哈
        BigDecimal balance = new BigDecimal(ethGetBalance1.getBalance().divide(new BigInteger("10000000000000")).toString());
        BigDecimal nbalance = balance.divide(new BigDecimal("100000"), 8, BigDecimal.ROUND_DOWN);
        System.out.println(nbalance);
    }

}
