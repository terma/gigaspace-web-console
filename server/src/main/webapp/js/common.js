/*
 Copyright 2015-2016 Artem Stasiuk

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

function nonEmptyString(string) {
    return string && string.length > 0;
}

Array.prototype.mkString = function (delimiter) {
    var result = "";
    for (var i = 0; i < this.length; i++) {
        if (i > 0) result += delimiter;
        result += this[i];
    }
    return result;
};

if (!String.prototype.endsWith) {
    String.prototype.endsWith = function (searchString, position) {
        var subjectString = this.toString();
        if (position === undefined || position > subjectString.length) {
            position = subjectString.length;
        }
        position -= searchString.length;
        var lastIndex = subjectString.indexOf(searchString, position);
        return lastIndex !== -1 && lastIndex === position;
    };
}

String.prototype.closerIndexFromLeft = function (searchStrings, lastPosition) {
    var closerIndex = -1;
    for (var i = 0; i < searchStrings.length; i++) {
        var index = this.indexOf(searchStrings[i]);
        if (index + searchStrings[i].length < lastPosition && closerIndex < index) closerIndex = index;
    }
    return closerIndex;
};

function transformResponse(data, headers, status) {
    if (status == 200)  return angular.fromJson(data);
    else {
        var errorPrefix = "/* --- JSON STREAM --- ERROR DELIMITER --- */";
        var errorBegin = data.indexOf(errorPrefix);
        if (errorBegin < 0) return data; // as is

        try {
            return angular.fromJson(data.substring(errorBegin + errorPrefix.length));
        } catch (e) {
            return data; // as is
        }
    }
}