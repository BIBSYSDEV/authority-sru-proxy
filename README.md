# authority-sru-proxy
A simple proxy to retrieve authority-records from SRU

The project consist only of an AWS CloudFormation script. It is ment to:
* set up a Lambda-function. The Lambda-function is an inlineCode nodejs script.
* and configure the ApiGateway for a GET-request

Prerequisites (shared resources):
* HostedZone: [sandbox|dev|test|prod].bibs.aws.unit.no
* Create a CodeStarConnection that allows CodePipeline to get events from and read the GitHub repository

  The user creating the connection must have permission to create "apps" i GitHub
* SSM Parameter Store Parameters:
  * /api/domainName = api.[sandbox|dev|test|prod].bibs.aws.unit.no
  * /github-connection = (CodeStarConnections ARN from above)
* Create the following CloudFormation stack manually using the AWS Web Console, CLI or API:
  * Stack for Custom Domain Name, Certificate and Route53 RecordSet:
    * Template: api-domain-name.yaml
    * Name: apigw-custom-domain-name-api-[sandbox|dev|test|prod]-bibs-aws-unit-no
    * Parameters:
      * HostedZoneId=[ID]

Bootstrap:
* Create the following CloudFormation stack manually using the AWS Web Console, CLI or API:
  * Stack for pipeline. This will bootstrap the app template
    * Template: pipeline.yml
    * Name: authority-sru-proxy-pipeline
    * Parameters:
    * PipelineApprovalEmail=[email address]
