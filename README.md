# Form_Validator_And_Sheets_Api
Validate form data using spring boot application and push data to google sheets

Schema Explanation

There will be two NOSQL databases. One for forms and one for responses.

The Response database will have different collections, one for every form.

Sample form in NOSQL database :

● Each form has formId

● Each form has a fields array for all the fields it has (name,age,etc).

● Each item in the fields array is a field with its question, key to be used internally
when sending responses in json,validators,required(true or false),type,etc.

● Validators is an array of validators for the field.

● Each validator has a name which also has an ENUM in java code.

● Each validator also contains the parameters to be used while verifying. Like for
INTEGER_WITHIN_RANGE it will have min value and max value.
Here is a Sample form with 5 different fields and 5 different validators :

{

"_id": {

"$oid": "60ed65692eb3284ae73eb93f"

},

"formId": "1",

"userID": "1",

"title": "Sample",

"fields": [

{

"description": "Enter participant's name",

"key": "name",

"question": "Name of the respondent",

"required": true,


"responseType": "String",

"validators": [

{

"maxChar": {

"$numberInt": "250"

},

"validator": "STRING_LENGTH_VALIDATOR"

}

]

},

{"choices": [

"Male",

"Female",

"Other"

],

"description": "Select participants gender",

"key": "gender",

"question": "Gender of respondent",

"required": true,


"responseType": "Multiple_Choice",

"validators": [

{

"choices": [

"Male",


"Female",

"Others"

],

"maxChoices": {

"$numberInt": "1"

},

"minChoices": {

"$numberInt": "1"

},

"validator": "MULTIPLE_CHOICES_VALIDATOR"

}

]

}

,

{

"description": "Enter age in years",

"key": "age",

"question": "Age of the respondent",

"required": true,

"responseType": "Integer",

"validators": [

{
"max": {
"$numberInt": "120"
},

"min": {
"$numberInt": "0"
},

"validator": "INTEGER_WITHIN_RANGE"}
]

},

{
"key": "location",

"question": "Current Location",

"required": false,

"responseType": "Location",

"validators": [
{
"countries": [
"India",
"Indonesia"
],

"validator": "LOCATION_WITHIN_COUNTRY"

}

]

},

{

"key": "email",

"question": "Enter Email of respondent",

"required": true,

"responseType": "Email",

"validators": [

{
"validator": "EMAIL_VALIDATOR"
}

]

}

]

}

Example Response for above form

{

"name": "shubham",

"gender": [

"Male"

],

"age": 100,

"location": {

"latitude": 23.610,"longitude": 85.27

},

"email": "shubhamr@gmail.com"

}

Keys are specified by the user as a unique identifier of each column. Keys help to find
the field in form metadata. After finding form metadata we can also get the validators
and apply those validators to each field.

Sending this as a POST request to "/{formId}/addNewEntry" adds the response to
the above form id with verification.

Code Explanation
package com.atlanForms.demo.controllers;
This file has the function to handle requests for "/{formId}/addNewEntry"
It verifies the new response by fetching the form metadata from the class
FormUtils. By using validators it validates all the fields, checks if required fields
are present and then adds the response to the database. FormUtils makes a
database call to get the form metadata using FormDao.
FormDao interacts with the database and has the methods to add new
responses, get all responses(for exporting to sheets) and get form metadata.
Validators class in utils has the function for every validation. ValidatorEnum is the
ENUM for all validators. Each field in the form has validators which are fetched
from the form metadata and applied. If validation fails then it throws a validation
exception with the appropriate message.
Config has MongoDBConfig class which has @Configuration annotation so the
database is configured before use.
SheetRESTController has the function to listen to /{formID}/exportToSheets.
SheetService in utils has a method to create a sheet in google sheets from
supplied data.

As the application is not configured with the frontend, just after sending a request
to export to spreadsheet at “ http://localhost:8082/{formID}/exportToSheets” open the
following link in the browser to grant access to your google sheets. This link is
also shown in the log.https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=75809
322581-9g6tnqdj6dkp4cgr9vuijldmls82n8vd.apps.googleusercontent.com&redire
ct_uri=http://localhost:8083/Callback&response_type=code&scope=https://www.g
oogleapis.com/auth/spreadsheets
After giving permission the application would complete the export.
HttpUtils in utils is used to make calls to google geocoding API. This api is used in
LOCATION_WITHIN_COUNTRY validator. This API gives the country for any location and that
is used to verify the country.
Managing the Application in cloud

● Deploying
The application can be run in kubernetes by making containers. We can run 3 services
,one for creating a new form with validation, one for adding new reponses with
validation and one for redis cache as the application would make a lot of repeated calls
to get form metadata.

● Scaling
Each service will have a different number of replicas running. Responses will get
maximum traffic so it will have more replicas. We can set CPU utilization as 50%, min
replicas and max replicas for each service to autoscale when traffic increases.

● Monitoring
We can use the kubernetes dashboard to monitor the current health of the
system. We can get information about memory and CPU utilization to know
whether resources are being over utilized or underutilized. We will change min
replicas and max replicas according to it.

● Logs
To view logs we will give different labels to each service in kubernetes. We can
use the following kind of command for logs
“kubectl logs -l app=formService --all-containers=true --since=10m”. Here the -l
flag specifies that we want logs for containers with the label “app=formService”
for the last 10 minutes.
