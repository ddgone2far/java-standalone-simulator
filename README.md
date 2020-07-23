# java-standalone-simulator
Standalone Simulator that mock a real Web service API behavior

You can choose to run in IDE(Eclipse) or run as standalone

-> Run in IDE
1. Set run configuration Arguments -> this port on which this server listens to (e.g. 8088)
2. Run as java application

-> Run as standalone
1. Use maven to package -> mvn clean install
2. from where the jar package is, run command: java -jar <jar-name>.jar <port>
