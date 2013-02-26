'use strict';

/* Services */

angular.module('patientsServices', ['ngResource']).factory('Patients', function ($resource) {
    return $resource('../mrs/api/patients');
});

angular.module('patientService', ['ngResource']).factory('Patient', function ($resource) {
    return $resource('../mrs/api/patients/:mrsId');
});