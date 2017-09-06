(function(ng, undefined) {'use strict';
    /** 模块定义 */
    ng.module('piscesApp', ['ngSanitize', 'ngAnimate', 'ugCommon', 'ugBootstrap', 'ugRequest', 'leoApi'])

    /** 全局配置 */
        .config(['$controllerProvider', '$sceProvider', function($controllerProvider, $sceProvider) {
            $controllerProvider.allowGlobals();
            $sceProvider.enabled(false);
        }])

        /** 全局控制器 */
        .controller('piscesCtrl', ['$scope', '$common', '$request', function($scope, $common, $request) {
            ng.extend($scope, $common, $request);
        }]);
})(window.angular);