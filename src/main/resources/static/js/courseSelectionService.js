angular.module('courseSelection', [])
    .service('courseSelectionService', function () {
        var selectedCourseIds = [];


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
                removeId :removeId
    }});
