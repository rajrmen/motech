/* localization service */

var localizationModule = angular.module('localization', []);

localizationModule.factory("i18nService", function() {
    'use strict';

    var service = {
        getMessage : function(key, value1, value2, value3, value4) {
            return jQuery.i18n.prop(key, value1, value2, value3, value4);
        },

        init : function(data) {
            jQuery.i18n.map = data;
        }
    };

    return service;
});
