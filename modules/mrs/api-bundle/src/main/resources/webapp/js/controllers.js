'use strict';

/* Controllers */

function PatientMrsCtrl($scope, Patients, $http, $routeParams, $filter) {
    $scope.filteredItems = [];
    $scope.itemsPerPage = 3;
    $scope.resetItemsPagination();
    $scope.patientList = Patients.query();
    $scope.showPatientsView=true;
    $scope.selectedPatientView=true;
    $scope.patientDto = {};

    $scope.getPatient = function (motechId) {
        $http.post('../mrs/api/patients/getPatient', motechId).success(function(data) {
            $scope.patientDto = data;
        });
        $scope.showPatientsView=!$scope.selectedPatientView;
        $scope.selectedPatientView=!$scope.selectedPatientView;
    }

    if ($routeParams.mrsId != undefined) {
        $scope.getPatient($routeParams.mrsId);
    } else {
        $scope.patientsList = Patients.query();
    }

    var searchMatch = function (patient, searchQuery) {
        if (!searchQuery) {
            return true;
        }
        return patient.motechId.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
    };

    $scope.search = function () {
        $scope.filteredItems = $filter('filter')($scope.patientList, function (item) {
            if(item) {
                if (searchMatch(item, $scope.query))
                    return true;
            }
            return false;
        });
        $scope.setCurrentPage(0);
        $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
    };

    $scope.search();
}

function ManagePatientMrsCtrl($scope, Patient, $routeParams, $http) {
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.personDto = {};
    $scope.facilityDto = {};
    $scope.containerDto = {};
    $scope.attributesDto = [];
    $scope.temp = {};
    $scope.motechIdValidate=true;

    function resetValues() {
            $scope.patientDto = {
                patientId : "" ,
                facility : null,
                person : null
            };
            $scope.personDto = {
                personId : "",
                birthDateEstimated: false,
                gender: 'male',
                dead: false,
                attributes : []
            };
            $scope.facilityDto = {
                name : "",
            };
            $scope.attributesDto = [];
        }

    if ($routeParams.mrsId != undefined) {
        $scope.patientDto = Patient.get( { mrsId: $routeParams.mrsId }, function () {
            $scope.personDto = $scope.patientDto.person;

            $scope.attributesDto = [];
            if($scope.personDto.attributes){
                for (i = 0; i<$scope.personDto.attributes.length; i += 1) {
                    $scope.attributesDto.push($scope.personDto.attributes[i]);
                }
            }

            $scope.facilityDto = $scope.patientDto.facility;
            $scope.patientDto.person = null;
            $scope.patientDto.facility = null;
            $scope.personDto.attributes = [];
        });
    } else {
        resetValues();
    }

    $scope.save = function() {
        blockUI();

        $scope.containerDto.personDto = $scope.personDto;
        $scope.containerDto.patientDto = $scope.patientDto;
        $scope.containerDto.facilityDto = $scope.facilityDto;

        $scope.containerDto.attributesDto = [];
        if ($scope.attributesDto.length!=0) {
            for (i = 0; i < $scope.attributesDto.length; i += 1) {
                $scope.containerDto.attributesDto.push($scope.attributesDto[i]);
            }
        }

        if ($routeParams.mrsId != undefined) {
            $http.post('../mrs/api/patients/update', $scope.containerDto).
                success(function () {
                    var loc, indexOf;
                    unblockUI();

                    motechAlert('data.provider.mrs.success.saved', 'data.provider.mrs.header.saved', function () {
                        loc = new String(window.location);
                        indexOf = loc.indexOf('#');

                        window.location = loc.substring(0, indexOf) + "#/dashboard/" + $routeParams.mrsId;
                    });
                }).error(function () {
                    $scope.containerDto = {};

                    alertHandler('data.provider.mrs.error.saved', 'data.provider.mrs.header.error');
                    unblockUI();
                });
        } else {
            $http.post('../mrs/api/patients/save', $scope.containerDto).
                success(function () {
                    var loc, indexOf;
                    unblockUI();

                    motechAlert('data.provider.mrs.success.saved', 'data.provider.mrs.header.saved', function () {
                        loc = new String(window.location);
                        indexOf = loc.indexOf('#');

                        window.location = loc.substring(0, indexOf) + "#/dashboard";
                    });
                }).error(function () {
                    $scope.containerDto = {};
                    unblockUI();

                    alertHandler('data.provider.mrs.error.saved', 'data.provider.mrs.header.error');
                });
        }
    }

    $scope.change = function (motechId) {
        checkIsPatientExist(motechId);
    };

    function checkIsPatientExist (motechId) {
            $scope.motechIdValidate = true;
            $http.post('../mrs/api/patients/getPatient', motechId).success(function(data) {
                if (data == "") return $scope.motechIdValidate;
                else $scope.motechIdValidate = false;
            });
            return $scope.motechIdValidate;
        }

    $scope.validateForm = function() {
        return !($scope.form.$invalid || !$scope.motechIdValidate);
    }

    $scope.addAttribute = function() {
        $scope.attributesDto.push({})
    }

    $scope.removeAttribute = function(attribute) {
           $scope.attributesDto.removeObject(attribute);
    }

    $scope.cssClass = function(prop, option) {
        var msg = 'control-group';

        if (!$scope.hasValue(prop, option)) {
            msg = msg.concat(' error');
        }

        return msg;
    }

    $scope.hasValue = function(prop, option) {
        switch(option)
        {
            case '1':
                return $scope.patientDto.hasOwnProperty(prop) && $scope.patientDto[prop] != undefined;
            case '2':
                return $scope.personDto.hasOwnProperty(prop) && $scope.personDto[prop] != undefined;
            case '3':
                return $scope.facilityDto.hasOwnProperty(prop) && $scope.facilityDto[prop] != undefined;
 /*           case '4':
                return change(prop);*/
            default:
                break;
        }
    }

    $scope.cancel = function() {
        if ($routeParams.mrsId != undefined) {
            var url = "#/dashboard/" + $routeParams.mrsId;
            window.location.href = url;
        } else {
            window.location.href = '#/dashboard/';
        }

    }

}
