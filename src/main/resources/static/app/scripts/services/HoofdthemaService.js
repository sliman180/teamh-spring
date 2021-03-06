(function (angular) {

    "use strict";

    function HoofdthemaService($http) {

        var exports = {};

        exports.getOrganisatie = function (id) {

            return $http.get("/api/hoofdthemas/" + id + "/organisatie").then(function (response) {
                return response.data;
            });

        };

        exports.getSubthemas = function (id) {

            return $http.get("/api/hoofdthemas/" + id + "/subthemas").then(function (response) {
                return response.data;
            });

        };

        exports.allOfGebruiker = function (id) {

            return $http.get("/api/gebruikers/" + id + "/hoofdthemas").then(function (response) {
                return response.data;
            });

        };

        exports.all = function () {

            return $http.get("/api/hoofdthemas").then(function (response) {
                return response.data;
            });

        };

        exports.find = function (id) {

            return $http.get("/api/hoofdthemas/" + id).then(function (response) {
                return response.data;
            });

        };

        exports.create = function (hoofdthema) {

            return $http.post("/api/hoofdthemas", hoofdthema).then(function (response) {
                return response.data;
            });
        };

        exports.update = function (hoofdthema) {

            return $http.put("/api/hoofdthemas/" + hoofdthema.id, hoofdthema).then(function (response) {
                return response.data;
            });

        };

        exports.delete = function (id) {

            return $http.delete("/api/hoofdthemas/" + id).then(function (response) {
                return response.data;
            });

        };

        return exports;

    }

    angular.module("kandoe").factory("HoofdthemaService", HoofdthemaService);

})(window.angular);
