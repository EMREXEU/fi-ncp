app = angular.module('fi-ncp', ['ngRoute', 'learningOpportunityRecursion'])

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

app.controller('courseSelection', function ($scope, $http, response) {
    var report = response.data.elmo.report;

    // learningOpportunity must be an array for working recursion..
    if (!angular.isArray(report.learningOpportunitySpecification))
        report.learningOpportunitySpecification = [{learningOpportunitySpecification: report.learningOpportunitySpecification}];
    $scope.learningOpportunities = report.learningOpportunitySpecification;

    console.log($scope.learningOpportunities);

    $scope.selectedIds = [];

    $scope.addId = function (id) {
        console.log('add id ' + id.id);
        if ($scope.selectedIds.indexOf(id.id) < 0)
            $scope.selectedIds.push(id.id);
    };

    $scope.removeId = function (id) {
        console.log('remove id ' + id.id);
        var index = $scope.selectedIds.indexOf(id.id);
        console.log(index)
        if (index >= 0)
            $scope.selectedIds.splice(index, 1);
    };


    $scope.sendIds = function () {
        $http({
            url: 'api/review',
            method: 'GET',
            params: {selectedIds: $scope.selectedIds}
        }).success(function (data) {
            console.log(data);
        });
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




