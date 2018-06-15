var commonDataController = {
    _ajaxHander: ajaxDataController(),
    _url: {
        //登录
        test:"/test"

    },
    test:function (params,callback) {
        this._ajaxHander.get(this._url.test, params, function(data) {
            callback(data);
        })
    }

};