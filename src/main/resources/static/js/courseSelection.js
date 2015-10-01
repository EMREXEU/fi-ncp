angular.module('courseSelection', [])
    .controller('courseSelectionCtrl', function ($scope, $http, $sce, response) {
        $scope.selectedLanguage = "fi"; //

        var reports = response.data.elmo.report;

        // Report must be an array...
        if (!angular.isArray(reports))
            reports = [reports];

        $scope.reports = reports;

        var selectedCourseIds = [];
        $scope.educationInstitutionOptions = {}; // {'Helsinki University' : true, 'Oulu AMK' : true};
        $scope.typeOptions = {};
        $scope.levelOptions = [];

        var findOptionsRecursively = function (learningOpportunityArray, partOf) {
            angular.forEach(learningOpportunityArray, function (opportunityWrapper) {
                var opportunity = opportunityWrapper.learningOpportunitySpecification;

                if (opportunity.type)
                    $scope.typeOptions[opportunity.type] = true;

                if (opportunity.level) {
                    var indexOf = $scope.levelOptions.indexOf(opportunity.level)
                    if (indexOf < 0)
                        $scope.levelOptions.push(opportunity.level);
                }

                if (opportunity.hasPart)
                    findOptionsRecursively(opportunity.hasPart, opportunity)
            });
            return;
        };

        function getRightLanguage(titles) {
            var result = "";
            angular.forEach(titles, function (title) {
                if (title['xml:lang'] === $scope.selectedLanguage)
                    result = title['content'];
            });
            return result;
        };

        $scope.getRightLanguage = function(titles){
            return getRightLanguage(titles);
        };

        angular.forEach(reports, function (report) {
            $scope.learner = report.learner;
            var issuerTitle = getRightLanguage(report.issuer.title);
            $scope.educationInstitutionOptions[issuerTitle] = true;
            var hasPart = [];


            // let's modify array to make it compatible with recursion
            if (!angular.isArray(report.learningOpportunitySpecification))
                hasPart.push({learningOpportunitySpecification: report.learningOpportunitySpecification})
            else
                angular.forEach(report.learningOpportunitySpecification, function (specification) {
                    hasPart.push({learningOpportunitySpecification: specification});
                });
            report.learningOpportunitySpecification = hasPart;
            findOptionsRecursively(hasPart);
        });

        $scope.selectedIds = selectedCourseIds;

        $scope.addId = function (id) {
            if ($scope.selectedIds.indexOf(id) < 0)
                $scope.selectedIds.push(id);
        };

        $scope.removeId = function (id) {
            var index = $scope.selectedIds.indexOf(id);
            if (index >= 0)
                $scope.selectedIds.splice(index, 1);
        };

        $scope.sendIds = function () {
            $http({
                url: 'review',
                method: 'GET',
                params: {courses: $scope.selectedIds}
            }).success(function (data) {
                $scope.review = $sce.trustAsHtml(data);
            });
        };
    });