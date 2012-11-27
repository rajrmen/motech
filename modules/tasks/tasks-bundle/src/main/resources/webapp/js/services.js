'use strict';

/* Services */

angular.module('channelServices', ['ngResource']).factory('Channels', function($resource) {
    return $resource('../tasks/api/channel');
});

angular.module('taskServices', ['ngResource']).factory('Tasks', function($resource) {
    return $resource('../tasks/api/task/:taskId', {taskId: '@_id'});
});