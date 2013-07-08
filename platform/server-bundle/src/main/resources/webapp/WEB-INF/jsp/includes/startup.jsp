<div class="bodywrap" ng-controller="StartupCtrl">
    <div class="startup">
        <div class="startup-strip">
            <div class="control-group">
                <h2 class="title ng-binding">{{msg('welcome.startup')}}</h2>
            </div>
        </div>
        <div class="clearfix"></div>
        <div class="startup-form">
            <div class="diver">
                <form class="form-horizontal">
                    <div class="control-group">
                        <label class="control-label">{{msg('select.language')}}</label>
                        <div class="controls">
                            <span ng-repeat="(key, value) in languages">
                                <input ng-click="setUserLang(key)" type="radio" value="key" name="language" ng-checked="settings.language == key"/><i class="flag flag-{{key}} label-flag-radio"></i>
                            </span>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">{{msg('enter.queueUrl')}}</label>
                        <div class="controls">
                            <input type="text" class="input-large" name="queueUrl" ng-model="settings.queueUrl"/>
                            <div id="queue.urls">
                                <div ng-repeat="url in suggestions.queueUrls" id="queue.url.{{$index + 1}}">
                                    <span><i>{{msg('suggestion')}} #{{$index + 1}}: </i>{{url}}</span>
                                    <button ng-click="settings.queueUrl = url" type="button" class="btn btn-mini">{{msg('use')}}</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">{{msg('enter.schedulerUrl')}}</label>
                        <div class="controls">
                            <input type="text" class="input-large" name="schedulerUrl" ng-model="settings.schedulerUrl"/>
                            <div id="scheduler.urls">
                                <div ng-repeat="url in suggestions.schedulerUrls" id="scheduler.url.{{$index + 1}}">
                                    <span><i>{{msg('suggestion')}} #{{$index + 1}}: </i>{{url}}</span>
                                    <button ng-click="settings.schedulerUrl = url" type="button" class="btn btn-mini">{{msg('use')}}</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">{{msg('select.loginMode')}}</label>
                        <div class="controls">
                            <input type="radio" value="repository" name="loginMode" ng-click="settings.loginMode = 'repository'" ng-checked="settings.loginMode == 'repository'"><span>{{msg('repository')}}</span>
                            <input type="radio" value="openid" name="loginMode" ng-click="settings.loginMode = 'openid'" ng-checked="settings.loginMode == 'openid'"/><span>{{msg('openId')}}</span>
                        </div>
                    </div>
                    <div ui-if="settings.loginMode == 'repository'" class="control-group">
                        <label class="control-label">{{msg('enter.adminLogin')}}</label>
                        <div class="controls">
                            <input type="text" class="input-large" name="adminLogin" ng-model="settings.adminLogin"/>
                        </div>
                    </div>
                    <div ui-if="settings.loginMode == 'repository'" class="control-group">
                        <label class="control-label">{{msg('enter.adminPassword')}}</label>
                        <div class="controls">
                            <input type="password" class="input-large" name="adminPassword" ng-model="settings.adminPassword"/>
                        </div>
                    </div>
                    <div ui-if="settings.loginMode == 'repository'" class="control-group">
                        <label class="control-label">{{msg('enter.adminComfirmPassword')}}</label>
                        <div class="controls">
                            <input type="password" class="input-large" name="adminConfirmPassword" ng-model="settings.adminConfirmPassword"/>
                        </div>
                    </div>
                    <div ui-if="settings.loginMode == 'repository'" class="control-group">
                        <label class="control-label">{{msg('enter.adminEmail')}}</label>
                        <div class="controls">
                            <input type="email" class="input-large" name="adminEmail" ng-model="settings.adminEmail"/>
                        </div>
                    </div>
                    <div ui-if="settings.loginMode == 'openid'" class="control-group">
                        <label class="control-label">{{msg('enter.providerName')}}</label>
                        <div class="controls">
                            <input type="text" class="input-large" name="providerName" ng-model="settings.providerName"/>
                        </div>
                    </div>
                    <div ui-if="settings.loginMode == 'openid'" class="control-group">
                        <label class="control-label">{{msg('enter.providerUrl')}}</label>
                        <div class="controls">
                            <input type="text" class="input-large" name="providerUrl" ng-model="settings.providerUrl"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <div class="controls">
                            <button ng-click="saveSettings()" class="btn btn-primary" type="submit">
                                {{msg('submit')}}
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
