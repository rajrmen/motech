'use strict';

angular.module('CampaignService', ['ngResource']).factory('Campaigns', function ($resource) {
    return $resource('../messagecampaign/campaigns/all');
});

angular.module('EnrollmentService', ['ngResource']).factory('Enrollments', function ($resource) {
    return $resource('../messagecampaign/enrollments/all');
});
