var module = angular.module('learningOpportunityRecursion', []);

module.directive('learningOpportunityCollection', function () {
        return {
            restrict: "E",
            replace: true,
            scope: {
                collection: '=',
                addIdFn: '&',
                removeIdFn: '&'
            },
            templateUrl: "partials/learning-opportunity-collection.html",
            link: function (scope, elm, attrs) {
                scope.addId = function(id) {
                    scope.addIdFn({id : id})
                }

                scope.removeId = function(id) {
                    scope.removeIdFn({id : id})
                }
            }
        }
    });

module.directive('learningOpportunity', function ($compile) {
    return {
        restrict: "E",
        replace: true,
        scope: {
            member: '=',
            addIdFn: '&',
            removeIdFn: '&'
        },
        controller: function ($scope) {
            console.log('MEMBER ' + $scope.member);
            if (angular.isArray($scope.member.identifier))
                angular.forEach($scope.member.identifier, function (identifier) {
                    if (identifier.type == "elmo")
                        $scope.elmoIdentifier = identifier.content;
                })
            else
                $scope.elmoIdentifier = $scope.member.identifier.content;

            $scope.checkBoxChanged = function () {
                if ($scope.checked)
                    $scope.addId({id: $scope.elmoIdentifier})
                else
                    $scope.removeId({id: $scope.elmoIdentifier});
            };
        },
        templateUrl: "partials/learning-opportunity.html",
        link: function (scope, element, attrs) {

            if (angular.isArray(scope.member.hasPart)) {
                element.append("<learning-opportunity-collection add-id-fn='addId(id)' remove-id-fn='removeId(id)' collection='member.hasPart'></learning-opportunity-collection>");
                $compile(element.contents())(scope)
            }

            scope.addId = function (id) {
                scope.addIdFn({id: id})
            }

            scope.removeId = function (id) {
                scope.removeIdFn({id: id})
            }
        }

    }
})