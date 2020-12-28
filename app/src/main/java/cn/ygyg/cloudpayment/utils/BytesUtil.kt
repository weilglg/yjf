package cn.ygyg.cloudpayment.utils

object BytesUtil {
    /**
     * 拼接字节到字节数组中
     * @param paramArrayOfByte 原始字节数组
     * @param paramByte        要拼接的字节
     * @return 拼接后的数组
     */
    fun mergerArray(paramArrayOfByte: ByteArray, paramByte: Byte): ByteArray {
        val arrayOfByte = ByteArray(paramArrayOfByte.size + 1)
        System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.size)
        arrayOfByte[paramArrayOfByte.size] = paramByte
        return arrayOfByte
    }

    /**
     * 两个字节数组拼接
     *
     * @param paramArrayOfByte1 字节数组1
     * @param paramArrayOfByte2 字节数组2
     * @return 拼接后的数组
     */
    fun mergerArray(paramArrayOfByte1: ByteArray, paramArrayOfByte2: ByteArray): ByteArray {
        val arrayOfByte = ByteArray(paramArrayOfByte1.size + paramArrayOfByte2.size)
        System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.size)
        System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, paramArrayOfByte1.size, paramArrayOfByte2.size)
        return arrayOfByte
    }

    /**
     * 字节数组拆分
     *
     * @param paramArrayOfByte 原始数组
     * @param paramInt1        起始下标
     * @param paramInt2        要截取的长度
     * @return 处理后的数组
     */
    fun subBytes(paramArrayOfByte: ByteArray, paramInt1: Int, paramInt2: Int): ByteArray {
        val arrayOfByte = ByteArray(paramInt2)
        var i = 0
        while (true) {
            if (i >= paramInt2) return arrayOfByte
            arrayOfByte[i] = paramArrayOfByte[i + paramInt1]
            i += 1
        }
    }
}