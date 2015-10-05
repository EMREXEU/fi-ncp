angular.module('api', [])
    .service('apiService', function ($http, $q, $sce) {
        var fixReports = function(reports)
        {
            // Report must be an array...
            if (!angular.isArray(reports))
                reports = [reports];

            angular.forEach(reports, function (report) {
                var hasPart = [];

                // let's modify array to make it compatible with recursion
                if (!angular.isArray(report.learningOpportunitySpecification))
                    hasPart.push({learningOpportunitySpecification: report.learningOpportunitySpecification})
                else
                    angular.forEach(report.learningOpportunitySpecification, function (specification) {
                        hasPart.push({learningOpportunitySpecification: specification});
                    });
                report.learningOpportunitySpecification = hasPart;
            });
            return reports;
        };

        var getElmoAll = function() {
            var deferred = $q.defer();
            $http.get('/ncp/api/elmo/').success(function (response) {
                var reports = fixReports(response.elmo.report);
                deferred.resolve(reports);
            }).error(function (error) {
                deferred.reject(error);
            });
            return deferred.promise;
        };

        var getElmoSelected = function(courses){
            var deferred = $q.defer();
            $http({
                url: '/ncp/api/elmo/',
                method: 'GET',
                params: {courses: courses}
            }).success(function (response) {
                deferred.resolve(response.elmo.report);
            }).error(function (error){
                deferred.reject(error);
            });
            return deferred.promise;
        };

        var getSubmitHtml = function(courses) {
            var deferred = $q.defer();
            $http({
                url: 'review',
                method: 'GET',
                params: {courses: courses}
            }).success(function (data) {
                deferred.resolve($sce.trustAsHtml(data));
            }).error(function (error){
                deferred.reject(error);
            });
            return deferred.promise;
        };



        return {getElmoAll: getElmoAll,
                getElmoSelected: getElmoSelected,
                getSubmitHtml : getSubmitHtml};

    }
);