MathLogic
=========

Some programs for checking mathematical logic proofs, automatically proof statements and so on. Specification:
https://github.com/shd/logic2011/blob/master/homework.pdf

ProofChecker
------------

ProofChecker automatically checks given in file proof.

To check proof in file filename.txt execute (without arguments ProofChecker will try to read proof from file proof.txt):

    java -jar ProofChecker-executable.jar filename.txt
    
ProofChecker-executable.jar file can be found from ./ProofChecker/target/ folder:  https://github.com/PolarHare/MathLogic/blob/master/ProofChecker/target/ProofChecker-executable.jar

proof.txt - example file with proofs can be found in root folder:  
https://github.com/PolarHare/MathLogic/blob/master/proof.txt

AssumptionConverter
------------

AssumptionConverter moves last assumption as prerequisite in consecution.

To run on file filename.txt execute (without arguments it will try to read from file assumption.txt):

    java -jar AssumptionConverter-executable.jar filename.txt

AssumptionConverter-executable.jar file can be found from ./AssumptionConverter/target/ folder:  https://github.com/PolarHare/MathLogic/blob/master/AssumptionConverter/target/AssumptionConverter-executable.jar

assumption.txt - example file can be found in root folder:
https://github.com/PolarHare/MathLogic/blob/master/assumption.txt

Result will be written in file result.txt (example can be found in root of repository):
https://github.com/PolarHare/MathLogic/blob/master/result.txt

Proofer
------------

Proofer checks expression and proofs it.

To run on file filename.txt execute (without arguments it will try to read from file toProof.txt):

    java -jar Proofer-executable.jar filename.txt

Proofer-executable.jar file can be found from ./Proofer/target/ folder:  https://github.com/PolarHare/MathLogic/blob/master/Proofer/target/Proofer-executable.jar

toProof.txt - example file can be found in root folder:
https://github.com/PolarHare/MathLogic/blob/master/toProof.txt

Result will be written in file resultProof.txt (example can be found in root of repository):
https://github.com/PolarHare/MathLogic/blob/master/resultProof.txt