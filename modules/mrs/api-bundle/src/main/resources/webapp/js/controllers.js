'use strict';

/* Controllers */

function DashboardMrsCtrl($scope, Patients, $http, $routeParams) {
    $scope.filteredItems = [];
    $scope.groupedItems = [];
    $scope.itemsPerPage = 10;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
    $scope.patientList = Patients.query();
    $scope.showPatientsView=true;
    $scope.selectedPatientView=true;
    $scope.patientDto = {};

    $scope.getPatient = function (motechId) {
        $scope.successfulMessage='';
        $scope.failureMessage='';
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

function ManageMrsCtrl($scope, Patients, Patient, $routeParams, $http) {
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.personDto = {};
    $scope.facilityDto = {};
    $scope.containerDto = {};
    $scope.attributesDto = [];

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
                    delete $scope.containerDto;

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
                    delete $scope.containerDto;
                    unblockUI();

                    alertHandler('data.provider.mrs.error.saved', 'data.provider.mrs.header.error');
                });
        }
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
