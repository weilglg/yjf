package cn.ygyg.cloudpayment.net

/**
 * Created by Admin on 2019/4/18.
 */
object UrlConstants {
    /**
     * 验证手机
     */
    const val valPhone = "customerApi/user/valPhone"
    /**
     * 获取验证码
     */
    const val captcha = "customerApi/user/captcha"
    /**
     * 注册
     */
    const val register = "customerApi/user/register"
    /**
     * 账号密码登录
     */
    const val login = "customerApi/user/login"
    /**
     * 验证码登录
     */
    const val captchalogin = "customerApi/user/captchaLogin"
    /**
     * 忘记密码修改密码
     */
    const val forgetPwd = "customerApi/user/forgetPwd"

    /**
     * 城市列表
     */
    const val cityList = "customerApi/city/list"

    /**
     * 城市公司列表
     */
    const val companyList = "customerApi/company/list"

    /**
     * 获取用户信息
     */
    const val getMemberInfo = "customerApi/openid/queryMember"
    /**
     * 微信登录获取openId
     */
    const val getToken = "customerApi/openid/getToken"
    /**
     * 绑定手机号码
     */
    const val bindPhone = "customerApi/device/bindPhone"

    /**
     * 获取物联网表 信息
     */
    const val getDevice = "customerApi/device/getDevice"
    /**
     * 获取物联网表 信息
     */
    const val getBind = "customerApi/device/getBind"
    /**
     * 绑定物联网表
     */
    const val bindDevice = "customerApi/device/bindMeter"
    /**
     * 绑定物联网表 列表
     */
    const val deviceList = "customerApi/device/list"

    /**
     * 解除绑定物联网表
     */
    const val unbind = "customerApi/device/unbind"
    /**
     * 创建预支付订单
     */
    const val createOrder = "customerApi/payment/createOrder"
    /**
     * NB表缴费记录
     */
    const val rechargeQuery = "customerApi/payment/rechargeQuery"
    /**
     * 刷新Token
     */
    const val refreshToken = ""
    /**
     * 获取厂家配置
     */
    const val getPaymentInformation = "customerApi/payment/getAPPPaymentInformation"
    /**
     * 获取用户信息
     */
    const val getMemberInfo2 = "customerApi/user/getMemberInfo"
    const val getTelephone = "customerApi/company/getByCompanyCode"

    const val firstVerify = "customerApi/nfccard/firstVerify"

    const val secondVerify = "customerApi/nfccard/secondVerify"

    /**
     * 根据MZ区的数据换取合约信息
     */
    const val case2ReadDec = "customerApi/nfccard/case2ReadDec"
    /**
     * 换算接口
     */
    const val calculate = "customerApi/nfcrecharge/calculate"
    /**
     * NFC 预支付
     */
    const val nfcPayments = "customerApi/nfcrecharge/rechargeCost"
    /**
     * NFC卡表缴费记录
     */
    const val nfcRechargeQuery = "customerApi/nfcrecharge/listCardBillingRecharge"

    const val nfcWriteEecAndVerify = "customerApi/nfccard/case3WriteEecAndVerify"

    const val updateStatus = "/customerApi/nfcrecharge/updateStatus"

}
