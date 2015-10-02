angular.module('courseSelection', [])
    .service('courseSelectionService', function () {
        var selectedCourseIds = [];
        var selectedLanguage = "fi";

        function getRightLanguage(titles) {
            var result = "";
            angular.forEach(titles, function (title) {
                if (title['xml:lang'] === selectedLanguage)
                    result = title['content'];
            });
            return result;
        };

        var addId = function (id) {
            if (selectedCourseIds.indexOf(id) < 0)
                selectedCourseIds.push(id);
        };

        var removeId = function (id) {
            var index = selectedCourseIds.indexOf(id);
            if (index >= 0)
                selectedCourseIds.splice(index, 1);
        };

        return {selectedCourseIds: selectedCourseIds,
                addId : addId,
                removeId :removeId,
                getRightLanguage : getRightLanguage
    }});