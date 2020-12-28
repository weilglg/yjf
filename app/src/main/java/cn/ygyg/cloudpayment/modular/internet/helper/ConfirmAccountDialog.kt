package cn.ygyg.cloudpayment.modular.internet.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.ygyg.cloudpayment.R
import cn.ygyg.cloudpayment.app.Constants
import cn.ygyg.cloudpayment.modular.internet.vm.DeviceVM
import cn.ygyg.cloudpayment.utils.ConfigUtil

class ConfirmAccountDialog(context: Context) : Dialog(context) {
    private var userName: TextView
    private var payCostCompany: TextView
    private var accountName: TextView
    private var address: TextView
    private var balance: TextView
    private var confirm: TextView

    init {
        setContentView(R.layout.dialog_confirm_account)
        window?.let {
            it.decorView?.setPadding(0, 0, 0, 0)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setGravity(Gravity.BOTTOM)
            it.decorView?.setBackgroundColor(Color.TRANSPARENT)
        }

        userName = findViewById(R.id.user_name)
        payCostCompany = findViewById(R.id.pay_cost_company)
        accountName = findViewById(R.id.account_name)
        address = findViewById(R.id.address)
        balance = findViewById(R.id.balance)
        confirm = findViewById(R.id.confirm)
        findViewById<View>(R.id.close).setOnClickListener { dismiss() }
    }


    fun setOnConformClick(l: View.OnClickListener) {
        confirm.setOnClickListener(l)
    }

    fun setData(data: DeviceVM, deviceCode: String) {
        userName.text = data.userName()
        payCostCompany.text = ConfigUtil.getCompanyName()
        accountName.text = deviceCode
        address.text = data.deviceAddress()
        balance.text = data.deviceBalance()
    }
}