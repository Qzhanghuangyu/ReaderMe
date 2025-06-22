package com.zhang.post

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zhang.common.service.IUser

@Route(path = "/post/activity")
class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        findViewById<TextView>(R.id.tvPost).setOnClickListener {
            val userProvider =
                ARouter.getInstance().build("/user/getUserInfo").navigation() as? IUser
            Log.i("zhanghuanyu", userProvider?.getUserInfo().toString())
        }
    }
}