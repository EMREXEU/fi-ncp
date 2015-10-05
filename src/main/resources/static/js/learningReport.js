angular.module('learningReport', [])
    .directive('learningReportDirective', function (courseSelectionService) {
        return {
            restrict: "E",
            replace: true,
            scope: {
                report: '=',
                typeFilter: '=',
                levelFilter: '=',
                onlyViewing: '='
            },
            templateUrl: 'partials/learningReport.html',
            controller: function ($scope) {

                if (!angular.isArray($scope.report.learningOpportunitySpecification))
                    $scope.report.learningOpportunitySpecification = [{learningOpportunitySpecification: $scope.report.learningOpportunitySpecification}];

                $scope.getRightLanguage = courseSelectionService.getRightLanguage;

                $scope.selectedTypes = function (report) {
                    if ($scope.onlyViewing)
                        return true;
                    else
                        return $scope.typeFilter[report.type];
                };

                $scope.selectedLevel = function (report) {
                    if ($scope.onlyViewing || $scope.levelFilter == "Any")
                        return true;
                    else
                        return $scope.levelFilter == report.level;
                };


                function recursiveOpportunityFlattening(learningOpportunityArray, partOf) {
                    angular.forEach(learningOpportunityArray, function (opportunityWrapper) {

                            // in some cases
                            if (opportunityWrapper.learningOpportunitySpecification)
                                var opportunity = opportunityWrapper.learningOpportunitySpecification;
                            else
                                var opportunity = opportunityWrapper;

                            // Add properties for table
                            opportunity.selected = true;

                            // Add Elmo identifier
                            if (angular.isArray(opportunity.identifier))
                                angular.forEach(opportunity.identifier, function (identifier) {
                                    if (identifier.type == "elmo")
                                        opportunity.elmoIdentifier = identifier.content;
                                })
                            else
                                opportunity.elmoIdentifier = opportunity.identifier.content;

                            // Find parents Elmo identifier
                            if (partOf)
                                opportunity.partOf = partOf.elmoIdentifier
                            else
                                opportunity.partOf = '-';

                            flatArray.push(opportunity);
                            courseSelectionService.addId(opportunity.elmoIdentifier); // all are selected at the beginning

                            // Recursion
                            if (opportunity.hasPart)
                                recursiveOpportunityFlattening(opportunity.hasPart, opportunity)
                    });
                    return flatArray;
                };

                var flatArray = [];
                flatArray = recursiveOpportunityFlattening($scope.report.learningOpportunitySpecification);
                $scope.flattenedLearningOpportunities = flatArray;

                $scope.issuerName = courseSelectionService.getRightLanguage($scope.report.issuer.title);

                var selectParent = function (child) {
                    if (child.partOf != '-')
                        angular.forEach($scope.flattenedLearningOpportunities, function (possibleParent) {
                            if (possibleParent.elmoIdentifier == child.partOf) {
                                possibleParent.selected = true;
                                $scope.checkBoxChanged(possibleParent);
                            }
                        });
                };

                var deselectChilds = function (parent) {
                    angular.forEach($scope.flattenedLearningOpportunities, function (possibleChild) {
                        if (parent.elmoIdentifier == possibleChild.partOf) {
                            possibleChild.selected = false;
                            $scope.checkBoxChanged(possibleChild);
                        }
                    })
                }


                $scope.checkBoxChanged = function (opportunity) {
                    if (opportunity.selected) {
                        courseSelectionService.addId(opportunity.elmoIdentifier)
                        selectParent(opportunity);

                    }
                    else {
                        courseSelectionService.removeId(opportunity.elmoIdentifier);
                        deselectChilds(opportunity);
                    }
                }
            }
        }
    });
