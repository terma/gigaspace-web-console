/*
 Copyright 2015-2016 Artem Stasiuk

 Licensed under the Apache License, Version 2.0 (the 'License');
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an 'AS IS' BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

describe("Timestamp conversion", function () {

    beforeEach(module("App"));

    var $controller = undefined;

    beforeEach(inject(function (_$controller_) {
        $controller = _$controller_;
    }));

    it("from SEC (when less 10 digits)", function () {
        var $scope = {};
        $controller("controller", {$scope: $scope});

        var data = [
            ["100000000"]
        ];

        var columnIndex = 0;
        $scope.queryColumnToTimestamp(data, columnIndex);

        expect(data[0][columnIndex]).toBe("100000000 > Sat, 03 Mar 1973 09:46:40 GMT");
    });

    it("from SEC (when 10 digits)", function () {
        var $scope = {};
        $controller("controller", {$scope: $scope});

        var data = [
            ["1000000000"]
        ];

        var columnIndex = 0;
        $scope.queryColumnToTimestamp(data, columnIndex);

        expect(data[0][columnIndex]).toBe("1000000000 > Sun, 09 Sep 2001 01:46:40 GMT");
    });

    it("from MS", function () {
        var $scope = {};
        $controller("controller", {$scope: $scope});

        var data = [
            ["1000000000000"]
        ];

        var columnIndex = 0;
        $scope.queryColumnToTimestamp(data, columnIndex);

        expect(data[0][columnIndex]).toBe("1000000000000 > Sun, 09 Sep 2001 01:46:40 GMT");
    });


    it("from MICRO", function () {
        var $scope = {};
        $controller("controller", {$scope: $scope});

        var data = [
            ["10000000000000"]
        ];

        var columnIndex = 0;
        $scope.queryColumnToTimestamp(data, columnIndex);

        expect(data[0][columnIndex]).toBe("10000000000000 > Sun, 09 Sep 2001 01:46:40 GMT");
    });


    it("from more than MICRO", function () {
        var $scope = {};
        $controller("controller", {$scope: $scope});

        var data = [
            ["100000000000000"]
        ];

        var columnIndex = 0;
        $scope.queryColumnToTimestamp(data, columnIndex);

        expect(data[0][columnIndex]).toBe("100000000000000 > Sun, 09 Sep 2001 01:46:40 GMT");
    });

});