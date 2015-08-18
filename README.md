# monitor

wip

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run
    
    then http://localhost:3000/services
    
    lein migratus migrate
    lein migratus rollback

## TODO

Checks
Align env status to checks status and start at WAITING
Autorefresh (angular)
Colours

jdbc:h2:file:C:/workspace-clj/monitor/monitor_dev.db;FILE_LOCK=NO