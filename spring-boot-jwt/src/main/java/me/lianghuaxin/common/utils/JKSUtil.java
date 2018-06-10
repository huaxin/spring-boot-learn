package me.lianghuaxin.common.utils;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;

public class JKSUtil {
    private String keyStoreFile;
    private char[] storePassword;
    private char[] keyPassword;//私钥密码
    private KeyStore store;
    private Object lock = new Object();

    private static JKSUtil instance = null;
    /*CN(Common Name名字与姓氏)
　　OU(Organization Unit组织单位名称)
　　O(Organization组织名称)
　　L(Locality城市或区域名称)
　　ST(State州或省份名称)
　　C(Country国家名称）*/
//keytool -genkey -v -alias test -dname "CN=Liang,OU=XXX,O=Corp,L=GuangZhou,ST=GuangDong,C=CN" -keyalg RSA -keysize 2048 -keypass 123456 -storepass 123456 -storetype JKS -validity 900
    public static JKSUtil getInstance() {
        synchronized (JKSUtil.class) {
            if (instance == null) {
                synchronized (JKSUtil.class) {
                    instance = new JKSUtil("C:/Users/mt200/.keystore", "123456".toCharArray(),"123456".toCharArray());
                }
            }
            return instance;
        }
    }

    private JKSUtil(String _jksFilePath, char[] storePassword, char[] keyPassword) {
        this.keyStoreFile = _jksFilePath;
        this.storePassword = storePassword;
        this.keyPassword = keyPassword;
    }

    public KeyPair getKeyPair(String alias) {
        return getKeyPair(alias, this.keyPassword);
    }

    public KeyPair getKeyPair(String alias, char[] keyPassword) {
        try {
            synchronized (this.lock) {
                if (this.store == null) {
                    synchronized (this.lock) {
                        InputStream is = this.getClass().getResourceAsStream(keyStoreFile);
                        try {
                            this.store = KeyStore.getInstance("JKS");//storetype 默认为JKS,常用的还有JCEKS,PKCS12,BKS,UBER
                            this.store.load(is, this.storePassword);//storePassword,证书库的访问密码
                        } finally {
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }
            RSAPrivateCrtKey key = (RSAPrivateCrtKey) this.store.getKey(alias, keyPassword);//alias,证书别名
            RSAPublicKeySpec spec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
            return new KeyPair(publicKey, key);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load keys from store: " + this.keyStoreFile, e);
        }
    }
}
