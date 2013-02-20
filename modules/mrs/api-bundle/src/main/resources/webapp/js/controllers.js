'use strict';

/* Controllers */

function DashboardMrsCtrl($scope, Patients, $http) {

    $scope.patientsList = Patients.query();
    $scope.filteredItems = [];
    $scope.groupedItems = [];
    $scope.itemsPerPage = 10;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
    $scope.patientList = Patients.query();
    $scope.showPatientsView=true;
    $scope.selectedPatientView=true;

    $scope.patientDto = {};


    $scope.getPatient = function(patient)  {
        $scope.successfulMessage='';
        $scope.failureMessage='';
        $http.post('../mrs/api/patients/getPatient', patient.motechId).success(function(data) {
            $scope.patientDto = data;
        });
        $scope.showPatientsView=!$scope.selectedPatientView;
        $scope.selectedPatientView=!$scope.selectedPatientView;
    }


    var searchMatch = function (item, searchQuery) {
        if (!searchQuery) {
            return true;
        }
        return item.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1;
    };

    $scope.search = function () {

        $scope.filteredItems = $filter('filter')($scope.allPatients, function (item) {
            for(var attr in item) {
                if (searchMatch(item[attr], $scope.query))
                    return true;
            }
            return false;
        });
        $scope.currentPage = 0;
        $scope.groupToPages();
    };

    $scope.groupToPages = function () {
        $scope.pagedItems = [];

        for (var i = 0; i < $scope.filteredItems.length; i++) {
            if (i % $scope.itemsPerPage === 0) {
                $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
            } else {
                $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
            }
        }
    };

    $scope.range = function (start, end) {
        var ret = [];
        if (!end) {
            end = start;
            start = 0;
        }
        for (var i = start; i < end; i++) {
            ret.push(i);
        }
        return ret;
    };

    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
        }
    };

    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.pagedItems.length - 1) {
            $scope.currentPage++;
        }
    };

    $scope.setPage = function () {
        $scope.currentPage = this.number;
    };

}

function ManageMrsCtrl($scope, Patients, $routeParams, $http) {
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.personDto = {};
    $scope.facilityDto = {};
    $scope.containerDto = {};

    $scope.patientDto = Patients.query(function() {
        blockUI();
        if ($routeParams.mrsId != undefined) {
            $http.post('../mrs/api/patients/getPatient', $routeParams.mrsId).success(function(data) {
                $scope.patientDto = data;
                $scope.personDto = $scope.patientDto.person;
                $scope.facilityDto = $scope.patientDto.facility;
                $scope.patientDto.person = null;
                $scope.patientDto.facility = null;
            });
        } else {
            $scope.resetValues();
        }
        unblockUI();
    });

    $scope.resetValues = function() {
        $scope.patientDto = {
            patientId : "" ,
            facility : null,
            person : null
        };
        $scope.personDto = {
            personId : "",
            //dateOfBirth: null,
            birthDateEstimated: false,
            //age: 0,
            gender: "",
            dead: false,
            //deathDate: null,
            attributes: []
        };
        $scope.facilityDto = {
            name : "",
        };
    }


    $scope.save = function() {
        blockUI();
        $scope.containerDto.personDto = $scope.personDto;
        $scope.containerDto.patientDto = $scope.patientDto;
        $scope.containerDto.facilityDto = $scope.facilityDto;

        if ($routeParams.mrsId != undefined) {
            $http.post('../mrs/api/patients/update', $scope.containerDto).
                success(function () {
                    var loc, indexOf;
                    unblockUI();

                    motechAlert('mrs.success.saved', 'header.saved', function () {
                        loc = new String(window.location);
                        indexOf = loc.indexOf('#');

                        window.location = loc.substring(0, indexOf) + "#/dashboard";
                    });
                }).error(function () {
                    delete $scope.containerDto;

                    alertHandler('mrs.error.saved', 'header.error');
                    unblockUI();
                });
        } else {
            $http.post('../mrs/api/patients/save', $scope.containerDto).
                success(function () {
                    var loc, indexOf;
                    unblockUI();

                    motechAlert('mrs.success.saved', 'header.saved', function () {
                        loc = new String(window.location);
                        indexOf = loc.indexOf('#');

                        window.location = loc.substring(0, indexOf) + "#/dashboard";
                    });
                }).error(function () {
                    delete $scope.containerDto;
                    unblockUI();

                    alertHandler('mrs.error.saved', 'header.error');
                });
        }
    }

    $scope.cssClass = function(prop) {
        var msg = 'control-group';

        if (!$scope.hasValue(prop)) {
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
            default:
                break;
        }
    }

}
