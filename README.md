# reins

Reins is a processor and executor that can be used to deploy mobile agents written in Clojure. Mobile agents, by definition, are a combination of computer software and data which is able to migrate from one computer to another autonomously and continue its execution on the destination computer. Reins aims at making this type of software easy to write, while still providing the safty and security we come to expect in modern applications.

The executor is a daemon that runs on a machine. It listens via many different data transfer protocols for incoming agents. If an agent is detected, it is ingested and allowed to run. It is a simple environment for applications to run within.

The processor converts a specially formatted Clojure file into an executor readable format. Optionally, the processor can send the processed agent via a data transfer protocol.

Once the agent begins execution, it is fully in control of itself. If it knows where it can go and how to get there, agents can move themselves to other locations. Agents should be written to be small, transferable, and self contained. If you can abstract some functionality within an agent into another, you should. The smaller the agent, the better. The focus of writing network applications should be creating many small programs that can do the task of one large one.

## Installation

In order to install, simply download or compile the jars on your own computer.

## Usage

To initialize the execution daemon, simply run the jar with a single parameter. The parameter is the unique name you want to give this executor. Once you have started up the daemon, it will work in the background, running any agents that have the proper credentials to do so.

    $ java -jar reins-executor-0.1.0-standalone.jar [custom-name]

To use the processor, simply run the processor with the path to the agent as the first parameter. This will convert the agent code into an executor-readable format and send a message via spread to be picked up by any listening executors.

    $ java -jar reins-processor-0.1.0-standalone.jar [agent-path]

## Options

FIXME: I expect that there will be many options when configuring the executor. They will go here at a later date

## Examples

Here are a few example usecases.

The hello world version of an agent is extremely simple. The agent's code would look a little something like this:
```clojure
{:config {}
 :data {}
 :do-next "do-next"}

(defn do-next
  "This function runs as soon as the agent arrives at its destination."
  [briefcase]
  (println "Hello, World!"))
```

The first map above is the agents briefcase. The briefcase holds information about the agent. An agent carries a briefcase as it travels from one executor to another. The :config section is read by the executor as it enters. Currently, it states that it is looking for an executor that is connected to spread with a name of 'exe-1.' The :data section describes the persistant variables that are going to be carried with it as it moves. In this example, the application is not carrying any information with it. The :do-next section holds the name of the function that will be run when the agent begens execution.

If that agent was stored in a file titled 'helloworld.clj,' you would process the code by running the following command:

    $ java -jar reins-processor-0.1.0-standalone.jar helloworld.clj

The processor sends the processed agent to the executor, and the code will run on the machine it was sent to.

### Types of Agents

We don't currently know what types of agents will take form throughout experimentation with this type of system. Below are a list of agent types and how they should be used.

+ Carrier Agent
    - Carries agents from one location to another.
    - A carrier agent could be used to move agents that don't understand the message transmission method implemented on the current network. Carriers could have knowledge of network architecture and use that knowledge to slurp up agents, and deliver them to their desired location.
+ Cloning Agent
    - Duplicates an agent.
    - A cloning agent takes in an agent as a parameter. The cloning agent creates multiple instances of the agent and deploys them on multiple different machines.
+ Container Agent
    - A glorified private variable.
    - A container agent is one that does nothing but hold data. It could be one centeral location that holds all of a network application's data. If you have the proper credentials to access the container, it will deliver data to the agent that requests it.
+ Duckling Agent
    - A follower.
    - A duckling agent follows the path of another agent and is capable of doing some task if requested. It could clean up after an agent, transport an agent, or carry an agent. Just an agent that fallows another.
+ Echo Agent
    - A messaging system.
    - An echo agent's purpose is to recieve messages from other agents and relay them elsewhere. It could relay the information to another program, standard out, etc.
+ Function Agent
    - A program acting as a function.
    - An agent that performs a single task, taking one input and giving one output. Function agents are part of the logic of the application. Agents uses fuction agents to do calcuations that are not needed this second, but could be retrieved at a later time.
+ Master Agent
    - A controller of agents.
    - A master agent controls other agents. This could be concidered the centeral hub of the network application. Noramlly it is stationary, and provides the user with some sort of interface for interacting with the other agents.
+ Mother Agent
    - Births uique agents.
    - A mother agent is like a cloning agent, but creates unique versions of a common agent. These agents are created with unique identifiers so they can be identified from one another.
+ Transport Agent
    - Sends agents using protocols.
    - A transport agent is one that has knowledge of network protocols and other data transmission methods. It takes in an agent as a parameter and sends it to another location using some sort of transmission method.

### Agent TCP Ports

In order to keep the system self contained, the agents communicate and move via TCP. If you wish to restrict the actions of a specific type of agent, you can simply restrict that ports usage. As long as you have your own agents on the network monitoring who comes into the network, your system should stay secure. Here is a list of port numbers and what they are associated with.

+ 1612
    - Executor. Have an agent send itself to an IP at this port in order to allow it to run. This is only implemented on open networks. This ip can be kept secret and modified if need be.
+ 1613
    - Carrier Agent. If you want to enter a network or send an agent to a carrier, this is the port you would use.
+ 1614
    - Cloner Agent. If you want to duplicate an agent or multicast it to many computers at once, this is the port you would use.
+ 1615
    - Container Agent. If you wish to request agent data from a specific ip, you would use this port number.

### Bugs

Im sure there will be many!

## License

Copyright © 2015 Alexander Maricich
