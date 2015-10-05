angular.module('courseSelection')
    .controller('courseSelectionPreviewCtrl',  function ($scope, $sce, $http, courseSelectionService, apiService) {
        apiService.getSubmitHtml(courseSelectionService.selectedCourseIds).then(function(html) {
            $scope.review = html;
        }); // we could also handle errors...

        apiService.getElmoSelected(courseSelectionService.selectedCourseIds).then(function(reports){
            angular.forEach(reports, function (report) {
                $scope.learner = report.learner;
            });
        });


    });
