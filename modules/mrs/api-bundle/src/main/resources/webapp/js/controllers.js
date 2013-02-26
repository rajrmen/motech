'use strict';

/* Controllers */

function PatientMrsCtrl($scope, Patients, $http, $routeParams, $filter) {
    $scope.sortingOrder = 'motechId';
    $scope.reverse = false;
    $scope.filteredItems = [];
    $scope.itemsPerPage = 10;
    $scope.resetItemsPagination();
    $scope.showPatientsView=true;
    $scope.selectedPatientView=true;
    $scope.patientDto = {};
    $scope.selectedItem;

    $scope.getPatient = function (motechId) {
        $http.post('../mrs/api/patients/getPatient', motechId).success(function(data) {
            $scope.patientDto = data;
        });
        $scope.showPatientsView=!$scope.selectedPatientView;
        $scope.selectedPatientView=!$scope.selectedPatientView;
    }

    function getMrsProviders() {
        $http.post('../mrs/api/patientsAdapters/getAll').success(function(data) {
            $scope.mrsProvidersList = data;
        });
    }

    $scope.chooseAdapter = function () {
        $http.post('../mrs/api/patientsAdapters/set', $scope.selectedItem).success(function() {
            window.location.href = '#/patients/';
        });
    }

    if ($routeParams.mrsId != undefined) {
        $scope.getPatient($routeParams.mrsId);
    } else {
        $scope.patientsList = Patients.query();
        getMrsProviders();
    }

    var searchMatch = function (patient, searchQuery) {
        if (!searchQuery) {
            return true;
        } else if (patient.person.firstName.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1) {
            return true;
        } else if (patient.person.lastName.toLowerCase().indexOf(searchQuery.toLowerCase()) !== -1) {
            return true;
        } else
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
        if ($scope.sortingOrder !== '') {
            $scope.filteredItems = $filter('orderBy')($scope.filteredItems, $scope.sortingOrder, $scope.reverse);
        }
        $scope.setCurrentPage(0);
        $scope.groupToPages($scope.filteredItems, $scope.itemsPerPage);
    };
    $scope.sort_by = function (newSortingOrder) {
        if ($scope.sortingOrder == newSortingOrder) {
            $scope.reverse = !$scope.reverse;
        }

        $scope.sortingOrder = newSortingOrder;

        $('th img').each(function(){
            // icon reset
            $(this).removeClass().addClass('sorting-no');
        });

        $scope.sortingOrderClass = $scope.sortingOrder.replace("person.","");

        if ($scope.reverse)
            $('th.'+$scope.sortingOrderClass+' img').removeClass('sorting-no').addClass('sorting-desc');
        else
            $('th.'+$scope.sortingOrderClass+' img').removeClass('sorting-no').addClass('sorting-asc');
    };

    $scope.patientList = Patients.query({}, $scope.search);

}

function ManagePatientMrsCtrl($scope, Patient, $routeParams, $http) {
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.personDto = {};
    $scope.facilityDto = {};
    $scope.containerDto = {};
    $scope.attributesDto = [];
    $scope.motechIdValidate=true;

    var typingTimer;
    var doneTypingInterval = 3000;
    $scope.inProgress = false;

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

    $('#inputMotechId').keyup(function(){
        $scope.inProgress = true;
        clearTimeout(typingTimer);
        if ($('#inputMotechId').val) {
            typingTimer = setTimeout(checkIsPatientExist, doneTypingInterval);
        }
    });

    $('#inputMotechId').keydown(function(){
        $scope.inProgress = true;
    });

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
            $scope.inProgress = false;
            $('#inputMotechId').prop('readonly', true);
        });
    } else {
        resetValues();
        $scope.inProgress = false;
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

                        window.location = loc.substring(0, indexOf) + "#/patients/" + $routeParams.mrsId;
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

                        window.location = loc.substring(0, indexOf) + "#/patients";
                    });
                }).error(function () {
                    $scope.containerDto = {};
                    unblockUI();

                    alertHandler('data.provider.mrs.error.saved', 'data.provider.mrs.header.error');
                });
        }
    }

    function checkIsPatientExist () {
            $scope.motechIdValidate = true;
            $http.post('../mrs/api/patients/getPatient', $scope.patientDto.motechId).success(function(data) {
                if (data == "") return $scope.motechIdValidate;
                else $scope.motechIdValidate = false;
            });
            $scope.inProgress = false;
        }

    $scope.validateForm = function() {
        return !($scope.form.$invalid || !$scope.motechIdValidate || $scope.inProgress);
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
            var url = "#/patients/" + $routeParams.mrsId;
            window.location.href = url;
        } else {
            window.location.href = '#/patients/';
        }

    }

}
