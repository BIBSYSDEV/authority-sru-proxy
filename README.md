# authority-sru-proxy
A simple proxy to retrieve authority-records from SRU

The project consist only of an AWS CloudFormation script. It is ment to:
* set up a Lambda-function. The Lambda-function is an inlineCode nodejs script.
* and configure the ApiGateway for a GET-request
