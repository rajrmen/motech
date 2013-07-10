angular.element(document).ready(function () {
    $.when(
        $.post('getModulesWithoutSubMenu'),
        $.post('getModulesWithSubMenu')
    ).done(function (data1, data2) {
        var modules = $.merge($.merge([], data1[0]), data2[0]),
            toLoad = 0,
            loaded = 0,
            angularModules = ['motech-dashboard'],
            header = [],
            onLoad = function () {
                loaded += 1;

                if (toLoad === loaded && angularModules.length > 0) {
                    try {
                        angular.bootstrap(document, angularModules);
                    } catch (err) {
                        alert(err.message);
                    }
                }
            };

        angular.forEach(modules, function (module) {
            $.merge(angularModules, module.angularModules);

            $.merge(header, angular.element(module.header).filter(function () {
                return $(this).is('link') || $(this).is('script');
            }));
        });

        toLoad = header.length;

        angular.forEach(header, function (entry) {
            var element, elem = $(entry);

            if (elem.is('link')) {
                element = document.createElement('link');
                element.setAttribute('rel', 'stylesheet');
                element.setAttribute('type', 'text/css');
                element.setAttribute('href', elem.attr('href'));
            } else if (elem.is('script')) {
                element = document.createElement('script');
                element.setAttribute('type', 'text/javascript');
                element.setAttribute('src', elem.attr('src'));
            }

            if (element !== undefined) {
                element.onreadystatechange = function () {
                    if (this.readyState === 'complete') {
                        onLoad();
                    }
                };

                element.onload = onLoad;
                document.getElementsByTagName("head")[0].appendChild(element);
            }
        });
    });
});
