#
# Script to perform automated testing for assignment 2 of AA, 2019 semester 1
#
# The provided Python script will be the same one used to test your implementation.
# We will be testing your code on the core teaching servers, so please try your code there.
# The script first compiles your Java code, runs one of the two implementations then runs a series of test.
# Each test consists of sequence of operations to execute, whose results will be saved to file, then compared against
# the expected output.  If output from the tested implementation is the same as expected (script is tolerant for
# some formatting differences, but please try to stick to space separated output), then we pass that test.
# Otherwise, difference will be printed via 'diff' (if in verbose mode, see below).
#
# Usage, assuming you are in the directory where the test script " assign1TestScript.py" is located.
#
# > python assign1TestScript.py [-v] [-f input filename] <codeDirectory> <name of implementation to test> <list of input files to test on>
#
#options:
#
#    -v : verbose mode
#    -f [filename] : If specified, the file 'filename' will be passed to the Java framework to load as the initial graph.
#
#Input:
#
#   code directory : directory where the Java files reside.  E.g., if directory specified is Assign1-s1234,
#        then Assign1-s1234/MultisetTester.java should exist.  This is also where the script
#        expects your program to be compiled and created in, e.g., Assign2-s1234/MultisetTester.class.
#   name of implementation to test: This is the name of the implementation to test.  The names
#        should be the same as specified in the script or in GraphTester.java
#   input files: these are the input files, where each file is a list of commands to execute.
#        IMPORTANT, the expected output file for the print operation must be in the same directory
#        as the input files, and the should have the same basename - e.g., if we have input operation
#        file of "test1.in", then we should have expected files "test1.vert.exp", "test1.edge.exp", "test1.neigh.exp" and "test1.misc.exp".
#
#
# As an example, I can run the code like this when testing code directory "Assign1-s1234",
# all my input and expected files are located in a directory called "tests"
# and named test1.in and testing for adjacent list implementation:
#
#> python assign1TestScript.py -v   Assign1-s1234    adjlist     tests/test1.in
#
# Another example if running test2.in and using the assocGraph.csv graph as my initial one:
#> python assign1TestScript.py -v -f assocGraph.csv   Assign1-s1234    adjlist     tests/test2.in
#
#
#
# Jeffrey Chan, RMIT 2019
#

import string
import csv
import sets
import getopt
import math
import os
import os.path
import platform
import re
import shutil
import sys
import subprocess as sp
import getopt
import os
import os.path
import sys
import subprocess as sp
from threading import Timer
import cPickle as pickle
import zipfile
import shutil
import time
import glob
import timeit

def main():

    # process command line arguments
    try:
        # option list
        sOptions = "vs:"
        # get options
        optList, remainArgs = getopt.gnu_getopt(sys.argv[1:], sOptions)
    except getopt.GetoptError, err:
        print >> sys.stderr, str(err)
        usage(sys.argv[0])

    bVerbose = False
    bInputFile = False
    sInputFile = ""
    bHasSourceCodeDir = False
    sSourceCodeDir = ""
    # default timeout (in seconds)
    iTimeoutSec = 9999

    for opt, arg in optList:
        if opt == "-v":
            bVerbose = True
        elif opt == "-s":
            # our source code directory to copy jar files etc if missing
            bHasSourceCodeDir = True
            sSourceCodeDir = arg
        elif opt == "-t":
            # specifying timeout period
            iTimeoutSec = int(arg)
        else:
            usage(sys.argv[0])


    if len(remainArgs) < 2:
        print >> sys.stderr, "Not enough command line arguments"
        usage(sys.argv[0])


    # code directory
    sCodeDir = remainArgs[0]
    # set of input tests
    lsInFile = remainArgs[1:]


    # compile the skeleton java files
    sClassPath = "-cp .:jopt-simple-5.0.2.jar:sample.jar"
    sOs = platform.system()
    if sOs == "Windows":
        sClassPath = "-cp .;jopt-simple-5.0.2.jar;sample.jar"

    sCompileCommand = "javac " + sClassPath + " *.java" + " map/*.java" + " pathFinder/*.java" 
    print sCompileCommand
    sExec = "PathFinderTester"

    # whether executable was compiled and constructed
    bCompiled = False

    sOrigPath = os.getcwd()
    os.chdir(sCodeDir)

    if (not os.path.isabs(sCodeDir)):
        sCodeDir = os.path.join(sOrigPath, sCodeDir)

    # check if have all the necessary files
    sPathTester = "PathFinderTester.java"
    sJar = "jopt-simple-5.0.2.jar"
    if bHasSourceCodeDir:
        if not os.path.isfile(sPathTester):
            shutil.copy(os.path.join(sSourceCodeDir, sPathTester), ".")
        if not os.path.isfile(sJar):
            shutil.copy(os.path.join(sSourceCodeDir, sJar), ".")


    # remove main file to force compilation
    if os.path.isfile(sExec + ".class"):
        os.remove(sExec + ".class")

    # compile
    # proc = sp.Popen([sCompileCommand], shell=True, stderr=sp.PIPE)
    proc = sp.Popen(sCompileCommand, shell=True, stderr=sp.PIPE)

    (sStdout, sStderr) = proc.communicate()
	#print sStderr


    # check if executable was constructed
    if not os.path.isfile(sExec + ".class"):
        print >> sys.stderr, sExec + ".java didn't compile successfully."
    else:
        bCompiled = True


    # variable to store the number of tests passed
    passedNum = 0
    #lsTestPassed = [False for x in range(len(lsInFile))]
    lsTestPassed = []
    print ""


    if bCompiled:
        # loop through each input test file
        for (j, sInLoopFile) in enumerate(lsInFile):
            sInFile = os.path.join(sOrigPath, sInLoopFile);
            # sTestName = os.path.splitext(os.path.basename(sInFile))[0]
            sBaseTestName = os.path.splitext(os.path.basename(sInLoopFile))[0]
            sTestName = os.path.splitext(sInFile)[0]
            print 'sTestName = ' + sTestName
            print 'sBaseTestName = ' + sBaseTestName

            # check if there are terrain and waypoint parameter files
            sInTerrainFile = ""
            if os.path.exists(os.path.join(sTestName + ".terrain")):
                sInTerrainFile = os.path.join(sTestName + ".terrain")
            sInWaypointsFile = ""
            if os.path.exists(os.path.join(sTestName + ".waypoint")):
                sInWaypointsFile = os.path.join(sTestName + ".waypoint")

            #sOutputFile = os.path.join(sCodeDir, sTestName + "-" + sImpl + ".out")
            sPathOutputFile = os.path.join(sCodeDir, sBaseTestName + ".path.out")
            sPathExpectedFile = os.path.join(sTestName + ".path.exp")

            # check if expected files exist
            if not os.path.isfile(sPathExpectedFile):
                print >> sys.stderr, sPathExpectedFile + " is missing."
                continue

            sCommandStr = "java " + sClassPath + " " + sExec + " "
            if len(sInTerrainFile) > 0:
               sCommandStr += " -t " + sInTerrainFile
            if len(sInWaypointsFile) > 0:
                sCommandStr += " -w " + sInWaypointsFile
            sCommandStr += " -o " + sPathOutputFile + " " + sInFile

            sCommand = os.path.join(sCommandStr)
            print sCommand


            # following command used by my dummy code to test possible output (don't replace above)
#                 lCommand = os.path.join(sCodeDir, sExec + " " + sExpectedFile + ".test")
            if bVerbose:
                print "Testing: " + sCommand

            proc = sp.Popen(sCommand, shell=True, stderr=sp.PIPE)
            #proc = sp.Popen(sCommand, shell=True, stdin=sp.PIPE, stdout=sp.PIPE, stderr=sp.PIPE)


	        # timing the java subprocess
            ##start_time=time.time() To check timer
            timer = Timer(iTimeoutSec, timeoutHandler, [proc, iTimeoutSec])
            timer.start()
            #(sStdout, sStderr) = proc.communicate("a hello\np\nq")
            (sStdout, sStderr) = proc.communicate()
            timer.cancel()
            ##end_time=time.time()
            ##print(end_time-start_time) To print timer.


            if len(sStderr) > 0:
            #if False:
                print >> sys.stderr, "Cannot execute " + sInFile
                print >> sys.stderr, "Error message from java program: " + sStderr
            else:
                # if bVerbose and len(sStderr) > 0:
                #     print >> sys.stderr, "\nWarnings and error messages from running java program:\n" + sStderr

                # compare expected with output
                (bMapPassed, bOriginPassed, bDestPassed, bWaypoints, lExpPath, lActPath, hTerrain) = pathEvaluate(sInFile, sInTerrainFile, sInWaypointsFile, sPathExpectedFile, sPathOutputFile)

                bPassed = bMapPassed and bOriginPassed and bDestPassed and bWaypoints

                if bVerbose and not bPassed:
                    if not bMapPassed:
                        print >> sys.stderr, 'One or more coordinates of path found by your algorithm either is off the map, goes through impassable coordinates or your path is not continuous (ie., two adjacent coordinates in your path are not neighbourhing coordinates).'
                    if not bOriginPassed:
                        print >> sys.stderr, 'Origin coordinate of path found by your algorithm is not one of the specified origins.'
                    if not bDestPassed:
                        print >> sys.stderr, 'Destination coordinate of path found by your algorithm is not one of the specified destinations.'
                    if not bWaypoints:
                        print >> sys.stderr, 'Path found by your algorithm does not traverse through all the specified waypoints.'

                # we only compare path lengths if paths pass other tests
                if bPassed:
                    # compute the shortest path costs for both
                    expPathCost = computePathCost(lExpPath, hTerrain)
                    actPathCost = computePathCost(lActPath, hTerrain)

                    if expPathCost < actPathCost:
                        if bVerbose:
                            print >> sys.stderr, 'Path cost of found path is not the shortest.'
                            print >> sys.stderr, 'Path cost of ground truth (expected) = ' + str(expPathCost)
                            print >> sys.stderr, 'Path cost of path found by algorithm = ' + str(actPathCost)
                    else:
                        if bVerbose:
                            print 'Passed all tests.'

                        passedNum += 1
                        #vTestPassed[j] = True
                        lsTestPassed.append(sTestName)



    # change back to original path
    os.chdir(sOrigPath)

    print "\nSUMMARY: " + sExec + " has passed " + str(passedNum) + " out of " + str(len(lsInFile)) + " tests."
    #print "PASSED: " + ", ".join([str(x+1) for (x,y) in enumerate(vTestPassed) if y == True]) + "\n"
   # print "PASSED: " + ", ".join(lsTestPassed) + "\n"




#######################################################################################################


def pathEvaluate(sInFile, sInTerrainFile, sInWaypointsFile, sPathExpectedFile, sPathOutputFile):
    """
    Evaluate if the output is the same as expected input based on produced path.
    """

    sDelimiter = " "

    rowNum = 0
    colNum = 0
    hOrigins = {}
    hDestinations = {}
    hImpassable = {}
    hTerrain = {}
    lWaypoints = []

    lExpPath = []
    lActPath = []

    # parse size, origin and destination locations, and finally impassable coordinatesExplored
    with open(sInFile, "r") as fConfig:
        # number of rows and columns
        sLine = fConfig.readline().strip()
        if len(sLine) > 0:
            lFields = string.split(sLine, sDelimiter)
            if len(lFields) >= 2:
                rowNum = int(lFields[0])
                colNum = int(lFields[1])

        # origin
        sLine = fConfig.readline().strip()
        if len(sLine) > 0:
            lFields = string.split(sLine, sDelimiter)
            for i in range(0, int(math.floor(len(lFields)/2))):
                x = int(lFields[2*i])
                y = int(lFields[2*i+1])
                hOrigins[conHashPair(x,y)] = ""

        # destination
        sLine = fConfig.readline().strip()
        if len(sLine) > 0:
            lFields = string.split(sLine, sDelimiter)
            for i in range(0, int(math.floor(len(lFields)/2))):
                x = int(lFields[2*i])
                y = int(lFields[2*i+1])
                hDestinations[conHashPair(x,y)] = ""

        # impassable coordinatesExplored
        for sLine in fConfig:
            sLine1 = sLine.strip()
            lFields = string.split(sLine1, sDelimiter)
            if len(lFields) >= 2:
                x = int(lFields[0])
                y = int(lFields[1])
                hImpassable[conHashPair(x,y)] = ""

    # parse terrain file
    if len(sInTerrainFile) > 0:
        with open(sInTerrainFile, "r") as fTerrain:
            for sLine in fTerrain:
                sLine1 = sLine.strip()
                lFields = string.split(sLine1, sDelimiter)
                if len(lFields) >= 3:
                    x = int(lFields[0])
                    y = int(lFields[1])
                    weight = int(lFields[2])
                    hTerrain[conHashPair(x,y)] = weight

    # parse waypoints file
    if len(sInWaypointsFile) > 0:
        # print sInWaypointsFile
        with open(sInWaypointsFile, "r") as fWaypoints:
            for sLine in fWaypoints:
                # print sLine
                sLine1 = sLine.strip()
                lFields = string.split(sLine1, sDelimiter)
                if len(lFields) >= 2:
                    x = int(lFields[0])
                    y = int(lFields[1])
                    lWaypoints.append((x,y))



    # load expected path
    with open(sPathExpectedFile, "r") as fExpected:
        # should only be one line
        sLine = fExpected.readline()
        if len(sLine) > 0:
            # space delimiter
            sLine1 = sLine.strip()
            lPairs = string.split(sLine1, " ")
            for pair in lPairs:
                matchGroups = re.match(r"[(]([-]*\d+)[,]([-]*\d+)[)]", pair)
                if len(matchGroups.groups()) == 2:
                    x = int(matchGroups.group(1))
                    y = int(matchGroups.group(2))
                    lExpPath.append((x,y))
                else:
                    print >> sys.stderr, 'One of pairs in expected path failed to parse. ' + pair
                    sys.exit(1)

    with open(sPathOutputFile, "r") as fOut:
        # should only be one line
        sLine = fOut.readline()
        if len(sLine) > 0:
            # space delimiter
            sLine1 = sLine.strip()
            lPairs = string.split(sLine1, " ")
            for pair in lPairs:
                matchGroups = re.match(r"[(]([-]*\d+)[,]([-]*\d+)[)]", pair)
                if len(matchGroups.groups()) == 2:
                    x = int(matchGroups.group(1))
                    y = int(matchGroups.group(2))
                    lActPath.append((x,y))
                else:
                    print >> sys.stderr, 'One of pairs in actual path failed to parse. ' + pair
                    sys.exit(1)


    # check if actual/found path goes through any impassable coordinates and path is sequential (no jumps)
    bMapPassed = checkMap(lActPath, rowNum, colNum, hImpassable)

    # check if actual/found path has correct origin
    bOriginPassed = checkOriginDestination(lActPath, hOrigins, 0)

    # check if actual/found path has correct destination
    bDestPassed = checkOriginDestination(lActPath, hDestinations, -1)

    # OPTIONAl, check if path goes through all waypoints
    bWaypoints = checkWaypoints(lActPath, lWaypoints)

    return (bMapPassed, bOriginPassed, bDestPassed, bWaypoints, lExpPath, lActPath, hTerrain)


#################################################################################


def conHashPair(x, y):
    """
    Construct string for hashing.
    """

    return "{}-{}".format(x, y)


def checkMap(lPath, rowNum, colNum, hImpassable):
    """
    Check if all coordinates in path are within map and not go through impassable coordinates.
    Also check if coordinates in path are sequential.
    """

    prevCoord = None
    if len(lPath) > 0:
        (prevX, prevY) = lPath[0]
        if not checkCoordinate(prevX, prevY, rowNum, colNum, hImpassable):
            return False

    for (x,y) in lPath[1:]:
        if not checkCoordinate(x, y, rowNum, colNum, hImpassable):
            return False

        # check if sequential
        if not checkSequential(prevX, prevY, x, y):
            return False

        prevX = x
        prevY = y

    # default is pass
    return True;


def checkOriginDestination(lPath, hCoords, index):
    """
    Check if starting or ending coordinate in path is either the origin or destination respectively.
    """

    if len(lPath) > 0:
        x = lPath[index][0]
        y = lPath[index][1]
        if conHashPair(x, y) not in hCoords:
            print >> sys.stderr, 'First or last point in found path is not an origin or destination respectively' + ' (' + str(x) + ',' + str(y) + ')'
            return False

    # default is origin or destination is valid
    return True;


def checkWaypoints(lPath, lWaypoints):
    """
    Check if all waypoints are passed in path.
    """

    setPath = set(lPath)
    for coord in lWaypoints:
        # check impassableCells
        if coord not in setPath:
            print >> sys.stderr, 'one of the waypoints is missing from path ' + str(coord)
            return False

    # default is pass
    return True;



def checkCoordinate(x, y, rowNum, colNum, hImpassable):
    """
    Check if coordinate are within map or not impassable.
    """

    if x < 0 or x > rowNum:
        print >> sys.stderr, 'one of row coordinate in path is outside boundaries' + ' (' + x + ',' + y + ')'
        return False
    if y < 0 or y > colNum:
        print >> sys.stderr, 'one of column coordinate in path is outside boundaries' + ' (' + x + ',' + y + ')'
        return False

    # check impassableCells
    if conHashPair(x,y) in hImpassable:
        print >> sys.stderr, 'one of the coordinates in path is an impassable one' + ' (' + x + ',' + y + ')'
        return False

    return True


def checkSequential(prevX, prevY, x, y):
    """
    Check if the two coordinates are sequential.
    """
    return (abs(prevX - x) == 1 and abs(prevY - y) == 0) or (abs(prevX - x) == 0 and abs(prevY - y) == 1)


def computePathCost(lPath, hTerrain):
    """
    Compute the path cost.
    """
    totalCost = 0
    for (x,y) in lPath:
        sCoord = conHashPair(x,y)
        if sCoord in hTerrain:
            totalCost += hTerrain[sCoord]
        else:
            # default cost of 1
            totalCost += 1

    return totalCost


################################################################
# handles timeout
def timeoutHandler(p, t):
    p.kill()
    print >> sys.stderr, 'Took more than', t, 'sec to execute, test being timeouted.\n'

def usage(sProg):
    print >> sys.stderr, sProg + " [-v] [-s source directory to copy any missing framework code] [-t timeout in seconds] <parent code directory to test>  <list of test input files>"
    sys.exit(1)



if __name__ == "__main__":
    main()
