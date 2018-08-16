package top.bfylu;

import org.junit.Test;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
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
 * 创建时间：2018/8/10 17:43
 * @email bfyjian@gmail.com
 */
public class EthTest {



    //可以去https://infura.io 注册账号，就会生成这个地址
    //连接钱包节点【后续所有操作都需要钱包节点广播出去】
    //Admin web3j = Admin.build(new HttpService());
    private static Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/6c8600c1d46b4a128c6e4c3058f8a171"));

    public EthTest() throws IOException, CipherException {
    }

    /**
     * <p>测试节点是否链接成功</p>
     */
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

        String path="E:/pictures/UTC--2018-08-11T02-05-32.644000000Z--53b9672f79d457090141812941ad65137091eff6.json";

        Credentials ALICE = WalletUtils.loadCredentials("lu123456", path);
        System.out.println(ALICE.getAddress()); //钱包地址
        System.out.println(ALICE.getEcKeyPair().getPrivateKey()); //密钥
        System.out.println(ALICE.getEcKeyPair().getPublicKey()); //公钥

    }

    //查看钱包地址，密钥，公钥
    @Test
    public void test04() throws IOException, CipherException {
        String path = "E:/pictures/UTC--2018-08-11T02-05-32.644000000Z--53b9672f79d457090141812941ad65137091eff6.json";
        String path2 = "F:/Ethereum/keystore/UTC--2018-08-10T09-44-53.240086500Z--7934763c09abf1a99c309ce1b86e318ac7c691de";
        Credentials ALICE = WalletUtils.loadCredentials("lu123456", path2);

        System.out.println(ALICE.getAddress()); //钱包地址
        System.out.println(ALICE.getEcKeyPair().getPrivateKey()); //密钥
        System.out.println(ALICE.getEcKeyPair().getPublicKey()); //公钥
    }

    /**
     * <p>获取余额</p>
     * @throws IOException
     */
    @Test
    public void test05() throws IOException {
        //获取余额
        EthGetBalance ethGetBalance1 = web3.ethGetBalance("0x53b9672f79d457090141812941ad65137091eff6", DefaultBlockParameter.valueOf("latest")).send();

        //eth默认会部18个0这里处理比较随意，大家可以随便处理哈
        BigDecimal balance = new BigDecimal(ethGetBalance1.getBalance().divide(new BigInteger("10000000000000")).toString());
        BigDecimal nbalance = balance.divide(new BigDecimal("100000"), 8, BigDecimal.ROUND_DOWN);
        System.out.println("以太币：" + nbalance);
    }

    /*********************** eth交易 start**************************/
    //请求到一个钱包信息对象！
    private static BigInteger getNonce(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount =
                web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                        .sendAsync().get();
        return ethGetTransactionCount.getTransactionCount();
    }

    //发送金额
    private static RawTransaction createEtherTransaction(BigInteger nonce, String toAddress) {

        //eth转账数量(单位 wei)
        BigInteger value = Convert.toWei("0.5", Convert.Unit.ETHER).toBigInteger();

        /**
         * <p>交易手续费由price*limit来决定，所有这两个值你可以自定义，也可以使用系统参数获取当前两个值</p>
         * <P>>影响的结果就是自定义手续费会影响到账时间，手续费过低矿机会最后才处理你的！使用系统的话，手续费可能会很高，系统
         * 是获取当前最新成交的一笔手续来计算的。可能一笔需要几百人民币</P>
         * gasprice就是起到一个汇率的作用，它代表的是一个gas值多少eth。
         */
        //gas上限 (单位 wei)
        BigInteger price = BigInteger.valueOf(2);
        //gas价格
        BigInteger limit = BigInteger.valueOf(21000);

        return RawTransaction.createEtherTransaction(nonce, price,limit , toAddress, value);
    }

    /**
     * <p>eth交易</p>
     */
    @Test
    public void test06() throws Exception {

        //String path="E:/pictures/UTC--2018-08-11T02-05-32.644000000Z--53b9672f79d457090141812941ad65137091eff6.json";
        String path2 = "F:/Ethereum/keystore/UTC--2018-08-10T09-44-53.240086500Z--7934763c09abf1a99c309ce1b86e318ac7c691de";

        Credentials ALICE = WalletUtils.loadCredentials("lu123456", path2);
        //请求到一个钱包信息对象！
        BigInteger nonce = getNonce("0x7934763c09aBF1A99C309Ce1b86E318AC7C691dE");
        //发送金额
        RawTransaction rawTransaction = createEtherTransaction(nonce, "0x53b9672f79d457090141812941ad65137091eff6");

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, ALICE);

        //交易订单号
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();

        System.out.println("交易订单号==>>" + hexValue);
        System.out.println("TransactionHash==>>" + ethSendTransaction.getTransactionHash());
        System.out.println("getError==>>" + ethSendTransaction.getError());
        System.out.println("getRawResponse==>>" + ethSendTransaction.getRawResponse());
        System.out.println("getResult==>>" + ethSendTransaction.getResult());

        getType(ethSendTransaction.getTransactionHash());
    }

    /************************** eth交易 end *****************/

    /**
     * 轮询智能合约的状态
     */
    public void getType(String transactionHash) throws IOException, CipherException {

//        String path2 = "F:/Ethereum/keystore/UTC--2018-08-10T09-44-53.240086500Z--7934763c09abf1a99c309ce1b86e318ac7c691de";
//        //证书
//        Credentials credentials = WalletUtils.loadCredentials("lu123456", path2);

        PollingTransactionReceiptProcessor processor = new PollingTransactionReceiptProcessor(web3, 1000, 2000);
        try {
            processor.waitForTransactionReceipt(transactionHash);
            System.out.println("交易成功");
        } catch (TransactionException e) {
            System.out.println("交易失败");
            e.printStackTrace();
        }
    }

    //以太坊中代币数量的计量
    @Test
    public void test07() {
       // wei转换为ether
        BigInteger value = Convert.toWei("1", Convert.Unit.ETHER).toBigInteger();
        System.out.println(value);
    }


}
