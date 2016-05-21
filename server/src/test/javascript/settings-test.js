/*
 Copyright 2015 Artem Stasiuk

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

describe('Settings persisting', function () {

    beforeEach(module('App'));

    var settings1 = undefined;

    beforeEach(inject(function (settings) {
        settings1 = settings;

        // clean up
        window.localStorage.removeItem('settings')
    }));

    it('should store nothing if passed settings is undefined', function () {
        settings1.store(null);
        expect(window.localStorage.getItem('settings')).toBe(null);
    });

    it('should store nothing if passed settings has no databases', function () {
        settings1.store({});
        expect(window.localStorage.getItem('settings')).toBe(null);
    });

    it('should store/restore min info about gigaspace', function () {
        settings1.store({
            gigaspaces: [{
                name: 'name'
            }]
        });

        expect(window.localStorage.getItem('settings')).toBe(
            '{"gigaspaces":[{"name":"name"}]}');

        function findPredefinedDatabase() {
            return true;
        }

        expect(settings1.restore(findPredefinedDatabase)).toEqual({
            gigaspaces: [{
                name: 'name',
                exportImportTab: {},
                typesTab: {},
                copyTab: {},
                queryTab: {editors: [{content: ''}], selectedEditor: {content: ''}},
                selectedTab: 'query'
            }]
        });
    });

    it('should store/restore max info about gigaspace', function () {
        settings1.store({
            gigaspaces: [{
                name: 'name',
                user: 'user',
                url: 'url',
                driver: 'driver',
                selectedTab: 'types',
                copyTab: {
                    targetUrl: 'copy-url',
                    targetUser: 'copy-u',
                    content: 'copy-c'
                },
                typesTab: {
                    hideZero: true,
                    byPartitions: true,
                    filter: 'some_filter',
                    selectedCount: 12
                },
                queryTab: {
                    editors: [{
                        name: 'nm',
                        content: 'ct',
                        cursor: 99,
                        height: 234
                    }],
                    selectedEditor: 2
                }
            }]
        });

        expect(window.localStorage.getItem('settings')).toBe(
            '{"gigaspaces":[{"name":"name","user":"user","driver":"driver","url":"url","selectedTab":"types",' +
            '"copyTab":{"targetUrl":"copy-url","targetUser":"copy-u","content":"copy-c"},' +
            '"typesTab":{"hideZero":true,"byPartitions":true,"filter":"some_filter","selectedCount":12},' +
            '"queryTab":{"editors":[{"name":"nm","content":"ct","cursor":99,"height":234}]}}]}');

        function findPredefinedDatabase() {
            return true;
        }

        expect(settings1.restore(findPredefinedDatabase)).toEqual({
            gigaspaces: [{
                name: 'name',
                user: 'user',
                url: 'url',
                driver: 'driver',
                selectedTab: 'types',
                copyTab: {targetUrl: 'copy-url', targetUser: 'copy-u', content: 'copy-c'},
                typesTab: {hideZero: true, byPartitions: true, filter: 'some_filter', selectedCount: 12},
                queryTab: {editors: [{name: 'nm', content: 'ct', cursor: 99, height: 234}]},
                exportImportTab: {}
            }]
        });
    });

    it('should store types tab info', function () {
        settings1.store({
            gigaspaces: [{
                name: 'name',
                typesTab: {
                    hideZero: true,
                    byPartitions: true,
                    filter: 'some_filter',
                    selectedCount: 12
                }
            }]
        });

        expect(window.localStorage.getItem('settings')).toBe(
            '{"gigaspaces":[{"name":"name","typesTab":{"hideZero":true,"byPartitions":true,"filter":"some_filter","selectedCount":12}}]}');
    });

    it('should query tab info', function () {
        settings1.store({
            gigaspaces: [{
                name: 'name',
                queryTab: {
                    editors: [{
                        name: 'nm',
                        content: 'ct',
                        cursor: 99,
                        height: 234
                    }],
                    selectedEditor: 2
                }
            }]
        });

        expect(window.localStorage.getItem('settings')).toBe(
            '{"gigaspaces":[{"name":"name","queryTab":{"editors":[{"name":"nm","content":"ct","cursor":99,"height":234}]}}]}');
    });

    it('should not store password', function () {
        settings1.store({
            gigaspaces: [{
                name: 'name',
                password: 'password',
                selectedTab: 1
            }]
        });

        expect(window.localStorage.getItem('settings')).toBe(
            '{"gigaspaces":[{"name":"name","selectedTab":1}]}');
    });

    it('should add database if stored does not have with that name', function () {
        window.localStorage.setItem('settings', '{"gigaspaces":[{"name":"a"}]}');

        settings1.store({
            gigaspaces: [{
                name: 'a'
            }, {
                name: 'b',
                user: 'user',
                url: 'url',
                driver: 'd',
                password: 'p',
                selectedTab: 1
            }]
        });

        expect(window.localStorage.getItem('settings')).toBe('{"gigaspaces":[{"name":"a"},{"name":"b","user":"user","driver":"d","url":"url","selectedTab":1}]}');
    });

    it('should remove stored database if not in to store', function () {
        window.localStorage.setItem('settings', '{"gigaspaces":[{"name":"a"}]}');

        settings1.store({
            gigaspaces: [{
                name: 'other',
                selectedTab: 1
            }]
        });

        expect(window.localStorage.getItem('settings')).toBe('{"gigaspaces":[{"name":"other","selectedTab":1}]}');
    });

    it('should store selected database index', function () {
        var gigaspace = {
            name: 'b',
            user: 'user',
            url: 'url',
            driver: 'd',
            password: 'p',
            selectedTab: 1
        };
        settings1.store({
            gigaspaces: [gigaspace],
            selectedGigaspace: gigaspace
        });

        expect(window.localStorage.getItem('settings')).toBe('{"gigaspaces":[{"name":"b","user":"user","driver":"d","url":"url","selectedTab":1}],"selectedGigaspace":0}');
    });

    it('should override stored database if stored settings corrupted', function () {
        window.localStorage.setItem('settings', '{"gig');

        settings1.store({
            gigaspaces: [{
                name: 'name',
                url: 'url',
                selectedTab: 1
            }]
        });

        expect(window.localStorage.getItem('settings')).toBe('{"gigaspaces":[{"name":"name","url":"url",' +
            '"selectedTab":1}]}');
    });

    describe('resolving conflicts', function () {
        it('should store database if it was changed by client', function () {
            window.localStorage.setItem('settings', '{"gigaspaces":[{"name":"old"}]}');
            settings1.loaded({name: 'old'});

            settings1.store({gigaspaces: [{name: 'old', url: 'u1'}]});

            expect(window.localStorage.getItem('settings')).toBe(
                '{"gigaspaces":[{"name":"old","url":"u1"}]}');
        });

        it('should store database if it is not in loaded (unexpected case)', function () {
            window.localStorage.setItem('settings', '{"gigaspaces":[{"name":"old"}]}');

            settings1.store({gigaspaces: [{name: 'old', url: 'u1'}]});

            expect(window.localStorage.getItem('settings')).toBe(
                '{"gigaspaces":[{"name":"old","url":"u1"}]}');
        });

        it('should store client version of database if it was changed outside and by client', function () {
            window.localStorage.setItem('settings', '{"gigaspaces":[{"name":"old"}]}');
            settings1.loaded({name: 'old'});

            settings1.store({gigaspaces: [{name: 'old', url: 'u1'}]});

            expect(window.localStorage.getItem('settings')).toBe(
                '{"gigaspaces":[{"name":"old","url":"u1"}]}');
        });
    });

    describe('restoring', function () {
        it('should restore default if nothing stored', function () {
            expect(settings1.restore()).toEqual({gigaspaces: []});
        });

        it('should restore default if corrupted stored', function () {
            window.localStorage.setItem('settings', 'aaa{:fwere');
            expect(settings1.restore()).toEqual({gigaspaces: []});
        });

        it('should not restore database if not its predefined', function () {
            window.localStorage.setItem('settings',
                '{"gigaspaces":[{"name":"my","url":"","queryTab":{"editors":[]}}, {"name": "a"}]}');

            function findPredefinedDatabase(name) {
                return name === 'my';
            }

            expect(settings1.restore(findPredefinedDatabase)).toEqual({
                gigaspaces: [{
                    name: 'my',
                    url: '',
                    queryTab: {editors: [{content: ''}], selectedEditor: {content: ''}},
                    selectedTab: 'query',
                    exportImportTab: {},
                    typesTab: {},
                    copyTab: {}
                }]
            });
        });

        it('should restore selected database', function () {
            window.localStorage.setItem('settings',
                '{"gigaspaces":[{"name":"my","url":"","selectedTab":1,"queryTab":{"editors":[]}}, {"name": "a"}],"selectedGigaspace":1}');

            function findPredefinedDatabase() {
                return true;
            }

            expect(settings1.restore(findPredefinedDatabase).selectedGigaspace.name).toEqual('a');
        });

        it('should restore selected editor', function () {
            window.localStorage.setItem('settings',
                '{"gigaspaces":[{"name":"my","url":"","selectedTab":1,"queryTab":{"editors":[{},{"content":"CT"}], "selectedEditor": 1}}],"selectedGigaspace":0}');

            function findPredefinedDatabase() {
                return true;
            }

            expect(settings1.restore(findPredefinedDatabase).selectedGigaspace
                .queryTab.selectedEditor.content).toEqual('CT');
        });
    });

});