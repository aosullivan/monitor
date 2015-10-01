# monitor

Simple webapp to ping various services and display service status

Services are categorized as environments eg.

test evironment 1
	- database
	- web app url 1
	- web app url 2

test evironment 2
	- database
	- web app url 1
	- web app url 2

The monitor server run checks against the various services,
e.g. connect to the db and do a SELECT on some table.

It will regularly run a custom test script against each service in each environment,
and store the results in a local h2 database.

The UI will regularly call the monitor server to get the last known status
of each service, and display the results.

## Configuration

2. Configure the connection details for each environment 
in a file, config.edn under /resources

This is not checked into git for obvious reasons

Sample config.edn content:

{:qa6 {:classname   "com.sybase.jdbc4.jdbc.SybDriver"
      :subprotocol "sybase:Tds"
      :subname "myhost:10020/mydatabase1"
      :user "fred"
      :password "fred" }}


## Running

To start a web server for the application, run:

    lein run
    
    then http://localhost:3000/services
    
    lein migratus migrate
    lein migratus rollback

## TODO

checks
- cd server
- rest api
- other rest api
- edi ejb
- mid ejb

show disabled job status in UI
reload screen automatically
are db connections reused?
Print the actual error somewhere on the UI for failed tests and LINK to any screenshots
remove database completely, its all temp state
check fiddler for duplicate requests
make WAITING orange

jdbc:h2:file:C:/workspace-clj/monitor/monitor_dev.db;FILE_LOCK=NO