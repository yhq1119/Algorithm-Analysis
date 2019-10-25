#
# Script to perform automated testing for assignment 1 of AA, 2019 semester 1
#
# The provided Python script will be the same one used to Assignment1Tests your implementation.
# We will be testing your code on the core teaching servers, so please try your code there.
# The script first compiles your Java code, runs one of the two implementations then runs a series of Assignment1Tests.
# Each Assignment1Tests consists of sequence of operations to execute, whose results will be saved to file, then compared against
# the expected output.  If output from the tested implementation is the same as expected (script is tolerant for
# some formatting differences, but please try to stick to space separated output), then we pass that Assignment1Tests.
# Otherwise, difference will be printed via 'diff' (if in verbose mode, see below).
#
# Usage, assuming you are in the directory where the Assignment1Tests script " assign1TestScript.py" is located.
#
# > python assign1TestScript.py [-v] [-f input filename] <codeDirectory> <name of implementation to Assignment1Tests> <list of input files to Assignment1Tests on>
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
#   name of implementation to Assignment1Tests: This is the name of the implementation to Assignment1Tests.  The names
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
# Jeffrey Chan, 2019
#

import string
import csv
import sets
import getopt
import os
import os.path
import platform
import re
import shutil
import sys
import subprocess as sp


def main():

    # process command line arguments
    try:
        # option list
        sOptions = "vf:s:"
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

    for opt, arg in optList:
        if opt == "-v":
            bVerbose = True
        elif opt == "-f":
            bInputFile = True
            sInputFile = arg
        elif opt == "-s":
            # our source code directory to copy jar files etc if missing
            bHasSourceCodeDir = True
            sSourceCodeDir = arg
        else:
            usage(sys.argv[0])


    if len(remainArgs) < 3:
        usage(sys.argv[0])


    # code directory
    sCodeDir = remainArgs[0]
    # which implementation to Assignment1Tests (see MultiTester.java for the implementation strings)
    sImpl = remainArgs[1]
    # set of input files that contains the operation commands
    lsInFile = remainArgs[2:]


    # check implementatoin
    setValidImpl = set(["adjlist", "incmat", "sample"])
    if sImpl not in setValidImpl:
        print >> sys.stderr, sImpl + " is not a valid implementation name."
        sys.exit(1)




    # compile the skeleton java files
    sClassPath = "-cp .:jopt-simple-5.0.2.jar:sample.jar"
    sOs = platform.system()
    if sOs == "Windows":
        sClassPath = "-cp .;jopt-simple-5.0.2.jar;sample.jar"

    sCompileCommand = "javac " + sClassPath + " *.java"
    print sCompileCommand
    sExec = "GraphEval"

    # whether executable was compiled and constructed
    bCompiled = False

    sOrigPath = os.getcwd()
    os.chdir(sCodeDir)

    # check if have all the necessary files
    sGraphTester = "GraphEval.java"
    sAssoc = "AssociationGraph.java"
    sAssoc2 = "AbstractAssocGraph.java"
    sJar1 = "jopt-simple-5.0.2.jar"
    if bHasSourceCodeDir:
        if not os.path.isfile(sGraphTester):
            shutil.copy(os.path.join(sSourceCodeDir, sGraphTester), ".")
        if not os.path.isfile(sAssoc):
            shutil.copy(os.path.join(sSourceCodeDir, sAssoc), ".")
        if not os.path.isfile(sAssoc2):
            shutil.copy(os.path.join(sSourceCodeDir, sAssoc2), ".")
        if not os.path.isfile(sJar1):
            shutil.copy(os.path.join(sSourceCodeDir, sJar1), ".")


    # compile
    # proc = sp.Popen([sCompileCommand], shell=True, stderr=sp.PIPE)
    proc = sp.Popen(sCompileCommand, shell=True, stderr=sp.PIPE)
    (sStdout, sStderr) = proc.communicate()
    print sStderr

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
        # loop through each input Assignment1Tests file
        for (j, sInLoopFile) in enumerate(lsInFile):
            sInFile = os.path.join(sOrigPath, sInLoopFile);
            sTestName = os.path.splitext(os.path.basename(sInFile))[0]
            #sOutputFile = os.path.join(sCodeDir, sTestName + "-" + sImpl + ".out")
            sVertOutputFile = os.path.join(sTestName + "-" + sImpl + ".vert.out")
            sEdgeOutputFile = os.path.join(sTestName + "-" + sImpl + ".edge.out")
            sNeighOutputFile = os.path.join(sTestName + "-" + sImpl + ".neigh.out")
            sMiscOutputFile = os.path.join(sTestName + "-" + sImpl + ".misc.out")
            sVertExpectedFile = os.path.splitext(sInFile)[0] + ".vert.exp"
            sEdgeExpectedFile = os.path.splitext(sInFile)[0] + ".edge.exp"
            sNeighExpectedFile = os.path.splitext(sInFile)[0] + ".neigh.exp"
            sMiscExpectedFile = os.path.splitext(sInFile)[0] + ".misc.exp"

            # check if expected files exist
            if not os.path.isfile(sVertExpectedFile):
                print >> sys.stderr, sVertExpectedFile + " is missing."
                continue


            sCommand = os.path.join("java " + sClassPath + " " + sExec + " " + sImpl + " " + sVertOutputFile + " " + sEdgeOutputFile + " " + sNeighOutputFile + " " + sMiscOutputFile)
            if bInputFile:
                sCommand = os.path.join("java " + sClassPath + " " + sExec + " -f " + sInputFile + " " + sImpl + " " + sVertOutputFile + " " + sEdgeOutputFile + " " + sNeighOutputFile + " " + sMiscOutputFile)
            print sCommand


            # following command used by my dummy code to Assignment1Tests possible output (don't replace above)
#                 lCommand = os.path.join(sCodeDir, sExec + " " + sExpectedFile + ".Assignment1Tests")
            if bVerbose:
                print "Testing: " + sCommand
            with open(sInFile, "r") as fIn:
                proc = sp.Popen(sCommand, shell=True, stdin=fIn, stderr=sp.PIPE)
                #proc = sp.Popen(sCommand, shell=True, stdin=sp.PIPE, stdout=sp.PIPE, stderr=sp.PIPE)

                #(sStdout, sStderr) = proc.communicate("a hello\np\nq")
                (sStdout, sStderr) = proc.communicate()

                #if len(sStderr) > 0:
                if False:
                    print >> sys.stderr, "Cannot execute " + sInFile
                    print >> sys.stderr, "Error message from java program: " + sStderr
                else:
                    if bVerbose and len(sStderr) > 0:
                        print >> sys.stderr, "\nWarnings and error messages from running java program:\n" + sStderr

                    # compare expected with output
                    bVertPassed = vertEvaluate(sVertExpectedFile, sVertOutputFile)
                    bEdgePassed = edgeEvaluate(sEdgeExpectedFile, sEdgeOutputFile)
                    bNeighPassed = neighEvaluate(sNeighExpectedFile, sNeighOutputFile)
                    bMiscPassed = miscEvaluate(sMiscExpectedFile, sMiscOutputFile)
                    if bVertPassed and bEdgePassed and bNeighPassed and bMiscPassed:
                        passedNum += 1
                        #vTestPassed[j] = True
                        lsTestPassed.append(sTestName)
                    else:
                        # print difference if failed
                        if bVerbose:
                            if not bVertPassed:
                                print >> sys.stderr, "Difference between vertices output and expected:"
                                proc = sp.Popen("diff -y " + sVertOutputFile + " " + sVertExpectedFile, shell=True)
                                proc.communicate()
                                print >> sys.stderr, ""
                            if not bEdgePassed:
                                print >> sys.stderr, "Difference between edges output and expected:"
                                proc = sp.Popen("diff -y " + sEdgeOutputFile + " " + sEdgeExpectedFile, shell=True)
                                proc.communicate()
                                print >> sys.stderr, ""
                            # TODO: Update to in and out neighbours
                            if not bNeighPassed:
                                print >> sys.stderr, "Difference between neighbours output and expected:"
                                proc = sp.Popen("diff -y " + sNeighOutputFile + " " + sNeighExpectedFile, shell=True)
                                proc.communicate()
                                print >> sys.stderr, ""
                            if not bMiscPassed:
                                print >> sys.stderr, "Difference between miscellaneous output and expected:"
                                proc = sp.Popen("diff -y " + sMiscOutputFile + " " + sMiscExpectedFile, shell=True)
                                proc.communicate()
                                print >> sys.stderr, ""



    # change back to original path
    os.chdir(sOrigPath)

    print "\nSUMMARY: " + sExec + " has passed " + str(passedNum) + " out of " + str(len(lsInFile)) + " tests."
    #print "PASSED: " + ", ".join([str(x+1) for (x,y) in enumerate(vTestPassed) if y == True]) + "\n"
    print "PASSED: " + ", ".join(lsTestPassed) + "\n"




#######################################################################################################


def vertEvaluate(sExpectedFile, sOutputFile):
    """
    Evaluate if the output is the same as expected input for the vertices operation.
    """

    lExpMatches = []
    lActMatches = []
    sDelimiter = ", "

    with open(sExpectedFile, "r") as fExpected:
        # should only be one line
        for sLine in fExpected:
            # space delimiter
            sLine1 = sLine.strip()
            lFields = re.split("[\t ]*[,|\|]?[\t ]*", sLine1)
            lExpMatches.extend(lFields)


    with open(sOutputFile, "r") as fOut:
        # should only be one line
        for sLine in fOut:
            # space delimiter
            sLine1 = sLine.strip()

            # if line is empty, we continue (this also takes care of extra newline at end of file)
            if len(sLine1) == 0:
                continue
            # should be space-delimited, but in case submissions use other delimiters
            lFields = re.split("[\t ]*[,|\|]?[\t ]*", sLine1)
            lActMatches.extend(lFields)

    setExpMatches = sets.Set(lExpMatches)
    setActMatches = sets.Set(lActMatches)


    # if there are differences between the sets
    if len(setExpMatches.symmetric_difference(setActMatches)) > 0:
        return False


    # passed
    return True


def edgeEvaluate(sExpectedFile, sOutputFile):
    """
    Evaluate if the output is the same as expected input for the edge operation.
    """

    ltExpMatches = []
    ltActMatches = []

    with open(sExpectedFile, "r") as fExpected:
        for sLine in fExpected:
            # space delimiter
            sLine1 = sLine.strip()
            lFields = re.split("[\t ]*[,|\|]?[\t ]*", sLine1)
            ltExpMatches.append((lFields[0], lFields[1], lFields[2]))


    with open(sOutputFile, "r") as fOut:
        for sLine in fOut:
            # space delimiter
            sLine1 = sLine.strip()

            # if line is empty, we continue (this also takes care of extra newline at end of file)
            if len(sLine1) == 0:
                continue
            # should be space-delimited, but in case submissions use other delimiters
            lFields = re.split("[\t ]*[,|\|]?[\t ]*", sLine1)
            if len(lFields) != 3:
                # less than 3 numbers on line, which is a valid matching if not empty line
                return False
            else:
                ltActMatches.append((lFields[0], lFields[1], lFields[2]))



    setExpMatches = sets.Set(ltExpMatches)
    setActMatches = sets.Set(ltActMatches)

    # if there are differences between the sets
    if len(setExpMatches.symmetric_difference(setActMatches)) > 0:
        return False

    # passed
    return True


def neighEvaluate(sExpectedFile, sOutputFile):
    """
    Evaluate if the output is the same as expected input for the neighbourhood operations.
    """

    llExpMatches = []
    llActMatches = []

    with open(sExpectedFile, "r") as fExpected:
        for sLine in fExpected:
            # space delimiter
            sLine1 = sLine.strip()
            lFields = re.split("[\t ]*", sLine1)
            if len(lFields) >= 1:
                llExpMatches.append(lFields[1:])
            else:
                llExpMatches.append([])


    with open(sOutputFile, "r") as fOut:
        for sLine in fOut:
            # space delimiter
            sLine1 = sLine.strip()

            # if line is empty, we continue (this also takes care of extra newline at end of file)
            if len(sLine1) == 0:
                continue
            # should be space-delimited, but in case submissions use other delimiters
            lFields = re.split("[\t ]*", sLine1)
            if len(lFields) >= 1:
                llActMatches.append(lFields[1:])
            else:
                llActMatches.append([])

    # Assignment1Tests for each neighbourhood, whether they are the same
    if len(llExpMatches) != len(llActMatches):
        return False

    for i in range(len(llExpMatches)):
        # direct difference
        setExpMatches = sets.Set(llExpMatches[i])
        setActMatches = sets.Set(llActMatches[i])

        # if there are differences between the sets
        if len(setExpMatches.symmetric_difference(setActMatches)) > 0:
            return False


    # passed
    return True



def miscEvaluate(sExpectedFile, sOutputFile):
    """
    Evaluate if the output is the same as expected input for miscellaneous operations.
    """

    lExpMatches = []
    lActMatches = []
    sDelimiter = " "

    with open(sExpectedFile, "r") as fExpected:
        for sLine in fExpected:
            # space delimiter
            sLine1 = sLine.strip()
            lExpMatches.append(sLine1);

    with open(sOutputFile, "r") as fOut:
        for sLine in fOut:
            # space delimiter
            sLine1 = sLine.strip()

            # if line is empty, we continue (this also takes care of extra newline at end of file)
            if len(sLine1) == 0:
                continue

            lActMatches.append(sLine1)

    if len(lExpMatches) != len(lActMatches):
        return False

    for i in range(len(lExpMatches)):
        if lExpMatches[i] != lActMatches[i]:
            return False

    # passed
    return True



def usage(sProg):
    print >> sys.stderr, sProg + " [-v] <code directory> <name of implementation to Assignment1Tests> <list of Assignment1Tests input files>"
    sys.exit(1)



if __name__ == "__main__":
    main()
