package cn.ygyg.cloudpayment.acp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager


class AcpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //不接受触摸屏事件
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if (savedInstanceState == null)
            Acp.getInstance(this).acpManager.checkRequestPermissionRationale(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Acp.getInstance(this).acpManager.checkRequestPermissionRationale(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Acp.getInstance(this).acpManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Acp.getInstance(this).acpManager.onActivityResult(this,requestCode, resultCode, data)
    }
}
