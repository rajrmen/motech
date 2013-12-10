(function () {
    'use strict';

    /* App Module */

    angular.module('motech-web-security', ['motech-dashboard', 'roleService', 'userService', 'permissionService', 'ngCookies', 'ngRoute', 'bootstrap','motech-widgets']).config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/users', {templateUrl: '../websecurity/partials/user.html', controller: 'UserCtrl'}).
                when('/roles', {templateUrl: '../websecurity/partials/role.html', controller: 'RolePermissionCtrl'}).
                when('/permissions', {templateUrl: '../websecurity/partials/permission.html', controller: 'RolePermissionCtrl'}).
                when('/profile/:username', {templateUrl: '../websecurity/partials/profile.html', controller: 'ProfileCtrl'}).
                otherwise({redirectTo: '/welcome'});
    }]).filter('filterPagination', function() {
        return function(input, start) {
            start= +start;
            return input.slice(start);
        };}).
    filter('repeat', function(){return function(input, total) {var i; total = parseInt(total, 10);for (i=0; i<total; i+=1) { input.push(i);} return input;};}).
    directive('roleNameValidate', function(){
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
               ctrl.$parsers.unshift(function(viewValue) {
                        var names = [], i;
                        for (i=0; i<scope.roleList.length; i+=1) {
                            names.push(scope.roleList[i].roleName);
                        }
                        if(scope.addOrEdit === "edit"){
                            names.removeObject(scope.role.originalRoleName);
                        }
                        if(names.indexOf(viewValue)===-1) {
                            scope.pwdNameValidate=true;
                            ctrl.$setValidity('pwd', true);
                            return viewValue;
                        } else {
                            scope.pwdNameValidate=false;
                            ctrl.$setValidity('pwd', false);
                            return viewValue;
                        }
               });
            }
        };
    }).directive('permNameValidate', function(){
         return {
             require: 'ngModel',
             link: function(scope, elm, attrs, ctrl) {
                ctrl.$parsers.unshift(function(viewValue) {
                     var names = [], i;
                     for (i=0; i<scope.permissionList.length; i+=1) {
                         names.push(scope.permissionList[i].permissionName);
                     }
                     if(names.indexOf(viewValue)===-1) {
                         scope.pwdNameValidate=true;
                         ctrl.$setValidity('pwd', true);
                         return viewValue;
                     } else {
                         scope.pwdNameValidate=false;
                         ctrl.$setValidity('pwd', false);
                         return viewValue;
                     }
                });
             }
         };
    }).directive('userNameValidate', function(){
           return {
               require: 'ngModel',
               link: function(scope, elm, attrs, ctrl) {
                  ctrl.$parsers.unshift(function(viewValue) {
                           var names = [], i;
                           for (i=0; i<scope.userList.length; i+=1) {
                               names.push(scope.userList[i].userName);
                           }
                           if(names.indexOf(viewValue)===-1) {
                               scope.pwdNameValidate=true;
                               ctrl.$setValidity('pwd', true);
                               return viewValue;
                           } else {
                               scope.pwdNameValidate=false;
                               ctrl.$setValidity('pwd', false);
                               return viewValue;
                           }
                  });
               }
           };
    }).directive('confirmPassword', function(){
         return {
                restrict: 'A',
                require: 'ngModel',
                link: function(scope, elm, attrs, ctrl) {

                    function validateEqual(confirmPassword, userPassword) {
                        if (confirmPassword === userPassword) {
                            ctrl.$setValidity('equal', true);
                            return confirmPassword;
                        } else {
                            ctrl.$setValidity('equal', false);
                            return undefined;
                        }
                    }

                    scope.$watch(attrs.confirmPassword, function(userViewPassword) {
                        validateEqual(ctrl.$viewValue, userViewPassword);
                    });

                    ctrl.$parsers.unshift(function(viewValue) {
                        return validateEqual(viewValue, scope.$eval(attrs.confirmPassword));
                    });

                    ctrl.$formatters.unshift(function(modelPassword) {
                        return validateEqual(modelPassword, scope.$eval(attrs.confirmPassword));
                    });
                }
         };
    }).directive('innerlayout', function() {
        return {
            restrict: 'EA',
            link: function(scope, elm, attrs) {
                var eastSelector;
                /*
                * Define options for inner layout
                */
                scope.innerLayoutOptions = {
                    name: 'innerLayout',
                    //resizeWithWindowDelay: 250, // delay calling resizeAll when window is *still* resizing
                    // resizeWithWindowMaxDelay: 2000, // force resize every XX ms while window is being resized
                    resizable: true,
                    slidable: true,
                    closable: true,
                    east__paneSelector: "#inner-east",
                    center__paneSelector: "#inner-center",
                    east__spacing_open: 6,
                    spacing_closed: 35,
                    //south__showOverflowOnHover: true,
                    // center__showOverflowOnHover: true,
                    east__size: 300,
                    showErrorMessages: true, // some panes do not have an inner layout
                    resizeWhileDragging: true,
                    center__minHeight: 100,
                    contentSelector: ".ui-layout-content",
                    togglerContent_open: '',
                    togglerContent_closed: '<div><i class="icon-caret-left button"></i></div>',
                    autoReopen: false, // auto-open panes that were previously auto-closed due to 'no room'
                    noRoom: true,
                    togglerAlign_closed: "top", // align to top of resizer
                    togglerAlign_open: "top",
                    togglerLength_open: 0,
                    togglerLength_closed: 35,
                    togglerTip_open: "Close This Pane",
                    togglerTip_closed: "Open This Pane",
                    east__initClosed: true,
                    initHidden: true
                    //isHidden: true
                };

                // create the page-layout, which will ALSO create the tabs-wrapper child-layout
                scope.innerLayout = elm.layout(scope.innerLayoutOptions);

                // BIND events to hard-coded buttons in the NORTH toolbar
                // scope.innerLayout.addCloseBtn( "#tbarCloseEast", "east" );
                }
            };
        });
}());

