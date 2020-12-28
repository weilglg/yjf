package cn.ygyg.cloudpayment.utils;

import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESHelper {

    private final static byte[] iv_zero16 = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
	
	/**
	 * AES Encrypt (ECB Mode), data must be 16 bytes aligned already
	 * 
	 * @param key [in]: AES key
	 * @param data [in]: data to encrypt
	 * @param offset [in]: data offset
	 * @param len [in]: data length
	 * 
	 * @return null: failed, other the result value
	 */
	public static byte[] aesEncryptECB16(byte[] key, byte[] data, int offset, int len) {
		try {
			if (key == null || key.length != 16)
				return null;

			if (data == null || offset < 0 || (len%16) != 0)
				return null;

			SecretKey aesKey = new SecretKeySpec(key, "AES");
			Cipher aesCipher = Cipher.getInstance("AES/ECB/NoPadding");
			IvParameterSpec iv = new IvParameterSpec(iv_zero16);
			//aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, iv);
			aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
			return aesCipher.doFinal(data, offset, len);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}
	
	/**
	 * AES Decrypt (ECB Mode), data must be 16 bytes aligned already
	 * 
	 * @param key [in]: AES key
	 * @param data [in]: data to decrtpr
	 * @param offset [in]: data offset
	 * @param len [in]: data length
	 * 
	 * @return null: failed, other the result value
	 */
	public static byte[] aesDecryptECB16(byte[] key, byte[] data, int offset, int len) {
		try {
			if (key == null || key.length != 16)
				return null;

			if (data == null || offset < 0 || (len%16) != 0)
				return null;
			
			SecretKey aesKey = new SecretKeySpec(key, "AES");
			Cipher aesCipher = Cipher.getInstance("AES/ECB/NoPadding");
			IvParameterSpec iv = new IvParameterSpec(iv_zero16);
			//aesCipher.init(Cipher.DECRYPT_MODE, aesKey, iv);
			aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
			return aesCipher.doFinal(data, offset, len);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}
	
	private static void Xor16(byte[] in1, byte[] in2, int in2_offset) {
		for (int i = 0; i < 16; i++) {
			in1[i] = (byte) (in1[i] ^ in2[in2_offset+i]);
		}
	}
	
	/**
	 * Encrypt the data of NFC Card (encrypted data stream := Ld(1) + data + padding)
	 * 
	 * @param key [in]: AES key
	 * @param data [in]: data to be encrypted
	 * @param offset [in]: data offset
	 * @param length [in]: data length
	 * @param Padding [in]: padding character
	 * 
	 * @return null: failed, other the result value
	 */
	public static byte[] NFCard_EncData(byte []key, byte[]data, int offset, int length, byte Padding) {
	
		try {			
			int len;
            byte[] in;
			if( ( (length+1)%16) == 0) {
				len = (length+1);
				in = new byte[len];
				in[0] = (byte)length;
				System.arraycopy(data, offset, in, 1, length);
			}
			else {
				len = (length+1) + 16 - (length+1)%16;				
				in = new byte[len];
				in[0] = (byte)length;
				System.arraycopy(data, offset, in, 1, length);
				Arrays.fill(in, (length+1), (length+1) + 16 - (length+1)%16, Padding);
			}
			
			//System.out.println("  in=" + PosUtils.bcdToString(in) + " len=" + len);
			return aesEncryptECB16(key, in, 0, len);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Decrypt the data of NFC Card
	 * 
	 * @param key [in]: AES key
	 * @param data [in]: data to be decrypted
	 * @param offset [in]: data offset
	 * @param length [in]: data length
	 * 
	 * @return null: failed, other the result value
	 */
	public static byte[] NFCard_DecData(byte []key, byte[]data, int offset, int length) {
	
		try {
			if( (length%16) != 0)
				return null; //not 16bytes align

            byte[] plain = aesDecryptECB16(key, data, offset, length);
			 int len = plain[0] & 0xff;
            byte[] out = new byte[len];
			 System.arraycopy(plain, 1, out, 0, len);
			 return out;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	/**
	 * Compute the MAC value of NFC Card
	 * 
	 * @param iv [in]: iv, use 16 bytes 0x00 as initialized value
	 * @param key [in]: AES key
	 * @param data [in]: data stream to compute MAC
	 * @param offset [in]: data offset
	 * @param length [in]: data length
	 * 
	 * @return null: failed, other the result value
	 */
	public static byte[] NFCard_getMAC(byte []iv, byte []key, byte[]data, int offset, int length) {
		
		try {
            byte[] in = new byte[16];
			Arrays.fill(in, (byte)0);

            byte[] padding = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    (byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            byte[] enc_hex;
			
			int  i;
			int len = length/16;

			for(i=0; i<16; i++) {
				in[i] = iv[i];
			}

			for(i=0; i<len; i++)
			{
				Xor16(in, data, offset+i*16);
				enc_hex = aesEncryptECB16(key, in, 0, 16);
				System.arraycopy(enc_hex, 0, in, 0, 16);
			}

			if( (length%16) == 0)
			{
				Xor16(in, padding, 16);
				enc_hex = aesEncryptECB16(key, in, 0, 16);
				System.arraycopy(enc_hex, 0, in, 0, 16);
			}
			else
			{
				for(i=len*16; i<length; i++)
				{
					padding[i-len*16]=data[offset+i];
				}
				padding[length-len*16]=(byte)0x80;

				for(i=length-len*8+1; i<8; i++)
				{
					padding[i] = 0;
				}
				Xor16(in,padding, 0);
				enc_hex = aesEncryptECB16(key, in, 0, 16);
				System.arraycopy(enc_hex, 0, in, 0, 16);
			}

            byte[] mac = new byte[8];
			System.arraycopy(in, 0, mac, 0, 8);
			return mac;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public static void main(String[] arg) {

		//Testing data
		final String UUID = "CA47A66C73545AC6CA47A66C73545AC6";
        final String[] MKEY = {"08AAFEDC03C9493EBD567BBE7740152B", // 0
                "9375579AD66F463CBB4B3D494B8D4E4C", // 1
                "A26A9CA9FEC1426898365DE9CB7AB9C4", // 2
                "1C939B8A900E4F1799037D336B6B886F", // 3
                "792A8ACD522A401598A6490969AF0C0D", // 4
                "CF2B6228FA944D68A1727EB4B2B184F8", // 5
                "B5ED6DF0263F45B1970EB1BBFF199635", // 6
                "4EEBAAC25BBA4D219DF26E751ECA4FF9" // 7
        };
		final String RANDOM1 = "CDA1040953B4FE33E10FCC50326D32D4";
		final String RANDOM2 = "BF2F745573574250ABE323A4565CD30D";
		final String AK = "BA2347CD88E94C209D2E2E10F78E19ED";
		int MKEY_index = 4;

		//Variable
        byte[] UUID_hex;
        byte[][] MKEY_hex = new byte[8][];
        byte[] RANDOM1_hex;
        byte[] RANDOM2_hex;
        byte[] AK_hex;

        //Initialize the Variable
		UUID_hex = PosUtils.hexStringToBytes(UUID);
		for (int i = 0; i < 8; i++) {
			MKEY_hex[i] = PosUtils.hexStringToBytes(MKEY[i]);
		}
		RANDOM1_hex = PosUtils.hexStringToBytes(RANDOM1);
		RANDOM2_hex = PosUtils.hexStringToBytes(RANDOM2);
		AK_hex = PosUtils.hexStringToBytes(AK);
		
		System.out.println("MKEY[" + MKEY_index + "] = " + PosUtils.bcdToString(MKEY_hex[MKEY_index]));
		System.out.println("Ramdom1 = " + PosUtils.bcdToString(RANDOM1_hex));
		
		//Ramdom1_enc = Enc(MK4, Random1)
        byte[] Random1_enc_hex = aesEncryptECB16(MKEY_hex[MKEY_index], RANDOM1_hex, 0, 16);
		System.out.println("Ramdom1_enc = Enc(MK4, Random1) = " + PosUtils.bcdToString(Random1_enc_hex));

        byte[] R1 = aesDecryptECB16(MKEY_hex[MKEY_index], Random1_enc_hex, 0, 16);
        System.out.println("R1 = Dec(MK4, Ramdom1_enc) = " + PosUtils.bcdToString(R1));

        //////////////////////////////////////
        //Test MAC

        String[] mac_data = {
                "1234567890abcdef12",
                "1234567890abcdef1234567890ab",
                "1234567890abcdef1234567890abcd",
                "1234567890abcdef1234567890abcdef"
        };
        
        System.out.println("  AK= " + PosUtils.bcdToString(AK_hex));
        for(int i=0; i<mac_data.length; i++) {
            byte[] mac_data_hex = PosUtils.hexStringToBytes(mac_data[i]);
            byte[] mac = NFCard_getMAC(iv_zero16, AK_hex, mac_data_hex, 0, mac_data_hex.length);
	        System.out.println("#" + i);
	        System.out.println("  mac_data= " + mac_data[i]);
	        System.out.println("  mac= " + PosUtils.bcdToString(mac));
        }
        
        //////////////////////////////////////
        //Test Enc

        String[] data = {
                "1234567890abcdef12",
                "1234567890abcdef1234567890",
                "1234567890abcdef1234567890ab",
                "1234567890abcdef1234567890abcd",
                "1234567890abcdef1234567890abcdef"
        };
        for(int i=0; i<data.length; i++) {
        	System.out.println("#" + i);
            byte[] data_hex = PosUtils.hexStringToBytes(data[i]);
            byte[] cipher = NFCard_EncData(AK_hex, data_hex, 0, data_hex.length, (byte) 0x0A/*padding*/);
	        System.out.println("  data= " + data[i]);
	        System.out.println("  cipher= " + PosUtils.bcdToString(cipher));

            byte[] plain = NFCard_DecData(AK_hex, cipher, 0, cipher.length);
	        System.out.println("  plain= " + PosUtils.bcdToString(plain));
	    }        	
	}
}