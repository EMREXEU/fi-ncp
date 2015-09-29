app = angular.module('fi-ncp', ['ngRoute', 'courseSelection', '720kb.datepicker'])

app.config(function ($routeProvider, $httpProvider) {

    $routeProvider.
        when('/', {
            templateUrl: 'partials/courseSelection.html',
            controller: 'courseSelectionCtrl',
            resolve: {
                response: function ($http) {
                    return $http.get('/ncp/api/elmo/').success(function (response) {
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

});




app.controller('norex', function ($scope, $http) {
    $http.post('/norex/').success(function (data) {
    });
});

app.controller('login', function ($scope, $http) {
    $http.get('/login/').success(function (data) {
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
        $scope.greeting = data;
    })
});




