# authority-sru-proxy

The purpose of this project is to fetch an authority-record (publication) by authority ID from the authority registry. 
Provide the parameter ```auth_id``` to the lambda to retrieve the corresponding record.

The application uses several AWS resources, including Lambda functions and an API Gateway API. These resources are
defined in the `template.yaml` file in this project. You can update the template to add AWS resources through the same
deployment process that updates your application code.

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
  * Stack for pipeline/CICD. This will bootstrap the app stack (template.yml)
    * Template: pipeline.yml
    * Name: authority-sru-proxy-pipeline
    * Parameters:
      * DeployStackName=authority-sru-proxy
      * GitBranch=develop
      * GitRepo=BIBSYSDEV/authority-sru-proxy
      * PipelineApprovalAction=[Yes|No] (No for non-prod?)
      * (Optional) PipelineApprovalEmail=[email address]
