# Author        : Shivani Sanjay Tergaonkar
# File          : Makefile
# Creation Date : 01/30/2021
# Description   : This file contains command line 
#                 options to build WordCount project

OBJ_PATH  = bin
OBJ       = WordThread
CC        = javac
CC_FLAGS  = -d
SRC       = src/WordThread.java
RUNCC     = java
RUN_FLAGS = -classpath

all:	
	@ echo --------------------------------------------------
	@ echo   Building WordThread program for COEN-283 class 
	mkdir -p $(OBJ_PATH)
	$(CC) $(CC_FLAGS) $(OBJ_PATH) $(SRC)
	@ echo   Build successful                         
	@ echo   © Property of Shivani Sanjay Tergaonkar
	@ echo --------------------------------------------------

clean:
	@ echo ---------------------------------------------
	@ echo   Deleting object files                      
	rm -rf $(OBJ_PATH)
	@ echo   Object files deleted                      
	@ echo ---------------------------------------------

run: 
	@ echo ---------------------------------------------
	@ echo   Running WordThread program 
	$(RUNCC) $(RUN_FLAGS) $(OBJ_PATH) $(OBJ)
	@ echo   WordThread Program completed!
	@ echo ---------------------------------------------


