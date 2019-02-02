all:			KingsValleyClient.class KingsValleyServer.class \
			KingsValleyImpl.class KingsValleyInterface.class

KingsValley.class:		KingsValleyImpl.java KingsValleyInterface.class
			@javac KingsValleyImpl.java

KingsValleyInterface.class:	KingsValleyInterface.java
			@javac KingsValleyInterface.java

KingsValleyClient.class:	KingsValleyClient.java
			@javac KingsValleyClient.java

KingsValleyServer.class:	KingsValleyServer.java
			@javac KingsValleyServer.java

run:			all
			@java KingsValleyServer &
			@sleep 1
			@java KingsValleyClient

clean:
			@rm -f *.class *~

info:
			@echo "(c) Roland Teodorowitsch (08 abr. 2015)"

