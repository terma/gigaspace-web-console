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

angular.module('App').factory('settings', [function () {

    var LOCAL_STORAGE_KEY = 'settings';

    /**
     * Map. Key is name of database. Value is hash of
     */
    var loaded = {};

    function getOrNull() {
        try {
            return angular.fromJson(window.localStorage.getItem(LOCAL_STORAGE_KEY));
        } catch (e) {
            // skip
        }
    }

    function getOrEmpty() {
        var settings;
        try {
            settings = angular.fromJson(window.localStorage.getItem(LOCAL_STORAGE_KEY));
        } catch (e) {
            // skip
        }
        return settings ? settings : {gigaspaces: []};
    }

    function findDatabaseByKey(settings, database) {
        if (settings && settings.gigaspaces) {
            for (var i = 0; i < settings.gigaspaces.length; i++) {
                if (settings.gigaspaces[i].name === database.name) return i;
            }
        }
        return -1;
    }

    function changedCompareToLoaded(database) {
        var l = loaded[database.name];
        if (l) return !angular.equals(toStore(database), l);
        else return true;
    }

    function toStore(database) {
        var toStoreDatabase = {name: database.name};

        if (database.user) toStoreDatabase.user = database.user;
        if (database.driver) toStoreDatabase.driver = database.driver;
        if (database.url) toStoreDatabase.url = database.url;

        toStoreDatabase.selectedTab = database.selectedTab;

        if (database.copyTab) {
            toStoreDatabase.copyTab = {
                targetUrl: database.copyTab.targetUrl,
                targetUser: database.copyTab.targetUser,
                content: database.copyTab.content
            };
        }

        if (database.exportImportTab) {
            toStoreDatabase.exportImportTab = {};
        }

        if (database.typesTab) {
            toStoreDatabase.typesTab = {
                hideZero: database.typesTab.hideZero,
                byPartitions: database.typesTab.byPartitions,
                filter: database.typesTab.filter,
                selectedCount: database.typesTab.selectedCount
            };
        }

        // copy editors
        if (database.queryTab) {
            toStoreDatabase.queryTab = {editors: []};

            for (var j = 0; j < database.queryTab.editors.length; j++) {
                // if selected store index
                if (database.queryTab.selectedEditor == database.queryTab.editors[j])
                    toStoreDatabase.queryTab.selectedEditor = j;

                var toStoreEditor = {
                    name: database.queryTab.editors[j].name,
                    content: database.queryTab.editors[j].content,
                    cursor: database.queryTab.editors[j].cursor,
                    height: database.queryTab.editors[j].height
                };

                toStoreDatabase.queryTab.editors.push(toStoreEditor);
            }
        }

        return toStoreDatabase;
    }

    return {

        /**
         * Public for test. Give you way to setup loaded state.
         */
        loaded: function (database) {
            loaded[database.name] = database;
        },

        store: function (settings) {
            log.log('starting store settings...');
            if (!settings || !settings.gigaspaces) {
                log.log('nothing to store. just skip');
                return;
            }

            var storedSettings = getOrEmpty();

            var newSettings = {gigaspaces: []};

            settings.gigaspaces.forEach(function (database, index) {
                var storedDatabaseIndex = findDatabaseByKey(storedSettings, database);
                if (storedDatabaseIndex > -1) {
                    if (changedCompareToLoaded(database)) {
                        newSettings.gigaspaces.push(toStore(database));
                        log.log(database.name + ' was update in storage');
                    } else {
                        newSettings.gigaspaces.push(storedSettings.gigaspaces[storedDatabaseIndex]);
                        log.log(storedSettings.gigaspaces[storedDatabaseIndex].name + ' just keep as is');
                    }
                } else {
                    newSettings.gigaspaces.push(toStore(database));
                    log.log(database.name + ' was added to storage');
                }

                if (settings.selectedGigaspace == database) newSettings.selectedGigaspace = index;
            });

            window.localStorage.setItem(LOCAL_STORAGE_KEY, angular.toJson(newSettings));
            log.log('settings were stored');
        },

        restore: function (findPredefinedDatabase) {
            log.log('start restoring settings...');

            var settings = {
                gigaspaces: []
            };

            function unsafeRestore() {
                var fromStore = getOrNull();
                if (fromStore) {
                    log.log('stored settings found, restoring...');
                    for (var i = 0; i < fromStore.gigaspaces.length; i++) {
                        var storedDatabase = fromStore.gigaspaces[i];

                        // fix restoring if needs
                        if (!storedDatabase.exportImportTab) storedDatabase.exportImportTab = {};
                        if (!storedDatabase.typesTab) storedDatabase.typesTab = {};
                        if (!storedDatabase.copyTab) storedDatabase.copyTab = {};
                        if (!storedDatabase.queryTab) storedDatabase.queryTab = {editors: []};
                        if (!storedDatabase.exportImportTab) storedDatabase.exportImportTab = {};
                        if (storedDatabase.selectedTab == void 0) storedDatabase.selectedTab = 'query';

                        if (storedDatabase.queryTab.editors.length === 0) {
                            storedDatabase.queryTab.editors.push({content: ''});
                            storedDatabase.queryTab.selectedEditor = 0;
                        }

                        // to track what db changed by user and what we need to store at the end keep original version
                        loaded[storedDatabase.name] = angular.copy(storedDatabase);

                        // check if restored predefinedGigaspace present in config other skip restore
                        if (findPredefinedDatabase(storedDatabase.name)) {
                            settings.gigaspaces.push(storedDatabase);

                            // restore link on selected predefinedGigaspace
                            if (fromStore.selectedGigaspace == i) settings.selectedGigaspace = storedDatabase;

                            if (fromStore.gigaspaces[i].queryTab.selectedEditor != void 0) {
                                // restore link on selected editor
                                settings.gigaspaces[i].queryTab.selectedEditor =
                                    fromStore.gigaspaces[i].queryTab.editors[fromStore.gigaspaces[i].queryTab.selectedEditor];
                            }

                            // create export if not present
                            if (!settings.gigaspaces[i].exportImportTab) settings.gigaspaces[i].exportImportTab = {};
                        }
                    }
                }
            }

            try {
                unsafeRestore();

                log.log('settings restored');
                log.log(this.gigaspaces);
            } catch (e) {
                log.log('can`t restore settings, go with empty');
                log.log(e);
            }

            return settings;
        }

    };
}]);