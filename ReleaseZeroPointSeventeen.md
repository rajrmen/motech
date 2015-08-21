# New Features #
  * Tasks Module - iterations 3, 4, 5 (adds support for task dashboard, filters, and task history)
  * Couch MRS repository - support for Patient, Person, Provider
  * New integration tests: message campaign, CommCare, event logging, schedule tracking, admin, decision tree, MRS, outbox, pill reminder, alerts, appointments, scheduler
  * Spike multi-tenant cloud infrastructure


# Bug Fixes #
  * SMS HTTP bundle IT failing randomly
  * Repeating jobs with no end date calculate 2.1 million next possible dates for fire times
  * Move couchdb connector creation logic out of server-config
  * Offset campaigns don't support minutes
  * Compile error in source generated by motech archetype
  * Expose couch query service through OSGi in event logging bundle
  * debian (and yum) package installer does not leave the app in the running state
  * Warnings during Maven build
  * Startup does not work
  * motech archetype should include reference to nexus repo in generated pom
  * StartupIT fails because of 'Unable to acquire global lock for resolve.'
  * Display MOTECH version in footer of admin UI.
  * Split enabled and disabled tasks into tabs
  * Admin UI should report an error when an uploaded module has an error.
  * Small UI tweaks on "Manage Modules" page
  * Module names are not displayed until loading of angularjs
  * Admin UI should show only motech feature bundles
  * Improve the UI validation filters in tasks
  * Improving the UI - SMS module
  * Fixes for Voxeo Module

For the complete list of cards that were completed for this release, see the [Releases](https://trello.com/board/releases/5087292416df848e4c001c88) board.