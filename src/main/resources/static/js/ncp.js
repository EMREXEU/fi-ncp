app = angular.module('fi-ncp', ['ngRoute'])
    .directive('learningOpportunityCollection', function () {
        return {
            restrict: "E",
            replace: true,
            scope: {
                collection: '=',
                addIdFn: '&',
                removeIdFn: '&'
            },
            controller: function($scope) {
                console.log('COLLECTION ' + JSON.stringify($scope.collection));
            },
            templateUrl: "partials/learning-opportunity-collection.html",
            link: function (scope, elm, attrs) {
                scope.addId = function(id) {
                    console.log('add id in COLLECTION link ' + id);
                    scope.addIdFn({id : id})
                }

                scope.removeId = function(id) {
                    console.log('remove id in COLLECTION link ' + id);
                    scope.removeIdFn({id : id})
                }
            }
        }
    })
    .directive('learningOpportunity', function ($compile) {
        return {
            restrict: "E",
            replace: true,
            scope: {
                member: '=',
                addIdFn: '&',
                removeIdFn: '&'
            },
            controller: function ($scope) {

                console.log('MEMBER ' + JSON.stringify($scope.member));
                angular.forEach($scope.member.identifier, function (identifier) {
                    if (identifier.type == "elmo")
                        $scope.elmoIdentifier = identifier.content;
                });

                $scope.checkBoxChanged = function () {
                    console.log($scope.checked + ' ' + $scope.elmoIdentifier);
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
                    console.log('add id in MEMBER ' + id);
                    scope.addIdFn({id: id})
                }


                scope.removeId = function (id) {
                    console.log('remove id in MEMBER link ' + id);
                    scope.removeIdFn({id: id})

                }
            }

        }
    })

app.config(function ($routeProvider, $httpProvider) {

    $routeProvider.
        when('/', {
            templateUrl: 'partials/courseSelection.html',
            controller: 'courseSelection',
            resolve: {
                response: function ($http) {
                    return $http.get('/api/elmo/').success(function (response) {
                        return response;
                    });
                }
            }
        }).
        when('/login', {
            templateUrl: 'partials/login.html',
            controller: 'login'
        }).
        when('/doLogin', {
            //templateUrl: 'partials/login.html',
            controller: 'doLogin'
        }).
        when('/norex', {
            templateUrl: 'partials/login.html',
            controller: 'norex'
        }).
        when('/elmo', {
            templateUrl: 'partials/elmo.html',
            controller: 'elmo'
        }).
        otherwise({
            redirectTo: '/'
        });

    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

})
;

app.controller('courseSelection', function($scope, response) {
    var report = response.data.elmo.report;
    if (!angular.isArray(report.learningOpportunitySpecification))
        report.learningOpportunitySpecification = [{learningOpportunitySpecification : report.learningOpportunitySpecification}];
    $scope.learningOpportunities = report.learningOpportunitySpecification;
    $scope.selectedIds = [];

    $scope.addId = function(id) {
        console.log('add id ' + id.id);
        if ($scope.selectedIds.indexOf(id.id) < 0)
            $scope.selectedIds.push(id.id);
    };

    $scope.removeId = function(id) {
        console.log('remove id ' + id.id);
        var index = $scope.selectedIds.indexOf(id.id);
        console.log(index)
        if (index >= 0)
            $scope.selectedIds.splice(index,1);
    };
});

app.controller('norex', function ($scope, $http) {
    $http.post('/norex/').success(function (data) {
    });
});

app.controller('login', function ($scope, $http) {
    $http.get('/login/').success(function (data) {
        console.log(data);
        $scope.greeting = data;
    });
});

app.controller('doLogin', function ($scope, $http, $location) {
    $http.post('/doLogin/').success(function (data) {
        $scope.greeting = data;
        $location.path("/courseSelection");
    })
});

app.controller('elmo', function ($scope, $http) {
    $http.post('/elmo/').success(function (data) {
        console.log(data);
        $scope.greeting = data;
    })
});




