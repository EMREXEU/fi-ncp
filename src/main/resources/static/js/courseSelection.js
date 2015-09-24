angular.module('courseSelection', [])
    .controller('courseSelectionCtrl', function ($scope, $http, $sce, response) {

        var flatArray = [];
        var ids = [];

        var recursiveOpportunityFlattening = function (learningOpportunityArray, partOf) {
            angular.forEach(learningOpportunityArray, function (opportunityWrapper) {
                var opportunity = opportunityWrapper.learningOpportunitySpecification;

                opportunity.selected = true;

                // Find Elmo identifier
                if (angular.isArray(opportunity.identifier))
                    angular.forEach(opportunity.identifier, function (identifier) {
                        if (identifier.type == "elmo")
                            opportunity.elmoIdentifier = identifier.content;
                    })
                else
                    opportunity.elmoIdentifier = opportunity.identifier.content;

                if (partOf)
                    opportunity.partOf = partOf.elmoIdentifier
                else
                    opportunity.partOf = '-';

                flatArray.push(opportunity);
                ids.push(opportunity.elmoIdentifier); // all items are first selected

                // Recursion. Nomnom
                if (opportunity.hasPart)
                    recursiveOpportunityFlattening(opportunity.hasPart, opportunity)
            });
            return flatArray;
        };

        var report = response.data.elmo.report;
        $scope.learner = report.learner;

        // learningOpportunity must be an array for working recursion..
        if (!angular.isArray(report.learningOpportunitySpecification))
            report.learningOpportunitySpecification = [{learningOpportunitySpecification: report.learningOpportunitySpecification}];

        flatArray = recursiveOpportunityFlattening(report.learningOpportunitySpecification);

        $scope.flattenedLearningOpportunities = flatArray;
        $scope.selectedIds = ids;

        $scope.checkBoxChanged = function (opportunity) {
            if (opportunity.selected)
                $scope.addId(opportunity.elmoIdentifier)
            else
                $scope.removeId(opportunity.elmoIdentifier);
        };

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