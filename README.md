Magentix 2 Multi-Agent Platform
===============================

Magentix2 is an agent platform for open Multiagent Systems. Its main objective is to bring agent technology to real domains: business, industry, logistics, e-commerce, health-care, etc.

Magentix2 provides support at three levels:

* Organization level, technologies and techniques related to agent societies.
* Interaction level, technologies and techniques related to communications between agents.
* Agent level, technologies and techniques related to individual agents (such as reasoning and learning).

In order to offer these support levels, Magentix2 is formed by different building blocks, and provides technologies to the development and execution of Multiagent Systems.

COMMUNICATION INFRASTRUCTURE
-----------------------------

Magentix2 uses AMQP as a foundation for agent communication. This industry-grade open standard is designed to support reliable, high-performance messaging over the Internet. It facilitates the interoperability between heterogeneous entities. Magentix2 allows heterogeneous agents to interact to each other via FIPA-ACL messages, which are exchanged over the AMQP standard.

Specifically, Magentix2 uses the Apache Qpid implementation of AMQP. Thus, Magentix2 agents use Qpid client APIs to connect to the Qpid broker and to communicate with other agents at any Internet location.


TRACING SERVICE SUPPORT
-----------------------

The Tracing Service Support allows agents in a Multiagent System to share information in an indirect way by means of trace events. This support is based on the publish/subscribe software pattern, which allows subscribers to filter events attending to some attributes (content-based filtering), so that agents only receive the information they are interested in and only requested information is transmitted.

In order to facilitate this labour to the platform agents, Magentix2 incorporates a Trace Manager (TM), which is in charge of coordinating the process of event tracing, allowing agents to publish/unpublish, to subscribe/unsubscribe, to trace information, or to look up available trace information at run time.


CONVERSATIONAL AGENTS
----------------------

Magentix 2 incorporates conversational agents, which is a new class of agents called CAgents. CAgents allow the automatic creation of simultaneous conversations based on interaction protocols. CAgents can use pre-defined interaction protocols, define their own interaction protocols and also dynamically change interaction protocols at runtime.

CAgents are composed of two main components: Conversation Factories (CFactories) and Conversation Processors (CProcessors). A CFactory defines an interaction protocol as finite state machines by means of nodes and arcs between them. CFactories are in charge of creating CProcessors that will execute the the defined interaction protocol. CFactories manage automatically incoming messages, deciding if a message belongs to an ongoing CProcessor or if a new one has to be created. Moreover, CFactories allow agents to maintain several conversations following simultaneously the same interaction protocol and also manage concurrence aspects.

THOMAS FRAMEWORK
----------------

Magentix2 platform not only has as aims to provide a guaranteed communication mechanism to the programmer, but also to provide a complete support for virtual organizations. THOMAS (Methods, Techniques and Tools for Open Multi-Agent Systems) framework has been integrated with Magentx2 with this purpose.

Agents have access to the infrastructure offered by THOMAS through a set of services including on two main components:

Service Facilitator (SF): its functionality is like a yellow page service and a service descriptor in charge of providing a green page service.
Organization Manager Service (OMS): it allows the creation and the management of any organization, the roles the agents play and the norms that rule their behavior.
Magentix2 implements two types of intermediary agents named SF and OMS to provide the THOMAS API to the platform agents in an easy way. Thus, they have been defined in order to address the translation between Magentix2 agents (or any external agent), that implement FIPA communication, and the services they provide.

In addition, Magentix2 offers a new communication mechanism based on the virtual organizations structure. Thus, it allows the mass communication among agents of an organization, taking into account the type of roles which agents play.


ARGUMENTATIVE AGENTS
--------------------

Magentix2 includes an argumentation API that allows agents to engage in argumentation dialogues to reach agreements about the best solution for a problem that must be solved. With this aim, both argumentative agents and several knowledge resources that they can use to manage arguments are provided.

Argumentative agents implement a case-based argumentation framework to generate arguments, to select the best argument to put forward taking into account their social context and to evaluate arguments in view of other arguments proposed in the dialogue. Also, they can use different dialogue strategies to exchange information and engage in the argumentation process.

Magentix2 argumentative agents are a special type of Magentix2 conversational agents that use a pre-defined interaction protocol.


JASON
------
Magentix2 provides native support for executing Jason agents. This framework has been integrated into Magentix2. Moreover, Jason agents can benefit from the reliable communication, tracing facilities and security mechanisms provided by Magentix2.

HTTP INTERFACE
--------------

In order to allow interaction between a Magentix2 agent and the outside world, an HTTP interface service has been developed. A common use for the HTTP Interface service is a webpage that allows its users to monitor and interact with the agents running in Magentix2. However, the use of the HTTP interface is not exclusive for web pages and can be used in other scenarios.


DEVELOPMENT
-----------
Magentix2 distribution includes examples of agents using all the technologies that Magentix2 provides. There are examples of basic agents, conversational agents, Thomas framework, security, Jason and tracing facilities.

In order to build Magentix2 examples or any new Magentix2 project, it is necessary to include Magentix2 library in the build path of the java compiler. This library is located in the lib folder inside the Magentix2 installation folder.

