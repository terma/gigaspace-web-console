describe("UI query result presentation suite", function () {

    beforeEach(module("App"));

    var $controller = undefined;

    beforeEach(inject(function (_$controller_) {
        $controller = _$controller_;
    }));

    describe("Able to convert column with timestamp to user date", function () {

        it("from SEC (when less 10 digits)", function () {
            var $scope = {};
            $controller("controller", {$scope: $scope});

            var data = {
                data: [
                    ["100000000"]
                ]
            };

            var columnIndex = 0;
            $scope.queryColumnToTimestamp(data, columnIndex);

            expect(data.data[0][columnIndex]).toBe("100000000 > Sat, 03 Mar 1973 09:46:40 GMT");
        });

        it("from SEC (when 10 digits)", function () {
            var $scope = {};
            $controller("controller", {$scope: $scope});

            var data = {
                data: [
                    ["1000000000"]
                ]
            };

            var columnIndex = 0;
            $scope.queryColumnToTimestamp(data, columnIndex);

            expect(data.data[0][columnIndex]).toBe("1000000000 > Sun, 09 Sep 2001 01:46:40 GMT");
        });

        it("from MS", function () {
            var $scope = {};
            $controller("controller", {$scope: $scope});

            var data = {
                data: [
                    ["1000000000000"]
                ]
            };

            var columnIndex = 0;
            $scope.queryColumnToTimestamp(data, columnIndex);

            expect(data.data[0][columnIndex]).toBe("1000000000000 > Sun, 09 Sep 2001 01:46:40 GMT");
        });


        it("from MICRO", function () {
            var $scope = {};
            $controller("controller", {$scope: $scope});

            var data = {
                data: [
                    ["10000000000000"]
                ]
            };

            var columnIndex = 0;
            $scope.queryColumnToTimestamp(data, columnIndex);

            expect(data.data[0][columnIndex]).toBe("10000000000000 > Sun, 09 Sep 2001 01:46:40 GMT");
        });


        it("from more than MICRO", function () {
            var $scope = {};
            $controller("controller", {$scope: $scope});

            var data = {
                data: [
                    ["100000000000000"]
                ]
            };

            var columnIndex = 0;
            $scope.queryColumnToTimestamp(data, columnIndex);

            expect(data.data[0][columnIndex]).toBe("100000000000000 > Sun, 09 Sep 2001 01:46:40 GMT");
        });
    });

});