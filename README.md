# Consulting Utils

This is an experimental pre-alpha release and should only be used under the direction of the Alfresco Professional Services Team

## Functionality Included

### Current Delivery 0.6.0

* Attribute Service Helpers
* Unique Properties
* Dynamic Constraints
* Folder Hierarchy Helpers
* Index Checker (not active)
* Queue (Not Active)
* Audit Reporting
* Tracker Component
* Metadata Reporting
* Audit Cleanup
* Batch Updater
* Tree Walker

### Clean up release 0.6.1

* Java Docs and other documentation

### Potential Additions 0.7.0

* Bad Node Audit
* Bulk Exporter

# Documentation

## Audit Cleanup

Below are the properties that need to be set to control the audit clean up

```
# Run at 11:59:59 PM on December 31, 2099 - essentially never
trim.audit.schedule=59 59 23 31 12 ? 2099
# Keep 1 week of Audit
trim.audit.keepSeconds=604800
# Purge the following audit applications
trim.audit.applicationNames=alfresco-access,CMISChangeLog
```

I would recommend running the clean up daily (or a few times a day if necessary)

# 0.6.0 Release Notes


## Caveats

* Update to Unique Attribute Helper (converted the ```MAX_ID_ATTR``` from int to long)
