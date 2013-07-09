<div class="bodywrap" ng-controller="MasterCtrl">
    <div id="content" class="container-fluid">
        <div class="row-fluid">
            <motech-modules></motech-modules>

            <div id="main-content" class="span10">
                <div>
                    <div ui-if="currentModule.criticalMessage" id="criticalNotification" class="alert alert-error">
                        {{currentModule.criticalMessage}}
                    </div>
                    <div id="module-content" ui-view></div>
                </div>
            </div>

        </div>
    </div>

    <div id="footer">
        <span class="inside">
            <strong> {{msg('server.localTime')}}&#58; </strong> <span server-time> </span>&#59;
            <strong> {{msg('server.time')}}&#58; </strong><span server-up-time> </span>&#59;
            <strong> {{msg('projectVersion')}}&#58; </strong> {{msg('version')}}
        </span>
    </div>

</div>
