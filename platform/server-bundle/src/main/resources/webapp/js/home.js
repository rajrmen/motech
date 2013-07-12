angular.element(document).ready(function () {
    'use strict';

    $.when(
        $.post('getModulesWithoutSubMenu'),
        $.post('getModulesWithSubMenu')
    ).done(function (data1, data2) {
        var modules = $.merge($.merge([], data1[0]), data2[0]),
            angularModules = ['motech-dashboard'],
            scripts = [];

        angular.forEach(modules, function (module) {
            $.merge(angularModules, module.angularModules);

            angular.forEach(angular.element(module.header).filter('link'), function (entry) {
                var element = document.createElement('link'),
                    elem = $(entry);

                element.setAttribute('rel', 'stylesheet');
                element.setAttribute('type', 'text/css');
                element.setAttribute('href', elem.attr('href'));

                document.getElementsByTagName("head")[0].appendChild(element);
            });

            angular.forEach(angular.element(module.header).filter('script'), function (entry) {
                scripts.push($(entry).attr('src'));
            });
        });

        scripts.push(function () {
            if (angularModules.length > 0) {
                angular.bootstrap(document, angularModules);
            }
        });

        head.js.apply(null, $.map(scripts, function (entry) {
            return entry;
        }));

    });
});
