var Logger = (function () {
    function Logger() {
    }

    Logger.printLog = function (type, msg) {
        switch (type) {
            case this.INFO:
                console.log(msg);
                break;
            case this.WARN:
                console.warn(msg);
                break;
            case this.ERROR:
                console.error(msg);
                break;
        }
    };
    Logger.log = function (type, className, methodName, params) {
        if (params && params.length === 0) {
            this.printLog(type, ">>> " + className + "#" + methodName + "()");
        }
        else {
            this.printLog(type, ">>> " + className + "#" + methodName + "()");
            for (var _i = 0, params_1 = params; _i < params_1.length; _i++) {
                var param = params_1[_i];
                this.printLog(type, param);
            }
            this.printLog(type, 'END <<<');
        }
    };
    Logger.info = function (className, methodName) {
        var params = [];
        for (var _i = 2; _i < arguments.length; _i++) {
            params[_i - 2] = arguments[_i];
        }

        this.log(this.INFO, className, methodName, params);
    };
    Logger.error = function (className, methodName) {
        var params = [];
        for (var _i = 2; _i < arguments.length; _i++) {
            params[_i - 2] = arguments[_i];
        }
        this.log(this.ERROR, className, methodName, params);
    };
    Logger.warn = function (tag, className, methodName) {
        var params = [];
        for (var _i = 3; _i < arguments.length; _i++) {
            params[_i - 3] = arguments[_i];
        }
        this.log(this.WARN, className, methodName, params);
    };
    Logger.INFO = 1;
    Logger.WARN = 2;
    Logger.ERROR = 3;
    return Logger;
}());