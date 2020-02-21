
![WSO2 Enterprise Integrator](gh-docs/images/wso2-integration-logo.png?raw=true)

[![Build Status][badge-travis-image]][badge-travis-url]
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/wso2/product-ei/blob/master/LICENSE)
[<img src="https://img.shields.io/badge/Slack-@wso2--ei-blue">](https://ei-slack.wso2.com/)
[![Twitter](https://img.shields.io/twitter/follow/wso2.svg?style=social&label=Follow)](https://twitter.com/intent/follow?screen_name=wso2)

# WSO2 Enterprise Integrator

WSO2 Enterprise Integrator is an open source, fast, cloud native and scalable integration solution that is the core of 
WSO2 Integration Agile Platform. It enables enterprise integration developers to build sophisticated integration 
solutions to achieve digital agility. As a mature integration product since 2005, *(branded as WSO2 ESB at the time)*, it 
continues to be the most sophisticated and extensible open source enterprise integration solution available.

Actively maintained, with commercial support from WSO2 Inc, WSO2 Enterprise Integrator is widely used in production at 
companies around the globe starting from startups to fortune 500 companies in the fields of government, healthcare, 
banking, education, communication, etc.

[Installation](https://docs.wso2.com/display/EI640/Installation+Guide) | 
[Documentation](https://docs.wso2.com/display/EI640/) | 
[Mailing Lists](https://wso2.com/mail/) | 
[Blog](https://wso2.com/blogs/thesource/) | 
[Support](https://wso2.com/subscription) | 
[Nightly Builds](https://wso2.org/jenkins/view/products/job/products/job/product-ei/)


## Outline 

- [**Why WSO2 Enterprise Integrator?**](#Why-WSO2-Enterprise-Integrator)
- [**Features**](#Features)
- [**Distributions**](#Distributions)
- [**Installation and Run**](#Installation-and-Run)
- [**Artifact Development**](#Artifact-Development)
- [**Building from Source**](#Building-from-Source)
- [**Enterprise Support**](#Enterprise-Support)
- [**Licence**](#License)


## Why WSO2 Enterprise Integrator

WSO2 Enterprise Integrator is an open source, hybrid integration platform. It is comprised of profiles that address different
parts of a complete integration story. 

**Enterprise Service Bus profile**: If you are trying to interconnect your enterprise applications on-premise, legacy 
  or cloud, WSO2 EI can act as a Service Bus. It can help transforming messages 
  to different formats, standards, use different protocols to communicate and mediating messages across the applications. 
  With 100+ ready made cloud connectors, intuitive tooling,  analytic capabilities WSO2 EI provides a greater agility to 
  meet growing and changing enterprise demands. It can also accelerate bringing your enterprise data to the screen by 
  exposing them as services, APIs.
 
**Message Broker profile**: This allows for queueing of messages (using the AMQP protocol) where you do not require an 
  immediate response to continue processing. WSO2 EI acts as a Message Broker server.  

**Business-process profile**: If your integration story contains human interactions (i.e., approval process) or stateful 
  integration/orchestration, WSO2 EI provides BPEL/BPMN and human task capabilities to develop work-flows.

**Analytics profile**: Provides time-based/count-based analytics, stats, and monetization capability at different points of 
  your integration flow implemented by combining the above profiles.    
  
All above aspects are seamlessly cross-supportive and, together, makes WSO2 EI a complete powerful middleware solution that
helps you to digitalize your business with more agility.

WSO2 EI has a **cloud native offering** as well named **[WSO2 Micro Integrator](https://github.com/wso2/micro-integrator)** that
enables developers implement composite microservices and integrate them. It contains a microservices friendly 
implementation of Enterprise Service Bus profile.

[![WSO2 Enterprise Integrator](gh-docs/images/wso2-ei-overview.png?raw=true "WSO2 Enterprise Integrator")](https://wso2.com/integration/features/)


## Features


- **Support for all EIP Patterns**: Integrate applications following 
  [standard enterprise integration patterns](https://en.wikipedia.org/wiki/Enterprise_Integration_Patterns) 
- **Faster message mediation**: [Passthrough HTTP Transport](https://wso2.com/library/articles/2013/12/demystifying-wso2-esb-pass-through-transport-part-i/) 
  for faster message mediation through WSO2 EI
- **Supports multiple transports/messaging standards**: Interconnect applications that support different 
  protocols (i.e., HTTP and JMS)
- **Supports numerous formats and protocols**: Interconnect applications that work with different message 
  formats (i.e., XML and Json)
- **As a gateway** : Front operations within the operations as a managed, secured proxy services/APIs.  
- **Offer QOS to services/APis**: Supports throttling, caching responses for faster mediation, circuit breaking, 
  applying security 
- **Secure enterprise**: Apply Oauth 2.0, Saml SSO, Keberos for services/APIs. WS-security support for proxy services 
- **Service orchestration**: Enable to interconnect a set of APIs and web services and expose them as a single API or 
  a web service 
- **Database integration**: Enable to expose data as services and APIs, stream data and listen for data changes and 
  trigger events 
- **Handle many concurrent HTTP(S) connections**: Ability to serve multiple HTTP connections concurrently using 
  reactor pattern and Java NIO
- **Event publishing, logging and auditing**: Server logs, audit logs, trace logs with information of executions within 
  server to different levels and ability to integrate with popular log analytic software 
- **185+ connectors**: Connecting Web APIs / Cloud services with on-premise enterprise applications
- **Expose enterprise data as APIs/Services**: Use data services to expose queries/stored procedures as  APIs/Services. 
  Transactions supported. 
- **Visual data mapping**: Map input data formats to output data formats visually, flexibility to change 
  message structure within enterprise is never made easier
- **Guaranteed delivery of messages**: WSO2 EI is configurable with message broker profile (OOB) and with any JMS 
  broker to construct asynchronous messaging patterns with GD.  
- **Periodic task execution**: Execute a periodic task, invoke a message flow periodically, handle a bulk load at 
  off peak time. 
- **Connecting with Packaged applications**: Integration with systems like SAP
- **Business processes and human tasks**: Ability to handle message flows those involves a human 
  interaction, approval process in a standard manner.
- **Data analytic, monetization capabilities**: Ability to measure number of hits to APIs/proxies, geographic 
  information, know what is used most by your users. 

For more about WSO2 EI extension points visit [here](https://wso2.com/library/articles/2016/06/article-extending-the-functionality-of-wso2-enterprise-service-bus-part-1/). Please 
find the full available connectors at [WSO2 Store](https://store.wso2.com/store/assets/esbconnector/list).


## Distributions

WSO2 EI is packaged in several forms for different platforms and environments. However, the bare metal form is the Binary Zip file:

- [WSO2 EI Zip](https://wso2.com/integration/install/binary/): Works on any platform, just unzip and run 


|Platform                 |  OS                  | Infrastructure Mgt  |
|-------------------------|----------------------|---------------------|
|[WSO2 EI AWS CloudFormation](https://wso2.com/integration/install/aws/community/get-started/) |[WSO2 EI installer](https://wso2.com/integration/install/download/?type=win64) for Windows  |[WSO2 EI Puppet Install](https://wso2.com/integration/install/puppet/)   |
|[WSO2 EI Kubernetes](https://wso2.com/integration/install/kubernetes/community/get-started/)  |[WSO2 EI installer](https://wso2.com/integration/install/download/?type=mac) for Mac        |[WSO2 EI Ansible Install](https://wso2.com/integration/install/ansible/) |
|[WSO2 EI Docker](https://wso2.com/integration/install/docker/community/get-started/)          |[WSO2 EI installer](https://wso2.com/integration/install/download/?type=ubuntu) for Ubuntu  |   |
|[WSO2 EI Vagrant](https://wso2.com/integration/install/vagrant/community/get-started/)        |[WSO2 EI installer](https://wso2.com/integration/install/download/?type=centos) for CentOS  |   |
|[WSO2 EI Helm](https://wso2.com/integration/install/helm/community/get-started/)              |   |   |
|[WSO2 EI YUM Install](https://wso2.com/integration/install/yum/community/get-started/)                    |   |   |
|[WSO2 EI Brew Install](https://wso2.com/integration/install/brew/community/get-started/)                  |   |   |
|[WSO2 EI Apt Install](https://wso2.com/integration/install/apt/community/get-started/)                    |   |   |

- [WSO2 Micro Integrator](https://github.com/wso2/micro-integrator) is the cloud-native offering of WSO2 EI. It is a 
  configuration driven runtime that helps developers implement composite microservices. 

## Installation and Run

Extract wso2ei-x.x.x.zip and navigate to extracted directory/bin. From there, start the preferred profile. 
After started, Management console will be accessible for the profile started. 
Use username:*admin* and password:*admin* to access the console.

| Profile            | Web Console Url                       |
| ------------------ | --------------------------------------|
| `Integrator (ESB)` | https://localhost:9443/carbon         |
| `Broker`           | https://localhost:9446/carbon         |
| `Business process` | https://localhost:9445/carbon         |
| `Analytics`        | https://localhost:9643/portal         |

All open issues pertaining to WSO2 Enterprise Integrator are reported at the following location: 
[known issues](https://github.com/wso2/product-ei/issues)


## Artifact Development

Use WSO2 EI tooling for artifact development to be deployed and run on WSO2 EI. It is a powerful, visual editor for 
developing WSO2 EI integration artifacts that enables you to structure the whole enterprise integration solution to a 
single project and export it as a single deployable unit. A team can push the project on GitHub and collaboratively 
work on developing integration flows. 

Read on how to develop integration artifacts with EI tooling [here](https://docs.wso2.com/display/EI640/Integration+Tutorials).

#### Test Artifacts in the Enterprise Integrator
WSO2 Enterprise Integrator allows you to execute unit tests against your integration artifacts by using [Synapse Unit Testing Framework](gh-docs/synapse-unit-testing-framework/synapse-unit-testing-framework.md). 

## Building from Source

If you intend to build the project from the source you can do that with or without building the dependent projects. 
Here is an outline how the dependent project are structured. If you build with dependencies, you need to do it from 
bottom to top in the hierarchy.

![WSO2 EI-Repositories](gh-docs/images/ei-repo-structure.png?raw=true)

Repositories referred above

- [product EI](https://github.com/wso2/product-ei): contains product EI packaging. 
- [carbon-mediation](https://github.com/wso2/carbon-mediation): mediation features related to WSO2 ESB are developed in this repository. 
- [carbon-data](https://github.com/wso2/carbon-data): contains features related to data services. 
- [wso2-synapse](https://github.com/wso2/wso2-synapse): source of messaging engine used by the WSO2 EI runtime. 
- [carbon-business-messaging](https://github.com/wso2/carbon-business-messaging): features related to WSO2 Message Broker profile. 
- [andes](https://github.com/wso2/andes): implementation of messaging core of WSO2 Message Broker profile. 
- [carbon-business-process](https://github.com/wso2/carbon-business-process): contains the modules implementing BPEL , 
  WS-Human Tasks and BPMN support for WSO2 EI's business process profile.
- [wso2-ode](https://github.com/wso2/wso2-ode): WS-BPEL compliant web services orchestration engine.
- [carbon Commons](https://github.com/wso2/carbon-commons): contains the common components to Carbon (i.e., cluster mgt, logging, ntask). 
- [WSO2 Axis2 Transports](https://github.com/wso2/wso2-axis2-transports): includes the axis2 based transport 
  implementations (i.e., JMS, Mail, message builders, formatters). 
- [carbon Kernel (4.x.x)](https://github.com/wso2/carbon-kernel/tree/4.4.x): lean, modular, OSGi-based platform. 
  This is the base of the WSO2 Carbon platform and is a server software development platform. 
- [wso2-axis2](https://github.com/wso2/wso2-axis2): is a Web Services / SOAP / WSDL engine. Forked from [Apache Axis2](http://axis.apache.org/axis2/java/core/). 
- [carbon Analytics](https://github.com/wso2/carbon-analytics): contains components related to WSO2 Stream Processor. 
- [carbon event processing](https://github.com/wso2/carbon-event-processing): realtime event processing functionalities 
  used in WSO2 analytics platform. 
- [carbon analytics](https://github.com/wso2/carbon-analytics): common functionalities used in WSO2 analytics platform. 
- [carbon dashboards](https://github.com/wso2/carbon-dashboards): APIs and UI components related to analytic dashboards. 
- [carbon analytics commons](https://github.com/wso2/carbon-analytics-common): implements common functionalities used 
  in WSO2 analytics platform. 
- [siddhi-io.*  repos](https://github.com/wso2-extensions): contains repos that contribute to transport layer of analytics. 
- [carbon Kernel (5.x.x)](https://github.com/wso2/carbon-kernel). 

## Enterprise Support

Enterprise support for WSO2 EI is provided by WSO2. Learn more [here](https://wso2.com/subscription).

## License

```
Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

WSO2 Inc. licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except
in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
```
 
[badge-travis-image]: https://wso2.org/jenkins/job/products/job/product-ei/badge/icon
[badge-travis-url]: https://wso2.org/jenkins/job/products/job/product-ei













