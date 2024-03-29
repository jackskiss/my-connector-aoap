// This file automatically generated by:
//   Apache Etch 1.3.0-incubating (LOCAL-0) / java 1.3.0-incubating (LOCAL-0)
//   Wed Sep 25 20:31:58 KST 2013
// This file is automatically created and should not be edited!

This is a description of the etch-generated files for your service.

----------------
- How To Build -
----------------

In the directory where the etch file is located, execute the following command:

etch -q -d . -I . -b java -w all Blah.etch

This would compile the service description Blah.etch, generating all java files,
into java packages rooted in the current directory (-d .), resolving includes
and mixins from the current directory (-I .) and being quiet about it (-q).

Assuming the Blah.etch specified a module of "demo" and a service of Blah,
the java files would be generated into a sub-directory demo of the current
directory. you may now change to that directory and compile the resulting files:

cd demo
javac -cp %ETCH_HOME%\lib\etch-java-runtime.jar *.java

(Assuming windows; for unix, you only need to change the slash direction and
change %ETCH_HOME% into \$ETCH_HOME.)

To run the service (which initially won't do anything but make a connection and
then close it, use the following two commands in separate shells (again, from
the demo directory):

java -cp %ETCH_HOME%\lib\etch-java-runtime.jar;.. demo.MainBlahListener

java -cp %ETCH_HOME%\lib\etch-java-runtime.jar;.. demo.MainBlahClient

(Again, assuming windows; for unix, make the changes detailed above and also
change the semi-colon in the -cp spec to a colon.)

The first command (java ... demo.MainBlahListener) starts the service listener
which accepts connections. The second command runs a sample client, which makes
a connection to the running listener and then closes it again.

Once you've compiled the service and tested the result, you may implement your
service by adding content to the etch file, adding implementation details to the
Impl*.java and Main*.java files, and recompiling using the above steps. You may
also load these files into an IDE such as eclipse or intellij, or use a build
management system such as maven or ant. Remember, whenever you change the etch
file, you must re-etch the service description and then re-javac all the java
files.

-------------------
- Generated Files -
-------------------

Here is a description of the generated files for a service named Blah.

Blah.java
BlahClient.java
BlahServer.java

These three files are the generated interface classes. Service defined constants
and types are in Blah.java, as well as direction "both" messages, which are
messages which are shared between client and server. BlahClient.java and
BlahServer.java are respectively the messages for just the direction client or
server. You should not edit these files.

RemoteBlah.java
RemoteBlahClient.java
RemoteBlahServer.java

RemoteBlah*.java are the generated classes which implement the interfaces,
with message implementations which encode the message type and arguments for
transport to the connection peer and then receive and decode any response. You
should not edit these files.

StubBlah.java
StubBlahClient.java
StubBlahServer.java

StubBlah*.java are the generated classes which receive messages encoded by
RemoteBlah*.java and decode them, calling the appropriate message implementation
and then encoding and sending any result. You should not edit these files.

ValueFactoryBlah.java

ValueFactoryBlah.java is a generated class which contains helper code to
serialize service defined types and also the meta data which describes the
messages and fields, field types, timeouts, etc. You should not edit this file.

BaseBlah.java
BaseBlahClient.java
BaseBlahServer.java

BaseBlah*.java are generated classes which implement the interfaces, with
message implementations which do nothing but throw UnsupportedOperationException.
They can be used to supply an implementation when you don't want to immediately
implement all the messages. You should not edit these files.

ImplBlahClient.java
ImplBlahServer.java

ImplBlah*.java are the generated sample client and server implementation
classes. They extend the generated base classes. These are used by the sample
main programs to provide implementations of the client or server messages. Edit
these files as you wish to implement your service (overriding the default
implementations from the generated base classes).

BlahHelper.java

BlahHelper.java is a generated class which includes static methods to help
construct transports for client and listener. You should not edit this file.

MainBlahClient.java
MainBlahListener.java

MainBlah*.java are the generated sample client and server main programs.
They show how to setup a transport and start it and how to create a session.
Edit these files as you wish to implement your service, or just copy the code
into your own application.

