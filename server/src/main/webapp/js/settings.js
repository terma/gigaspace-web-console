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

    return {

        store: function (settings) {
            log.log('starting store settings');

            var toStore = {
                gigaspaces: []
            };

            for (var i = 0; i < settings.gigaspaces.length; i++) {
                var toStoreGigaspace = {
                    name: settings.gigaspaces[i].name,
                    user: settings.gigaspaces[i].user,
                    url: settings.gigaspaces[i].url,
                    driver: settings.gigaspaces[i].driver,
                    selectedTab: settings.gigaspaces[i].selectedTab,

                    typesTab: {
                        hideZero: settings.gigaspaces[i].typesTab.hideZero,
                        filter: settings.gigaspaces[i].typesTab.filter,
                        selectedCount: settings.gigaspaces[i].typesTab.selectedCount
                    },

                    copyTab: {
                        targetUrl: settings.gigaspaces[i].copyTab ? settings.gigaspaces[i].copyTab.targetUrl : undefined,
                        targetUser: settings.gigaspaces[i].copyTab ? settings.gigaspaces[i].copyTab.targetUser : undefined,
                        content: settings.gigaspaces[i].copyTab ? settings.gigaspaces[i].copyTab.content : undefined
                    },

                    queryTab: {
                        editors: []
                    }
                };
                toStore.gigaspaces.push(toStoreGigaspace);

                // if selected store index
                if (settings.selectedGigaspace == settings.gigaspaces[i]) toStore.selectedGigaspace = i;

                // copy editors
                for (var j = 0; j < settings.gigaspaces[i].queryTab.editors.length; j++) {
                    // if selected store index
                    if (settings.gigaspaces[i].queryTab.selectedEditor == settings.gigaspaces[i].queryTab.editors[j])
                        toStoreGigaspace.queryTab.selectedEditor = j;

                    var toStoreEditor = {
                        name: settings.gigaspaces[i].queryTab.editors[j].name,
                        content: settings.gigaspaces[i].queryTab.editors[j].content,
                        cursor: settings.gigaspaces[i].queryTab.editors[j].cursor,
                        height: settings.gigaspaces[i].queryTab.editors[j].height
                    };

                    toStoreGigaspace.queryTab.editors.push(toStoreEditor);
                }
            }

            window.localStorage.setItem(LOCAL_STORAGE_KEY, angular.toJson(toStore));
            log.log('settings were stored');
        },

        restore: function (findPredefinedGigaspace) {
            log.log('start restoring settings...');

            var settings = {
                gigaspaces: []
            };

            function unsafeRestore() {
                var fromStore = angular.fromJson(window.localStorage.getItem(LOCAL_STORAGE_KEY));
                if (fromStore) {
                    log.log('stored settings found, restoring...');
                    for (var i = 0; i < fromStore.gigaspaces.length; i++) {
                        var fromStoreGigaspace = fromStore.gigaspaces[i];

                        // check if restored predefinedGigaspace present in config other skip restore
                        if (findPredefinedGigaspace(fromStoreGigaspace.name)) {
                            settings.gigaspaces.push(fromStoreGigaspace);

                            // restore link on selected predefinedGigaspace
                            if (fromStore.selectedGigaspace == i) settings.selectedGigaspace = fromStoreGigaspace;

                            // restore link on selected editor
                            settings.gigaspaces[i].queryTab.selectedEditor =
                                fromStore.gigaspaces[i].queryTab.editors[fromStore.gigaspaces[i].queryTab.selectedEditor];

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