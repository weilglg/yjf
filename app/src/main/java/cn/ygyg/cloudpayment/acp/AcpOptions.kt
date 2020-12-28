package cn.ygyg.cloudpayment.acp

/**
 * 请求权限配置
 */
class AcpOptions private constructor(builder: Builder) {
    val rationalMessage: String
    val deniedMessage: String
    val deniedCloseBtn: String
    val deniedSettingBtn: String
    val rationalBtnText: String
    val permissions: Array<out String>?
    val isDialogCancelable: Boolean
    val isDialogCanceledOnTouchOutside: Boolean

    init {
        rationalMessage = builder.mRationalMessage
        deniedMessage = builder.mDeniedMessage
        deniedCloseBtn = builder.mDeniedCloseBtn
        deniedSettingBtn = builder.mDeniedSettingBtn
        rationalBtnText = builder.mRationalBtn
        permissions = builder.mPermissions
        isDialogCancelable = builder.dialogCancelable
        isDialogCanceledOnTouchOutside = builder.dialogCanceledOnTouchOutside
    }

    class Builder {
        internal var mRationalMessage = DEF_RATIONAL_MESSAGE
        internal var mDeniedMessage = DEF_DENIED_MESSAGE
        internal var mDeniedCloseBtn = DEF_DENIED_CLOSE_BTN_TEXT
        internal var mDeniedSettingBtn = DEF_DENIED_SETTINGS_BTN_TEXT
        internal var mRationalBtn = DEF_RATIONAL_BTN_TEXT
        internal var mPermissions: Array<out String>? = null
        internal var dialogCancelable = false
        internal var dialogCanceledOnTouchOutside = false

        /**
         * 申请权限理由框提示语
         *
         * @param rationalMessage 申请权限理由框提示语
         * @return 当前对象
         */
        fun setRationalMessage(rationalMessage: String): Builder {
            mRationalMessage = rationalMessage
            return this
        }

        /**
         * 申请权限理由框按钮
         *
         * @param rationalBtnText 申请权限理由框按钮
         * @return 当前对象
         */
        fun setRationalBtn(rationalBtnText: String): Builder {
            this.mRationalBtn = rationalBtnText
            return this
        }

        /**
         * 拒绝框提示语
         *
         * @param deniedMessage 拒绝框提示语
         * @return 当前对象
         */
        fun setDeniedMessage(deniedMessage: String): Builder {
            mDeniedMessage = deniedMessage
            return this
        }

        /**
         * 拒绝框关闭按钮
         *
         * @param deniedCloseBtnText 拒绝框关闭按钮
         * @return 当前对象
         */
        fun setDeniedCloseBtn(deniedCloseBtnText: String): Builder {
            this.mDeniedCloseBtn = deniedCloseBtnText
            return this
        }

        /**
         * 拒绝框跳转设置权限按钮
         *
         * @param deniedSettingText 拒绝框跳转设置权限按钮
         * @return 当前对象
         */
        fun setDeniedSettingBtn(deniedSettingText: String): Builder {
            this.mDeniedSettingBtn = deniedSettingText
            return this
        }

        /**
         * 需要申请的权限
         *
         * @param mPermissions [android.Manifest.permission]
         * @return 当前对象
         */
        fun setPermissions(vararg mPermissions: String): Builder {
            this.mPermissions = mPermissions
            return this
        }

        fun setDialogCancelable(dialogCancelable: Boolean): Builder {
            this.dialogCancelable = dialogCancelable
            return this
        }

        fun setDialogCanceledOnTouchOutside(dialogCanceledOnTouchOutside: Boolean): Builder {
            this.dialogCanceledOnTouchOutside = dialogCanceledOnTouchOutside
            return this
        }

        fun build(): AcpOptions {
            if (this.mPermissions == null || this.mPermissions!!.size == 0) {
                throw IllegalArgumentException("mPermissions no found...")
            }
            return AcpOptions(this)
        }

        companion object {
            private val DEF_RATIONAL_MESSAGE = "此功能需要您授权，否则将不能正常使用"
            private val DEF_DENIED_MESSAGE = "您拒绝权限申请，此功能将不能正常使用，您可以去设置页面重新授权"
            private val DEF_DENIED_CLOSE_BTN_TEXT = "关闭"
            private val DEF_DENIED_SETTINGS_BTN_TEXT = "设置权限"
            private val DEF_RATIONAL_BTN_TEXT = "我知道了"
        }
    }
}
