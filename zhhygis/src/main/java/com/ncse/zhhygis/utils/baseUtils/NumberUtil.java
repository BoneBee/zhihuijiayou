package com.ncse.zhhygis.utils.baseUtils;

import com.supermap.data.Workspace;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * 生成编号
 * @author baiyang
 * @date 2018/8/10下午2:52
 */
public class NumberUtil {

    private static SecureRandom random = null;

    static {
        try {
            random = SecureRandom.getInstance("SHA1PRNG","SUN");
        } catch (NoSuchAlgorithmException e) {
        } catch (NoSuchProviderException e) {
        }
    }

    /**
     * 生成18位编号
     * @return
     */
    public static String random18(){
//        int random = (int) Math.floor(99999 * Math.random());
//        return System.currentTimeMillis()+""+random;
        String rand1 = String.valueOf(System.currentTimeMillis());
        String rand2 = String.valueOf(random(100000));
        if (rand2.length()<5){
            rand2 = "0" + rand2;
        }

        return rand1 + rand2;
    }

    /**
     * uuid
     * @return
     */
    public static String uuid(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 生成4位编号
     * @return
     */
    public static String random4(){
//        return String.valueOf(System.currentTimeMillis()).substring(11) + (int) Math.floor(99 * Math.random());
        String rand1 = String.valueOf(System.currentTimeMillis()).substring(11);
        String rand2 = String.valueOf(random(100));
        if (rand2.length()<2){
            rand2 = "0" + rand2;
        }
        return rand1+rand2;
    }

    /**
     * 生成6位编号
     * @return
     */
    public static String random6(){
//        return String.valueOf(System.currentTimeMillis()).substring(9) + (int) Math.floor(99 * Math.random());
        String rand1 = String.valueOf(System.currentTimeMillis()).substring(11);
        String rand2 = String.valueOf(random(10000));
        if (rand2.length()<4){
            rand2 = "0" + rand2;
        }
        return rand1+rand2;
    }

    /**
     * 生成10位编号
     * @return
     */
    public static String random10(){
//        return String.valueOf(System.currentTimeMillis()).substring(8) + (int) Math.floor(99999 * Math.random());
        String rand1 = String.valueOf(System.currentTimeMillis()).substring(8);
        String rand2 = String.valueOf(random(100000));
        if (rand2.length()<5){
            rand2 = "0" + rand2;
        }
        return rand1 + rand2;
    }

    /**
     * 指定生成随机数
     * @param num
     * @return
     */
    public static int random(int num){
        Integer randNum = 0;
        if (random != null){
            randNum = random.nextInt(num);
        }
        return randNum;
    }

}