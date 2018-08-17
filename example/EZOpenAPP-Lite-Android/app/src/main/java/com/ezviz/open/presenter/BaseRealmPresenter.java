package com.ezviz.open.presenter;

import com.ezviz.open.utils.DataManager;

import io.realm.Realm;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/12
 */
public abstract class BaseRealmPresenter extends BasePresenter{
    public Realm mRealm;
    public BaseRealmPresenter(){
        super();
        init();
    }
    public void init(){
        mRealm = DataManager.getInstance().getRealm();
    }
    public void release(){
        super.release();
        mRealm.close();
    }
}


