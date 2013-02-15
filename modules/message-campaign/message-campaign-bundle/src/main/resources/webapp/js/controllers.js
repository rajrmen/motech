
function CampaignsCtrl($scope, Campaigns) {

    $scope.$on('$viewContentLoaded', function () {
        $scope.campaigns = Campaigns.query();
    });
}

function EnrollmentsCtrl($scope, Enrollments) {

    $scope.$on('$viewContentLoaded', function () {
        $scope.enrollments = Enrollments.query();
    });
}
