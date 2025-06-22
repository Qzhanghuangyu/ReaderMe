package com.zhang.impl

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.template.IProvider
import com.zhang.common.service.IUser
import com.zhang.service.bean.UserInfo

@Route(path = "/user/getUserInfo")
class UserInfoImpl : IUser, IProvider {
    override fun getUserInfo(): UserInfo {
        return UserInfo("zhang",18)
    }

    override fun init(context: Context?) {
    }
}