angular.module('courseSelection')
    .controller('courseSelectionPreviewCtrl',  function ($scope, $sce, $http, courseSelectionService) {
        $http({
            url: 'review',
            method: 'GET',
            params: {courses: courseSelectionService.selectedCourseIds}
        }).success(function (data) {
            $scope.review = $sce.trustAsHtml(data);
        });


    });
